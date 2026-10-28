package mz.agrogest.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mz.agrogest.model.Carrinho;
import mz.agrogest.model.ItemCarrinho;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.model.VendaItem;
import mz.agrogest.repo.DataStore;

/**
 * O coração da parte comercial: verificar o stock, finalizar compras e emitir facturas.
 *
 * A finalização da compra é uma "transação lógica". Como não temos base de dados com transações,
 * garantimos o "tudo ou nada" desta forma: dentro de um bloco synchronized, primeiro verificamos o
 * stock de TODOS os produtos do carrinho e só se estiver tudo bem é que começamos a descontar.
 * Como essa segunda fase já não tem nada que possa falhar, nunca fica uma venda a meio.
 */
public class VendaService {

    private final DataStore store;

    public VendaService(DataStore store) {
        this.store = store;
    }

    /**
     * Verifica se todos os produtos do carrinho ainda podem ser vendidos. Devolve uma mensagem por cada
     * problema encontrado; se a lista vier vazia, a compra pode avançar.
     * Usamos sempre o produto atual do DataStore, e não o que está guardado no carrinho, porque o stock
     * pode ter mudado desde que o cliente adicionou o produto.
     */
    public List<String> verificarDisponibilidade(Carrinho carrinho) {
        List<String> problemas = new ArrayList<>();
        for (ItemCarrinho item : carrinho.getItens()) {
            Optional<Produto> atual = store.produto(item.getProduto().getId());
            if (atual.isEmpty()) {
                problemas.add("\"" + item.getProduto().getNome() + "\" já não existe no catálogo.");
            } else if (atual.get().isExpirado()) {
                problemas.add("\"" + atual.get().getNome() + "\" está fora da validade.");
            } else if (atual.get().getQuantidade() < item.getQuantidade()) {
                Produto p = atual.get();
                problemas.add("\"" + p.getNome() + "\": pediu " + item.getQuantidade() + " " + p.getUnidade()
                        + ", mas só há " + p.getQuantidade() + " em stock.");
            }
        }
        return problemas;
    }

    /** Finaliza a compra: desconta o stock de todos os produtos e cria a venda (factura). */
    public Venda finalizarCompra(Utilizador cliente, Carrinho carrinho) {
        if (carrinho.isVazio()) {
            throw new NegocioException("O carrinho está vazio.");
        }
        Venda venda;
        synchronized (store) {
            // Fase 1: só validar. Se houver algum problema, a exceção sai daqui e nada foi alterado.
            List<String> problemas = verificarDisponibilidade(carrinho);
            if (!problemas.isEmpty()) {
                throw new NegocioException("Compra não efetuada: produtos indisponíveis.", problemas);
            }
            // Fase 2: descontar o stock e criar a venda. Daqui para a frente já não há nada que possa
            // falhar, por isso ou a compra fica completa ou nem sequer começou.
            venda = new Venda();
            for (ItemCarrinho item : carrinho.getItens()) {
                Produto p = store.produto(item.getProduto().getId()).orElseThrow();
                p.setQuantidade(p.getQuantidade() - item.getQuantidade());
                venda.getItens().add(new VendaItem(p.getId(), p.getNome(), p.getUnidade(),
                        item.getQuantidade(), p.getPreco()));
            }
            preencherCliente(venda, cliente);
            venda.setData(LocalDateTime.now());
            venda.setId(store.proximoIdVenda());
            venda.setNumeroFactura(numeroFactura(venda));
            store.adicionar(venda);
        }
        // O carrinho só é esvaziado depois de a venda estar registada.
        carrinho.esvaziar();
        return venda;
    }

    /**
     * Devolve uma factura, respeitando a regra c) do enunciado: o cliente só vê as suas e o administrador vê todas.
     */
    public Venda obterFactura(Utilizador u, int vendaId) {
        Venda v = store.venda(vendaId).orElseThrow(() -> new NegocioException("Factura não encontrada."));
        if (!u.isAdmin() && v.getClienteId() != u.getId()) {
            throw new AcessoNegadoException("Esta factura pertence a outro cliente.");
        }
        return v;
    }

    /** Histórico de compras de um cliente, da mais recente para a mais antiga. */
    public List<Venda> comprasDoCliente(int clienteId) {
        return store.vendas().stream()
                .filter(v -> v.getClienteId() == clienteId)
                .sorted(Comparator.comparing(Venda::getData).reversed())
                .collect(Collectors.toList());
    }

    /** Soma do total (com IVA) de todas as vendas, mostrada no painel. */
    public BigDecimal totalFacturado() {
        return store.vendas().stream().map(Venda::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Copia para a venda os dados do cliente nesse momento: nome, email, telefone e morada. */
    static void preencherCliente(Venda v, Utilizador c) {
        v.setClienteId(c.getId());
        v.setClienteNome(c.getNome());
        v.setClienteEmail(c.getEmail());
        v.setClienteTelefone(c.getTelefone());
        v.setClienteEndereco(c.getEndereco());
    }

    /** Número da factura no formato FT-ano-sequencial, por exemplo FT-2026-1. */
    static String numeroFactura(Venda v) {
        return "FT-" + v.getData().getYear() + "-" + v.getId();
    }
}

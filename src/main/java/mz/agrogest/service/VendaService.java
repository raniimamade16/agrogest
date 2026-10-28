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
 * Vendas e facturação. A finalização da compra é uma transação lógica:
 * verifica o stock de TODOS os itens e só depois desconta — tudo ou nada —
 * dentro de um bloco {@code synchronized} no DataStore.
 */
public class VendaService {

    private final DataStore store;

    public VendaService(DataStore store) {
        this.store = store;
    }

    /**
     * Lista os problemas que impedem a compra (produto removido, expirado ou stock insuficiente).
     * Lista vazia = pode comprar.
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

    public Venda finalizarCompra(Utilizador cliente, Carrinho carrinho) {
        if (carrinho.isVazio()) {
            throw new NegocioException("O carrinho está vazio.");
        }
        Venda venda;
        synchronized (store) {
            // Fase 1: validar tudo (nenhuma alteração ainda)
            List<String> problemas = verificarDisponibilidade(carrinho);
            if (!problemas.isEmpty()) {
                throw new NegocioException("Compra não efetuada: produtos indisponíveis.", problemas);
            }
            // Fase 2: descontar stock e criar a venda (já não há validações que possam falhar)
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
        carrinho.esvaziar();
        return venda;
    }

    /** Factura: o cliente só vê as suas; o admin vê todas (RN c). */
    public Venda obterFactura(Utilizador u, int vendaId) {
        Venda v = store.venda(vendaId).orElseThrow(() -> new NegocioException("Factura não encontrada."));
        if (!u.isAdmin() && v.getClienteId() != u.getId()) {
            throw new AcessoNegadoException("Esta factura pertence a outro cliente.");
        }
        return v;
    }

    public List<Venda> comprasDoCliente(int clienteId) {
        return store.vendas().stream()
                .filter(v -> v.getClienteId() == clienteId)
                .sorted(Comparator.comparing(Venda::getData).reversed())
                .collect(Collectors.toList());
    }

    public BigDecimal totalFacturado() {
        return store.vendas().stream().map(Venda::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    static void preencherCliente(Venda v, Utilizador c) {
        v.setClienteId(c.getId());
        v.setClienteNome(c.getNome());
        v.setClienteEmail(c.getEmail());
        v.setClienteTelefone(c.getTelefone());
        v.setClienteEndereco(c.getEndereco());
    }

    /** FT-&lt;ano&gt;-&lt;sequencial&gt;, ex.: FT-2026-1 */
    static String numeroFactura(Venda v) {
        return "FT-" + v.getData().getYear() + "-" + v.getId();
    }
}

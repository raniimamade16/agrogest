package mz.agrogest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.agrogest.model.Categoria;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.Ordenacao;

/** Regras de negócio dos fornecedores e dos produtos (o catálogo). */
public class CatalogoService {

    private final DataStore store;

    public CatalogoService(DataStore store) {
        this.store = store;
    }

    // Fornecedores

    public Fornecedor criarFornecedor(String nome, TipoFornecedor tipo, String contacto, String provincia) {
        Fornecedor f = new Fornecedor(store.proximoIdFornecedor(), nome, tipo, contacto, provincia);
        store.adicionar(f);
        return f;
    }

    /**
     * Só deixamos remover um fornecedor sem produtos. Caso contrário ficavam produtos
     * a apontar para um fornecedor que já não existe.
     */
    public void removerFornecedor(int id) {
        synchronized (store) {
            long nProdutos = store.produtos().stream().filter(p -> p.getFornecedorId() == id).count();
            if (nProdutos > 0) {
                throw new NegocioException("Não é possível remover: o fornecedor tem " + nProdutos
                        + " produto(s) associado(s). Remova-os primeiro.");
            }
            if (!store.removerFornecedor(id)) {
                throw new NegocioException("Fornecedor não encontrado.");
            }
        }
    }

    /** Mapa id -> fornecedor. As JSP usam-no para mostrar o nome do fornecedor de cada produto. */
    public Map<Integer, Fornecedor> fornecedoresPorId() {
        return store.fornecedores().stream().collect(Collectors.toMap(Fornecedor::getId, Function.identity()));
    }

    // Produtos

    /**
     * Cria um produto depois de confirmar que o fornecedor existe e que o preço e a quantidade fazem sentido.
     */
    public Produto criarProduto(String nome, Categoria categoria, int fornecedorId, BigDecimal preco,
                                int quantidade, String unidade, String descricao, String foto, LocalDate validade) {
        if (store.fornecedor(fornecedorId).isEmpty()) {
            throw new NegocioException("Fornecedor inexistente.");
        }
        validarPreco(preco);
        if (quantidade < 0) {
            throw new NegocioException("A quantidade não pode ser negativa.");
        }
        Produto p = new Produto(store.proximoIdProduto(), nome, categoria, fornecedorId, preco,
                quantidade, unidade, descricao, foto, validade);
        store.adicionar(p);
        return p;
    }

    /**
     * Repõe stock (soma a quantidade indicada à que já existe) e, se for indicado, muda o preço.
     *
     * Corre dentro do mesmo synchronized que as vendas. Sem isso, uma reposição e uma venda ao
     * mesmo tempo podiam ler o mesmo valor de stock e uma das duas alterações perdia-se.
     */
    public void atualizarProduto(int id, int reposicao, BigDecimal novoPreco) {
        if (reposicao < 0) {
            throw new NegocioException("A reposição de stock não pode ser negativa.");
        }
        synchronized (store) {
            Produto p = obterProduto(id);
            if (novoPreco != null) {
                validarPreco(novoPreco);
                p.setPreco(novoPreco);
            }
            p.setQuantidade(p.getQuantidade() + reposicao);
        }
    }

    /**
     * Remove um produto. As facturas antigas não são afetadas, porque guardam uma cópia do nome e do preço.
     */
    public void removerProduto(int id) {
        synchronized (store) {
            if (!store.removerProduto(id)) {
                throw new NegocioException("Produto não encontrado.");
            }
        }
    }

    public Produto obterProduto(int id) {
        return store.produto(id).orElseThrow(() -> new NegocioException("Produto não encontrado."));
    }

    /**
     * Produtos que o cliente pode comprar: com stock, dentro da validade e, se for escolhida,
     * de uma só categoria. Vêm ordenados por nome.
     */
    public List<Produto> catalogo(Categoria categoria) {
        return store.produtos().stream()
                .filter(p -> p.getQuantidade() > 0 && !p.isExpirado())
                .filter(p -> categoria == null || p.getCategoria() == categoria)
                .sorted(Ordenacao.porTexto(Produto::getNome))
                .collect(Collectors.toList());
    }

    private static void validarPreco(BigDecimal preco) {
        if (preco.signum() <= 0) {
            throw new NegocioException("O preço deve ser maior que zero.");
        }
    }
}

package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carrinho de compras do cliente, guardado na sessão HTTP.
 *
 * Usamos um LinkedHashMap com o id do produto como chave. Assim, se o cliente adicionar o
 * mesmo produto duas vezes, as quantidades somam-se em vez de aparecerem duas linhas, e a
 * ordem em que os produtos foram adicionados mantém-se na página.
 *
 * Os métodos são synchronized porque o mesmo cliente pode ter dois separadores abertos e
 * fazer dois pedidos ao mesmo tempo sobre a mesma sessão.
 */
public class Carrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, ItemCarrinho> itens = new LinkedHashMap<>();

    public synchronized void adicionar(Produto p, int quantidade) {
        ItemCarrinho item = itens.get(p.getId());
        if (item == null) {
            itens.put(p.getId(), new ItemCarrinho(p, quantidade));
        } else {
            item.setProduto(p);
            item.setQuantidade(item.getQuantidade() + quantidade);
        }
    }

    /** Muda a quantidade de um produto. Uma quantidade de 0 (ou menos) retira-o do carrinho. */
    public synchronized void atualizar(int produtoId, int quantidade) {
        if (quantidade <= 0) {
            itens.remove(produtoId);
        } else if (itens.containsKey(produtoId)) {
            itens.get(produtoId).setQuantidade(quantidade);
        }
    }

    public synchronized void remover(int produtoId) {
        itens.remove(produtoId);
    }

    public synchronized void esvaziar() {
        itens.clear();
    }

    public synchronized int quantidadeDe(int produtoId) {
        ItemCarrinho item = itens.get(produtoId);
        return item == null ? 0 : item.getQuantidade();
    }

    public synchronized List<ItemCarrinho> getItens() {
        return new ArrayList<>(itens.values());
    }

    public synchronized boolean isVazio() {
        return itens.isEmpty();
    }

    public synchronized int getTotalItens() {
        return itens.size();
    }

    public synchronized BigDecimal getSubtotal() {
        return itens.values().stream().map(ItemCarrinho::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getIva() {
        return getSubtotal().multiply(Venda.TAXA_IVA).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getIva());
    }
}

package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Uma linha do carrinho: o produto e a quantidade que o cliente escolheu.
 * Só existe na sessão HTTP; desaparece quando a compra é concluída ou a sessão termina.
 */
public class ItemCarrinho implements Serializable {
    private static final long serialVersionUID = 1L;

    private Produto produto;
    private int quantidade;

    public ItemCarrinho() {
    }

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public BigDecimal getSubtotal() {
        return produto.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}

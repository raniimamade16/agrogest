package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Artigo que o agricultor tem em armazém e vende aos clientes.
 *
 * Em vez de guardar o objeto Fornecedor completo, guardamos só o fornecedorId, como se faria
 * com uma chave estrangeira numa base de dados. Assim não duplicamos dados e, se o nome do
 * fornecedor mudar, todos os produtos mostram logo o nome novo.
 */
public class Produto implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private Categoria categoria;
    private int fornecedorId;
    // Usamos BigDecimal, e não double, para os valores em dinheiro não terem erros de arredondamento.
    private BigDecimal preco;
    private int quantidade;
    private String unidade;
    private String descricao;
    // Caminho dentro da aplicação (img/produtos/milho.jpg) ou um endereço completo da internet.
    private String foto;
    // Pode ficar vazia (null) em produtos que não se estragam, como ferramentas e máquinas.
    private LocalDate validade;

    public Produto() {
    }

    public Produto(int id, String nome, Categoria categoria, int fornecedorId, BigDecimal preco,
                   int quantidade, String unidade, String descricao, String foto, LocalDate validade) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.fornecedorId = fornecedorId;
        this.preco = preco;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.descricao = descricao;
        this.foto = foto;
        this.validade = validade;
    }

    public boolean isEmStock() {
        return quantidade > 0;
    }

    /** Um produto fora da validade deixa de aparecer no catálogo e já não pode ser vendido. */
    public boolean isExpirado() {
        return validade != null && validade.isBefore(LocalDate.now());
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public int getFornecedorId() { return fornecedorId; }
    public void setFornecedorId(int fornecedorId) { this.fornecedorId = fornecedorId; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDate getValidade() { return validade; }
    public void setValidade(LocalDate validade) { this.validade = validade; }
}

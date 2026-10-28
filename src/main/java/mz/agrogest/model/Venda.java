package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Uma compra feita por um cliente. Cada venda é também a sua factura.
 *
 * O subtotal, o IVA e o total não são guardados: são calculados a partir das linhas (itens).
 * Desta forma é impossível os totais ficarem diferentes da soma dos produtos.
 */
public class Venda implements Serializable {
    private static final long serialVersionUID = 1L;

    // Taxa de IVA aplicada em Moçambique.
    public static final BigDecimal TAXA_IVA = new BigDecimal("0.16");

    private int id;
    private String numeroFactura;
    private LocalDateTime data;
    private int clienteId;
    // Dados do cliente copiados no momento da compra. Uma factura é um documento fixo:
    // se o cliente mudar de morada mais tarde, as facturas antigas não devem mudar.
    private String clienteNome;
    private String clienteEmail;
    private String clienteTelefone;
    private String clienteEndereco;
    private List<VendaItem> itens = new ArrayList<>();

    public Venda() {
    }

    public BigDecimal getSubtotal() {
        return itens.stream().map(VendaItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** IVA de 16% sobre o subtotal, arredondado aos centavos como numa factura real. */
    public BigDecimal getIva() {
        return getSubtotal().multiply(TAXA_IVA).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(getIva());
    }

    public int getTotalUnidades() {
        return itens.stream().mapToInt(VendaItem::getQuantidade).sum();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }
    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }
    public String getClienteNome() { return clienteNome; }
    public void setClienteNome(String clienteNome) { this.clienteNome = clienteNome; }
    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }
    public String getClienteTelefone() { return clienteTelefone; }
    public void setClienteTelefone(String clienteTelefone) { this.clienteTelefone = clienteTelefone; }
    public String getClienteEndereco() { return clienteEndereco; }
    public void setClienteEndereco(String clienteEndereco) { this.clienteEndereco = clienteEndereco; }
    public List<VendaItem> getItens() { return itens; }
    public void setItens(List<VendaItem> itens) { this.itens = itens; }
}

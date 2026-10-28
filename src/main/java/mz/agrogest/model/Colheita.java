package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Registo de uma colheita de um cultivo: data, quilos colhidos e preço de referência por quilo. */
public class Colheita implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int cultivoId;
    private LocalDate data;
    private BigDecimal quantidadeKg;
    private BigDecimal precoKg;

    public Colheita() {
    }

    public Colheita(int id, int cultivoId, LocalDate data, BigDecimal quantidadeKg, BigDecimal precoKg) {
        this.id = id;
        this.cultivoId = cultivoId;
        this.data = data;
        this.quantidadeKg = quantidadeKg;
        this.precoKg = precoKg;
    }

    /**
     * O valor total não é guardado: é calculado sempre que é pedido (kg x preço por kg).
     * Assim nunca fica desatualizado se alguém corrigir a quantidade ou o preço.
     */
    public BigDecimal getValorTotal() {
        return quantidadeKg.multiply(precoKg);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCultivoId() { return cultivoId; }
    public void setCultivoId(int cultivoId) { this.cultivoId = cultivoId; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public BigDecimal getQuantidadeKg() { return quantidadeKg; }
    public void setQuantidadeKg(BigDecimal quantidadeKg) { this.quantidadeKg = quantidadeKg; }
    public BigDecimal getPrecoKg() { return precoKg; }
    public void setPrecoKg(BigDecimal precoKg) { this.precoKg = precoKg; }
}

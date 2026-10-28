package mz.agrogest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Linha dos relatórios: junta um cliente com os totais das suas compras.
 * Não é gravado em lado nenhum; é calculado na hora pelo RelatorioService.
 */
public class ResumoCliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Utilizador cliente;
    private final int numeroCompras;
    private final int unidades;
    private final BigDecimal totalGasto;
    private final LocalDateTime ultimaCompra;

    public ResumoCliente(Utilizador cliente, int numeroCompras, int unidades, BigDecimal totalGasto,
                         LocalDateTime ultimaCompra) {
        this.cliente = cliente;
        this.numeroCompras = numeroCompras;
        this.unidades = unidades;
        this.totalGasto = totalGasto;
        this.ultimaCompra = ultimaCompra;
    }

    public Utilizador getCliente() { return cliente; }
    public int getNumeroCompras() { return numeroCompras; }
    public int getUnidades() { return unidades; }
    public BigDecimal getTotalGasto() { return totalGasto; }
    public LocalDateTime getUltimaCompra() { return ultimaCompra; }
}

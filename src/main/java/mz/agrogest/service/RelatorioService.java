package mz.agrogest.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import mz.agrogest.model.Perfil;
import mz.agrogest.model.ResumoCliente;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.Ordenacao;

/** Relatórios de clientes e vendas. */
public class RelatorioService {

    private final DataStore store;

    public RelatorioService(DataStore store) {
        this.store = store;
    }

    public List<Utilizador> clientes() {
        return store.utilizadores().stream().filter(u -> u.getPerfil() == Perfil.CLIENTE)
                .collect(Collectors.toList());
    }

    /** Clientes ordenados por nome (crescente). */
    public List<Utilizador> clientesPorNome() {
        List<Utilizador> l = clientes();
        l.sort(Ordenacao.porTexto(Utilizador::getNome));
        return l;
    }

    /** Resumo de compras de todos os clientes (inclui os que não compraram). */
    public List<ResumoCliente> resumos() {
        Map<Integer, List<Venda>> porCliente = store.vendas().stream()
                .collect(Collectors.groupingBy(Venda::getClienteId));
        return clientes().stream().map(c -> {
            List<Venda> vs = porCliente.getOrDefault(c.getId(), List.of());
            BigDecimal total = vs.stream().map(Venda::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            int unidades = vs.stream().mapToInt(Venda::getTotalUnidades).sum();
            return new ResumoCliente(c, vs.size(), unidades, total,
                    vs.stream().map(Venda::getData).max(Comparator.naturalOrder()).orElse(null));
        }).collect(Collectors.toList());
    }

    /** Clientes que compraram, ordenados por volume (valor total gasto, decrescente). */
    public List<ResumoCliente> clientesQueCompraram() {
        return resumos().stream().filter(r -> r.getNumeroCompras() > 0)
                .sorted(Comparator.comparing(ResumoCliente::getTotalGasto).reversed())
                .collect(Collectors.toList());
    }

    /** Ranking por número de compras (desempate pelo valor gasto). */
    public List<ResumoCliente> clientesMaisAssiduos() {
        return resumos().stream().filter(r -> r.getNumeroCompras() > 0)
                .sorted(Comparator.comparingInt(ResumoCliente::getNumeroCompras)
                        .thenComparing(ResumoCliente::getTotalGasto).reversed())
                .collect(Collectors.toList());
    }

    public List<Utilizador> clientesSemCompras() {
        return resumos().stream().filter(r -> r.getNumeroCompras() == 0).map(ResumoCliente::getCliente)
                .sorted(Ordenacao.porTexto(Utilizador::getNome))
                .collect(Collectors.toList());
    }
}

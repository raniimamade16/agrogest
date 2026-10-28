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

/**
 * Calcula os relatórios de clientes pedidos no enunciado.
 * Nada disto é guardado: os valores são calculados a partir das vendas sempre que a página é aberta,
 * por isso estão sempre atualizados.
 */
public class RelatorioService {

    private final DataStore store;

    public RelatorioService(DataStore store) {
        this.store = store;
    }

    /** Todos os clientes (a conta de administrador fica de fora). */
    public List<Utilizador> clientes() {
        return store.utilizadores().stream().filter(u -> u.getPerfil() == Perfil.CLIENTE)
                .collect(Collectors.toList());
    }

    /** Clientes por ordem alfabética do nome. */
    public List<Utilizador> clientesPorNome() {
        List<Utilizador> l = clientes();
        l.sort(Ordenacao.porTexto(Utilizador::getNome));
        return l;
    }

    /**
     * Para cada cliente, junta as suas vendas e calcula o número de compras, as unidades compradas
     * e o total gasto. Quem nunca comprou também aparece, com zeros.
     */
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

    /** Só os clientes que já compraram, de quem gastou mais para quem gastou menos (volume de compras). */
    public List<ResumoCliente> clientesQueCompraram() {
        return resumos().stream().filter(r -> r.getNumeroCompras() > 0)
                .sorted(Comparator.comparing(ResumoCliente::getTotalGasto).reversed())
                .collect(Collectors.toList());
    }

    /** Clientes mais assíduos, pelo número de compras. Em caso de empate fica à frente quem gastou mais. */
    public List<ResumoCliente> clientesMaisAssiduos() {
        return resumos().stream().filter(r -> r.getNumeroCompras() > 0)
                .sorted(Comparator.comparingInt(ResumoCliente::getNumeroCompras)
                        .thenComparing(ResumoCliente::getTotalGasto).reversed())
                .collect(Collectors.toList());
    }

    /** Clientes registados que ainda não fizeram nenhuma compra (o relatório extra do enunciado). */
    public List<Utilizador> clientesSemCompras() {
        return resumos().stream().filter(r -> r.getNumeroCompras() == 0).map(ResumoCliente::getCliente)
                .sorted(Ordenacao.porTexto(Utilizador::getNome))
                .collect(Collectors.toList());
    }
}

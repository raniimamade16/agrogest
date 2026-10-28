package mz.agrogest.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.repo.DataStore;

/** Campanhas agrícolas, cultivos e colheitas. */
public class AgricolaService {

    private final DataStore store;

    public AgricolaService(DataStore store) {
        this.store = store;
    }

    // ---------- campanhas ----------

    public Campanha criarCampanha(String nome, LocalDate inicio, LocalDate fim) {
        if (fim.isBefore(inicio)) {
            throw new NegocioException("A data de fim não pode ser anterior à data de início.");
        }
        Campanha c = new Campanha(store.proximoIdCampanha(), nome, inicio, fim);
        store.adicionar(c);
        return c;
    }

    public void definirFechada(int id, boolean fechada) {
        synchronized (store) {
            obterCampanha(id).setFechada(fechada);
        }
    }

    public void removerCampanha(int id) {
        synchronized (store) {
            long n = store.cultivos().stream().filter(c -> c.getCampanhaId() == id).count();
            if (n > 0) {
                throw new NegocioException("Não é possível remover a campanha: tem " + n + " cultivo(s) associado(s).");
            }
            if (!store.removerCampanha(id)) {
                throw new NegocioException("Campanha não encontrada.");
            }
        }
    }

    public Campanha obterCampanha(int id) {
        return store.campanha(id).orElseThrow(() -> new NegocioException("Campanha não encontrada."));
    }

    public Map<Integer, Campanha> campanhasPorId() {
        return store.campanhas().stream().collect(Collectors.toMap(Campanha::getId, Function.identity()));
    }

    // ---------- cultivos ----------

    public Cultivo criarCultivo(int campanhaId, String cultura, String variedade, BigDecimal hectares,
                                LocalDate plantio) {
        if (hectares.signum() <= 0) {
            throw new NegocioException("A área (hectares) deve ser maior que zero.");
        }
        synchronized (store) {
            Campanha c = obterCampanha(campanhaId);
            if (c.isFechada()) {
                throw new NegocioException("A campanha \"" + c.getNome() + "\" está fechada. Reabra-a para registar cultivos.");
            }
            Cultivo cultivo = new Cultivo(store.proximoIdCultivo(), campanhaId, cultura, variedade, hectares, plantio);
            store.adicionar(cultivo);
            return cultivo;
        }
    }

    public void removerCultivo(int id) {
        synchronized (store) {
            long n = store.colheitas().stream().filter(c -> c.getCultivoId() == id).count();
            if (n > 0) {
                throw new NegocioException("Não é possível remover o cultivo: tem " + n + " colheita(s) associada(s).");
            }
            if (!store.removerCultivo(id)) {
                throw new NegocioException("Cultivo não encontrado.");
            }
        }
    }

    public Map<Integer, Cultivo> cultivosPorId() {
        return store.cultivos().stream().collect(Collectors.toMap(Cultivo::getId, Function.identity()));
    }

    // ---------- colheitas ----------

    public Colheita registarColheita(int cultivoId, LocalDate data, BigDecimal kg, BigDecimal precoKg) {
        if (kg.signum() <= 0 || precoKg.signum() <= 0) {
            throw new NegocioException("A quantidade e o preço por kg devem ser maiores que zero.");
        }
        synchronized (store) {
            Cultivo cultivo = store.cultivo(cultivoId)
                    .orElseThrow(() -> new NegocioException("Cultivo não encontrado."));
            if (data.isBefore(cultivo.getDataPlantio())) {
                throw new NegocioException("A data da colheita não pode ser anterior à data de plantio.");
            }
            Colheita c = new Colheita(store.proximoIdColheita(), cultivoId, data, kg, precoKg);
            store.adicionar(c);
            return c;
        }
    }

    public void removerColheita(int id) {
        if (!store.removerColheita(id)) {
            throw new NegocioException("Colheita não encontrada.");
        }
    }

    public BigDecimal valorTotalColheitas() {
        return store.colheitas().stream().map(Colheita::getValorTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

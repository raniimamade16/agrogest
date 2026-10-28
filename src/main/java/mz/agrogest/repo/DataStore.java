package mz.agrogest.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;

/**
 * Camada de persistência em memória (singleton). Todas as listas são
 * {@link Collections#synchronizedList} e os IDs são gerados por {@link AtomicInteger}.
 *
 * Operações compostas (ler-validar-escrever) devem ser feitas dentro de
 * {@code synchronized (DataStore.get())} — é esse o "lock" global das transações lógicas.
 */
public final class DataStore {

    private static final DataStore INSTANCE = new DataStore();

    public static DataStore get() {
        return INSTANCE;
    }

    private final List<Utilizador> utilizadores = Collections.synchronizedList(new ArrayList<>());
    private final List<Fornecedor> fornecedores = Collections.synchronizedList(new ArrayList<>());
    private final List<Produto> produtos = Collections.synchronizedList(new ArrayList<>());
    private final List<Campanha> campanhas = Collections.synchronizedList(new ArrayList<>());
    private final List<Cultivo> cultivos = Collections.synchronizedList(new ArrayList<>());
    private final List<Colheita> colheitas = Collections.synchronizedList(new ArrayList<>());
    private final List<Venda> vendas = Collections.synchronizedList(new ArrayList<>());

    private final AtomicInteger seqUtilizador = new AtomicInteger();
    private final AtomicInteger seqFornecedor = new AtomicInteger();
    private final AtomicInteger seqProduto = new AtomicInteger();
    private final AtomicInteger seqCampanha = new AtomicInteger();
    private final AtomicInteger seqCultivo = new AtomicInteger();
    private final AtomicInteger seqColheita = new AtomicInteger();
    private final AtomicInteger seqVenda = new AtomicInteger();

    private DataStore() {
    }

    // ---------- geradores de ID ----------
    public int proximoIdUtilizador() { return seqUtilizador.incrementAndGet(); }
    public int proximoIdFornecedor() { return seqFornecedor.incrementAndGet(); }
    public int proximoIdProduto() { return seqProduto.incrementAndGet(); }
    public int proximoIdCampanha() { return seqCampanha.incrementAndGet(); }
    public int proximoIdCultivo() { return seqCultivo.incrementAndGet(); }
    public int proximoIdColheita() { return seqColheita.incrementAndGet(); }
    /** O id da venda é também o número sequencial da factura. */
    public int proximoIdVenda() { return seqVenda.incrementAndGet(); }

    // ---------- acesso às listas ----------
    public List<Utilizador> utilizadores() { return copia(utilizadores); }
    public List<Fornecedor> fornecedores() { return copia(fornecedores); }
    public List<Produto> produtos() { return copia(produtos); }
    public List<Campanha> campanhas() { return copia(campanhas); }
    public List<Cultivo> cultivos() { return copia(cultivos); }
    public List<Colheita> colheitas() { return copia(colheitas); }
    public List<Venda> vendas() { return copia(vendas); }

    public Optional<Utilizador> utilizador(int id) { return procurar(utilizadores, u -> u.getId() == id); }
    public Optional<Fornecedor> fornecedor(int id) { return procurar(fornecedores, f -> f.getId() == id); }
    public Optional<Produto> produto(int id) { return procurar(produtos, p -> p.getId() == id); }
    public Optional<Campanha> campanha(int id) { return procurar(campanhas, c -> c.getId() == id); }
    public Optional<Cultivo> cultivo(int id) { return procurar(cultivos, c -> c.getId() == id); }
    public Optional<Colheita> colheita(int id) { return procurar(colheitas, c -> c.getId() == id); }
    public Optional<Venda> venda(int id) { return procurar(vendas, v -> v.getId() == id); }

    public Optional<Utilizador> utilizadorPorEmail(String email) {
        return procurar(utilizadores, u -> u.getEmail().equalsIgnoreCase(email));
    }

    public void adicionar(Utilizador u) { utilizadores.add(u); }
    public void adicionar(Fornecedor f) { fornecedores.add(f); }
    public void adicionar(Produto p) { produtos.add(p); }
    public void adicionar(Campanha c) { campanhas.add(c); }
    public void adicionar(Cultivo c) { cultivos.add(c); }
    public void adicionar(Colheita c) { colheitas.add(c); }
    public void adicionar(Venda v) { vendas.add(v); }

    public boolean removerFornecedor(int id) { return fornecedores.removeIf(f -> f.getId() == id); }
    public boolean removerProduto(int id) { return produtos.removeIf(p -> p.getId() == id); }
    public boolean removerCampanha(int id) { return campanhas.removeIf(c -> c.getId() == id); }
    public boolean removerCultivo(int id) { return cultivos.removeIf(c -> c.getId() == id); }
    public boolean removerColheita(int id) { return colheitas.removeIf(c -> c.getId() == id); }

    // ---------- snapshot (serialização) ----------

    /** Fotografia consistente de todo o estado, para gravar em ficheiro. */
    public synchronized Snapshot snapshot() {
        Snapshot s = new Snapshot();
        s.utilizadores = copia(utilizadores);
        s.fornecedores = copia(fornecedores);
        s.produtos = copia(produtos);
        s.campanhas = copia(campanhas);
        s.cultivos = copia(cultivos);
        s.colheitas = copia(colheitas);
        s.vendas = copia(vendas);
        s.seqUtilizador = seqUtilizador.get();
        s.seqFornecedor = seqFornecedor.get();
        s.seqProduto = seqProduto.get();
        s.seqCampanha = seqCampanha.get();
        s.seqCultivo = seqCultivo.get();
        s.seqColheita = seqColheita.get();
        s.seqVenda = seqVenda.get();
        return s;
    }

    /** Substitui todo o estado em memória pelo conteúdo do snapshot. */
    public synchronized void restaurar(Snapshot s) {
        limpar();
        utilizadores.addAll(s.utilizadores);
        fornecedores.addAll(s.fornecedores);
        produtos.addAll(s.produtos);
        campanhas.addAll(s.campanhas);
        cultivos.addAll(s.cultivos);
        colheitas.addAll(s.colheitas);
        vendas.addAll(s.vendas);
        seqUtilizador.set(s.seqUtilizador);
        seqFornecedor.set(s.seqFornecedor);
        seqProduto.set(s.seqProduto);
        seqCampanha.set(s.seqCampanha);
        seqCultivo.set(s.seqCultivo);
        seqColheita.set(s.seqColheita);
        seqVenda.set(s.seqVenda);
    }

    public synchronized void limpar() {
        utilizadores.clear();
        fornecedores.clear();
        produtos.clear();
        campanhas.clear();
        cultivos.clear();
        colheitas.clear();
        vendas.clear();
        for (AtomicInteger seq : List.of(seqUtilizador, seqFornecedor, seqProduto, seqCampanha,
                seqCultivo, seqColheita, seqVenda)) {
            seq.set(0);
        }
    }

    // ---------- utilitários ----------

    private static <T> List<T> copia(List<T> lista) {
        synchronized (lista) {
            return new ArrayList<>(lista);
        }
    }

    private static <T> Optional<T> procurar(List<T> lista, Predicate<T> criterio) {
        synchronized (lista) {
            return lista.stream().filter(criterio).findFirst();
        }
    }
}

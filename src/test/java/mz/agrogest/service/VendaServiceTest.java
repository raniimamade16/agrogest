package mz.agrogest.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Categoria;
import mz.agrogest.model.Produto;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.repo.DataStore;

class VendaServiceTest {

    private final DataStore store = DataStore.get();
    private final CatalogoService catalogo = new CatalogoService(store);
    private final VendaService vendas = new VendaService(store);
    private final AuthService auth = new AuthService(store);

    private Produto milho;
    private Produto adubo;
    private Utilizador cliente;

    @BeforeEach
    void preparar() {
        store.limpar();
        int f = catalogo.criarFornecedor("F", TipoFornecedor.SEMENTES, "84", "Nampula").getId();
        milho = catalogo.criarProduto("Milho", Categoria.SEMENTE, f, new BigDecimal("100"), 10, "kg", "", "", null);
        adubo = catalogo.criarProduto("Adubo", Categoria.FERTILIZANTE, f, new BigDecimal("2000"), 1, "saco", "", "", null);
        cliente = auth.registarCliente("João Silva", "joao@teste.mz", "1234", "1234", "84", "Nampula");
    }

    @Test
    void compraDescontaStockECalculaIva() {
        Carrinho c = new Carrinho();
        c.adicionar(milho, 3);
        c.adicionar(adubo, 1);

        Venda v = vendas.finalizarCompra(cliente, c);

        assertEquals(7, milho.getQuantidade());
        assertEquals(0, adubo.getQuantidade());
        assertEquals(0, new BigDecimal("2300").compareTo(v.getSubtotal()));
        assertEquals(0, new BigDecimal("368.00").compareTo(v.getIva()));
        assertEquals(0, new BigDecimal("2668.00").compareTo(v.getTotal()));
        assertEquals("FT-" + v.getData().getYear() + "-1", v.getNumeroFactura());
        assertEquals(true, c.isVazio());
    }

    @Test
    void stockInsuficienteNumItemNaoDescontaNenhum() {
        Carrinho c = new Carrinho();
        c.adicionar(milho, 3);
        c.adicionar(adubo, 5); // só há 1

        NegocioException e = assertThrows(NegocioException.class, () -> vendas.finalizarCompra(cliente, c));

        assertEquals(1, e.getDetalhes().size());
        assertEquals(10, milho.getQuantidade(), "tudo ou nada: o milho não pode ter sido descontado");
        assertEquals(1, adubo.getQuantidade());
        assertEquals(0, store.vendas().size());
    }

    @Test
    void comprasConcorrentesNuncaVendemMaisQueOStock() throws Exception {
        int threads = 50; // 50 clientes tentam comprar 1 kg cada; só há 10 kg
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch partida = new CountDownLatch(1);
        List<Future<Boolean>> resultados = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            resultados.add(pool.submit(() -> {
                Carrinho c = new Carrinho();
                c.adicionar(milho, 1);
                partida.await();
                try {
                    vendas.finalizarCompra(cliente, c);
                    return true;
                } catch (NegocioException e) {
                    return false;
                }
            }));
        }
        partida.countDown();
        int sucesso = 0;
        for (Future<Boolean> f : resultados) {
            sucesso += f.get() ? 1 : 0;
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(10, sucesso);
        assertEquals(0, milho.getQuantidade());
        assertEquals(10, store.vendas().size());
    }

    @Test
    void clienteNaoVeFacturaDeOutro() {
        Carrinho c = new Carrinho();
        c.adicionar(milho, 1);
        Venda v = vendas.finalizarCompra(cliente, c);
        Utilizador outro = auth.registarCliente("Ana", "ana@teste.mz", "1234", "1234", "84", "Nacala");

        assertThrows(AcessoNegadoException.class, () -> vendas.obterFactura(outro, v.getId()));
    }

    @Test
    void emailDeRegistoEUnico() {
        assertThrows(NegocioException.class,
                () -> auth.registarCliente("Outro", "JOAO@teste.mz", "1234", "1234", "84", "Nampula"));
    }
}

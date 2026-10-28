package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.ResumoCliente;
import mz.agrogest.model.Venda;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Ordenacao;

/**
 * Listagens do administrador, todas na mesma página, com separadores.
 * O tipo de listagem e a ordenação vêm no endereço, por exemplo /admin/listagens?tipo=produtos&ordem=preco&dir=desc.
 * Para cada tipo existe um mapa "nome do campo -> Comparator", o que torna fácil acrescentar novas colunas ordenáveis.
 */
@WebServlet("/admin/listagens")
public class ListagensServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    private static final Map<String, Comparator<Produto>> ORDEM_PRODUTOS = new LinkedHashMap<>();
    private static final Map<String, Comparator<Fornecedor>> ORDEM_FORNECEDORES = new LinkedHashMap<>();
    private static final Map<String, Comparator<ResumoCliente>> ORDEM_CLIENTES = new LinkedHashMap<>();
    private static final Map<String, Comparator<Venda>> ORDEM_COMPRAS = new LinkedHashMap<>();

    static {
        ORDEM_PRODUTOS.put("nome", Ordenacao.porTexto(Produto::getNome));
        ORDEM_PRODUTOS.put("categoria", Comparator.comparing(Produto::getCategoria));
        ORDEM_PRODUTOS.put("preco", Comparator.comparing(Produto::getPreco));
        ORDEM_PRODUTOS.put("stock", Comparator.comparingInt(Produto::getQuantidade));
        ORDEM_PRODUTOS.put("valor", Comparator.comparing(ListagensServlet::valorEmStock));
        ORDEM_PRODUTOS.put("data", Comparator.comparing(Produto::getValidade,
                Comparator.nullsLast(Comparator.<LocalDate>naturalOrder())));

        ORDEM_FORNECEDORES.put("nome", Ordenacao.porTexto(Fornecedor::getNome));
        ORDEM_FORNECEDORES.put("tipo", Comparator.comparing(Fornecedor::getTipo));
        ORDEM_FORNECEDORES.put("provincia", Ordenacao.porTexto(Fornecedor::getProvincia));

        ORDEM_CLIENTES.put("nome", Ordenacao.porTexto(r -> r.getCliente().getNome()));
        ORDEM_CLIENTES.put("data", Comparator.comparing(r -> r.getCliente().getDataRegisto()));
        ORDEM_CLIENTES.put("compras", Comparator.comparingInt(ResumoCliente::getNumeroCompras));
        ORDEM_CLIENTES.put("valor", Comparator.comparing(ResumoCliente::getTotalGasto));

        ORDEM_COMPRAS.put("data", Comparator.comparing(Venda::getData));
        ORDEM_COMPRAS.put("valor", Comparator.comparing(Venda::getTotal));
        ORDEM_COMPRAS.put("nome", Ordenacao.porTexto(Venda::getClienteNome));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String tipo = req.getParameter("tipo");
        String ordem = req.getParameter("ordem");
        String dir = "desc".equals(req.getParameter("dir")) ? "desc" : "asc";
        if (tipo == null) {
            tipo = "produtos";
        }

        String usado;
        switch (tipo) {
            case "fornecedores" -> {
                List<Fornecedor> l = store.fornecedores();
                usado = Ordenacao.ordenar(l, ordem, dir, ORDEM_FORNECEDORES, "nome");
                req.setAttribute("lista", l);
            }
            case "clientes" -> {
                List<ResumoCliente> l = relatorioService.resumos();
                usado = Ordenacao.ordenar(l, ordem, dir, ORDEM_CLIENTES, "nome");
                req.setAttribute("lista", l);
            }
            case "compras" -> {
                List<Venda> l = store.vendas();
                if (ordem == null) {
                    dir = "desc"; // se não for pedida nenhuma ordem, mostramos as compras mais recentes primeiro
                }
                usado = Ordenacao.ordenar(l, ordem, dir, ORDEM_COMPRAS, "data");
                req.setAttribute("lista", l);
                req.setAttribute("totalGeral", l.stream().map(Venda::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            case "armazem", "produtos" -> {
                List<Produto> l = store.produtos();
                if (tipo.equals("armazem")) {
                    l = l.stream().filter(p -> p.getQuantidade() > 0).collect(Collectors.toList());
                    req.setAttribute("valorArmazem", l.stream().map(ListagensServlet::valorEmStock)
                            .reduce(BigDecimal.ZERO, BigDecimal::add));
                }
                usado = Ordenacao.ordenar(l, ordem, dir, ORDEM_PRODUTOS, "nome");
                req.setAttribute("lista", l);
                req.setAttribute("fornecedores", catalogoService.fornecedoresPorId());
            }
            default -> {
                res.sendError(HttpServletResponse.SC_NOT_FOUND, "Listagem desconhecida.");
                return;
            }
        }
        req.setAttribute("tipo", tipo);
        req.setAttribute("ordem", usado);
        req.setAttribute("dir", dir);
        render(req, res, "admin/listagens");
    }

    /** Valor do stock de um produto (preço x quantidade), usado no separador Armazém. */
    private static BigDecimal valorEmStock(Produto p) {
        return p.getPreco().multiply(BigDecimal.valueOf(p.getQuantidade()));
    }
}

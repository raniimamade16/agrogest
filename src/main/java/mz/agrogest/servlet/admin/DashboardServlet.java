package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.util.Comparator;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Venda;
import mz.agrogest.servlet.BaseServlet;

@WebServlet({"/admin", "/admin/", "/admin/dashboard"})
public class DashboardServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    /** Abaixo deste stock o produto aparece no alerta do dashboard. */
    private static final int STOCK_BAIXO = 20;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("nFornecedores", store.fornecedores().size());
        req.setAttribute("nProdutos", store.produtos().size());
        req.setAttribute("nClientes", relatorioService.clientes().size());
        req.setAttribute("nVendas", store.vendas().size());
        req.setAttribute("nCampanhas", store.campanhas().size());
        req.setAttribute("totalFacturado", vendaService.totalFacturado());
        req.setAttribute("valorColheitas", agricolaService.valorTotalColheitas());
        req.setAttribute("stockBaixo", store.produtos().stream()
                .filter(p -> p.getQuantidade() < STOCK_BAIXO)
                .sorted(Comparator.comparingInt(p -> p.getQuantidade()))
                .collect(Collectors.toList()));
        req.setAttribute("ultimasVendas", store.vendas().stream()
                .sorted(Comparator.comparing(Venda::getData).reversed())
                .limit(5).collect(Collectors.toList()));
        req.setAttribute("stockMinimo", STOCK_BAIXO);
        render(req, res, "admin/dashboard");
    }
}

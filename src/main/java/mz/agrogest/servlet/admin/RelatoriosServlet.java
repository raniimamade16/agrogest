package mz.agrogest.servlet.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.servlet.BaseServlet;

@WebServlet("/admin/relatorios")
public class RelatoriosServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("porNome", relatorioService.clientesPorNome());
        req.setAttribute("compraram", relatorioService.clientesQueCompraram());
        req.setAttribute("assiduos", relatorioService.clientesMaisAssiduos());
        req.setAttribute("semCompras", relatorioService.clientesSemCompras());
        render(req, res, "admin/relatorios");
    }
}

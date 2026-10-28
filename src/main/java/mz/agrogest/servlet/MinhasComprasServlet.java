package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Histórico de compras do cliente com sessão iniciada. */
@WebServlet("/minhas-compras")
public class MinhasComprasServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("vendas", vendaService.comprasDoCliente(utilizador(req).getId()));
        render(req, res, "minhas-compras");
    }
}

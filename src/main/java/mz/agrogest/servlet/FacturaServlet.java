package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.service.AcessoNegadoException;
import mz.agrogest.service.NegocioException;
import mz.agrogest.util.Params;

/** Mostra uma factura. Se for de outro cliente responde 403; se não existir, 404. */
@WebServlet("/factura")
public class FacturaServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            req.setAttribute("venda", vendaService.obterFactura(utilizador(req), Params.inteiro(req, "id", -1)));
            render(req, res, "factura");
        } catch (AcessoNegadoException e) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (NegocioException e) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}

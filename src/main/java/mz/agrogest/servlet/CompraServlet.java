package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Venda;
import mz.agrogest.service.NegocioException;
import mz.agrogest.util.Flash;

/** Checkout: GET mostra o resumo para confirmação; POST confirma e emite a factura. */
@WebServlet("/compra")
public class CompraServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Carrinho c = carrinho(req);
        if (c.isVazio()) {
            Flash.erro(req, "O carrinho está vazio. Adicione produtos antes de finalizar a compra.");
            redirect(req, res, "/catalogo");
            return;
        }
        req.setAttribute("problemas", vendaService.verificarDisponibilidade(c));
        render(req, res, "compra");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        try {
            Venda v = vendaService.finalizarCompra(utilizador(req), carrinho(req));
            Flash.sucesso(req, "Compra confirmada! Factura " + v.getNumeroFactura() + " emitida.");
            redirect(req, res, "/factura?id=" + v.getId());
        } catch (NegocioException e) {
            Flash.erro(req, e.getMessage(), e.getDetalhes());
            redirect(req, res, "/carrinho");
        }
    }
}

package mz.agrogest.servlet.admin;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Params;

/** Ativa ou desativa contas de clientes. A lista de clientes está em /admin/listagens?tipo=clientes. */
@WebServlet("/admin/clientes")
public class ClientesServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        executar(req, res, "/admin/listagens?tipo=clientes", () -> {
            authService.alternarAtivo(Params.inteiro(req, "id", "Cliente"));
            return "Estado da conta atualizado.";
        });
    }
}

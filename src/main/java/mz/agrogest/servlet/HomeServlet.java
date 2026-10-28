package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Utilizador;

/** Raiz da aplicação: encaminha cada perfil para a sua página inicial. */
@WebServlet("")
public class HomeServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Utilizador u = utilizador(req);
        redirect(req, res, u == null ? "/login" : u.isAdmin() ? "/admin/dashboard" : "/catalogo");
    }
}

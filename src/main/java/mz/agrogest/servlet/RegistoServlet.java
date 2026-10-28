package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.filter.AuthFilter;
import mz.agrogest.model.Utilizador;
import mz.agrogest.service.NegocioException;
import mz.agrogest.util.Flash;
import mz.agrogest.util.Params;

/** Auto-registo de clientes. Se correr bem, o cliente fica logo com a sessão iniciada. */
@WebServlet("/registo")
public class RegistoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        render(req, res, "registo");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        try {
            Utilizador u = authService.registarCliente(
                    Params.texto(req, "nome", "Nome"),
                    Params.texto(req, "email", "Email"),
                    Params.texto(req, "password", "Palavra-passe"),
                    Params.opcional(req, "confirmacao"),
                    Params.texto(req, "telefone", "Telefone"),
                    Params.texto(req, "endereco", "Endereço"));
            req.getSession();
            req.changeSessionId(); // só funciona com uma sessão já criada, por isso a criámos na linha anterior
            req.getSession().setAttribute(AuthFilter.UTILIZADOR, u);
            Flash.sucesso(req, "Bem-vindo(a), " + u.getNome() + "! A sua conta foi criada.");
            redirect(req, res, "/catalogo");
        } catch (NegocioException e) {
            req.setAttribute("erro", e.getMessage());
            render(req, res, "registo"); // voltamos ao formulário com o erro; a JSP volta a preencher os campos
        }
    }
}

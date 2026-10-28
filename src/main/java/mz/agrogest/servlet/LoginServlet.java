package mz.agrogest.servlet;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mz.agrogest.filter.AuthFilter;
import mz.agrogest.model.Utilizador;

/** Página de login. Valida as credenciais, guarda o utilizador na sessão e envia-o para a sua área. */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        if (utilizador(req) != null) {
            redirect(req, res, "/");
            return;
        }
        render(req, res, "login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String email = req.getParameter("email");
        Optional<Utilizador> u = authService.autenticar(email, req.getParameter("password"));
        if (u.isEmpty() || !u.get().isAtivo()) {
            req.setAttribute("erro", u.isEmpty() ? "Email ou palavra-passe incorretos." : "Conta desativada. Contacte o administrador.");
            req.setAttribute("email", email);
            render(req, res, "login");
            return;
        }
        HttpSession sessao = req.getSession();
        String destino = (String) sessao.getAttribute("destinoAposLogin");
        sessao.removeAttribute("destinoAposLogin");
        req.changeSessionId(); // trocamos o id da sessão ao entrar, para evitar o ataque de "session fixation"
        sessao.setAttribute(AuthFilter.UTILIZADOR, u.get());

        // Se a pessoa tentou abrir uma página antes de entrar, voltamos a levá-la lá.
        // Só aceitamos caminhos desta aplicação, e um cliente nunca é enviado para /admin.
        boolean destinoValido = destino != null && destino.startsWith(req.getContextPath() + "/")
                && (u.get().isAdmin() || !destino.startsWith(req.getContextPath() + "/admin"));
        if (destinoValido) {
            res.sendRedirect(destino);
        } else {
            redirect(req, res, u.get().isAdmin() ? "/admin/dashboard" : "/catalogo");
        }
    }
}

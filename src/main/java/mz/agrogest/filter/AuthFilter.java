package mz.agrogest.filter;

import java.io.IOException;
import java.util.Set;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mz.agrogest.model.Utilizador;

/**
 * Controlo de acesso por sessão e perfil.
 * <ul>
 *   <li>/admin/* — só ADMIN (sem sessão → login; cliente → HTTP 403);</li>
 *   <li>/catalogo, /carrinho/*, /compra, /factura, /minhas-compras — exigem sessão;</li>
 *   <li>/carrinho/* e /compra — só CLIENTE (o admin não compra).</li>
 * </ul>
 */
@WebFilter("/*")
public class AuthFilter extends HttpFilter {
    private static final long serialVersionUID = 1L;

    public static final String UTILIZADOR = "utilizador";

    private static final Set<String> COM_SESSAO = Set.of("/catalogo", "/carrinho", "/compra", "/factura", "/minhas-compras");
    private static final Set<String> SO_CLIENTE = Set.of("/carrinho", "/compra");

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String caminho = req.getRequestURI().substring(req.getContextPath().length());
        String raiz = raiz(caminho);
        HttpSession sessao = req.getSession(false);
        Utilizador u = sessao == null ? null : (Utilizador) sessao.getAttribute(UTILIZADOR);

        boolean admin = raiz.equals("/admin");
        if ((admin || COM_SESSAO.contains(raiz)) && u == null) {
            String destino = req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : "");
            req.getSession().setAttribute("destinoAposLogin", destino);
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        if (admin && !u.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Área reservada ao administrador.");
            return;
        }
        if (SO_CLIENTE.contains(raiz) && u.isAdmin()) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "O administrador não efetua compras.");
            return;
        }
        chain.doFilter(req, res);
    }

    /** "/carrinho/adicionar" → "/carrinho" */
    private static String raiz(String caminho) {
        int i = caminho.indexOf('/', 1);
        return i < 0 ? caminho : caminho.substring(0, i);
    }
}

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
 * Filtro que corre antes de qualquer servlet e decide quem pode abrir o quê
 * (regras d) e e) do enunciado):
 * - /admin/... é só para o administrador. Sem sessão vai para o login; um cliente recebe HTTP 403.
 * - /catalogo, /carrinho/..., /compra, /factura e /minhas-compras exigem sessão iniciada.
 * - /carrinho/... e /compra são só para clientes, porque o administrador não faz compras.
 * Tudo o resto (login, registo, CSS, imagens) é público.
 */
@WebFilter("/*")
public class AuthFilter extends HttpFilter {
    private static final long serialVersionUID = 1L;

    /** Nome do atributo da sessão onde fica guardado o utilizador autenticado. */
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
            // Guardamos a página pedida para voltar a ela depois do login.
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

    /** Fica só com a primeira parte do caminho: "/carrinho/adicionar" passa a "/carrinho". */
    private static String raiz(String caminho) {
        int i = caminho.indexOf('/', 1);
        return i < 0 ? caminho : caminho.substring(0, i);
    }
}

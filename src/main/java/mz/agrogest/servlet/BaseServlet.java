package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mz.agrogest.filter.AuthFilter;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Utilizador;
import mz.agrogest.repo.DataStore;
import mz.agrogest.service.AgricolaService;
import mz.agrogest.service.AuthService;
import mz.agrogest.service.CatalogoService;
import mz.agrogest.service.NegocioException;
import mz.agrogest.service.RelatorioService;
import mz.agrogest.service.VendaService;
import mz.agrogest.util.Flash;

/** Base dos controladores: acesso aos serviços e utilitários de navegação. */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String CARRINHO = "carrinho";

    protected final transient DataStore store = DataStore.get();
    protected final transient AuthService authService = new AuthService(store);
    protected final transient CatalogoService catalogoService = new CatalogoService(store);
    protected final transient AgricolaService agricolaService = new AgricolaService(store);
    protected final transient VendaService vendaService = new VendaService(store);
    protected final transient RelatorioService relatorioService = new RelatorioService(store);

    /** Uma ação de escrita que devolve a mensagem de sucesso. */
    @FunctionalInterface
    protected interface Acao {
        String executar();
    }

    /** Encaminha para /WEB-INF/views/{vista}.jsp (as vistas não são acessíveis diretamente). */
    protected void render(HttpServletRequest req, HttpServletResponse res, String vista)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + vista + ".jsp").forward(req, res);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse res, String caminho) throws IOException {
        res.sendRedirect(req.getContextPath() + caminho);
    }

    /** Post/Redirect/Get: executa a ação, guarda a mensagem (sucesso ou erro) e redireciona. */
    protected void executar(HttpServletRequest req, HttpServletResponse res, String destino, Acao acao)
            throws IOException {
        try {
            Flash.sucesso(req, acao.executar());
        } catch (NegocioException e) {
            Flash.erro(req, e.getMessage(), e.getDetalhes());
        }
        redirect(req, res, destino);
    }

    protected Utilizador utilizador(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (Utilizador) s.getAttribute(AuthFilter.UTILIZADOR);
    }

    protected Carrinho carrinho(HttpServletRequest req) {
        HttpSession s = req.getSession();
        Carrinho c = (Carrinho) s.getAttribute(CARRINHO);
        if (c == null) {
            c = new Carrinho();
            s.setAttribute(CARRINHO, c);
        }
        return c;
    }
}

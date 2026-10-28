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

/**
 * Classe mãe de todos os servlets, que são os controladores do padrão MVC.
 *
 * Junta o que todos precisam: acesso aos serviços, um método para mostrar uma JSP, outro para
 * redirecionar e a lógica comum dos formulários (padrão Post/Redirect/Get). Assim, cada servlet
 * concreto fica pequeno e só trata do seu próprio caso.
 */
public abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public static final String CARRINHO = "carrinho";

    // Os serviços não guardam estado próprio (está tudo no DataStore), por isso podem ser partilhados
    // por todos os pedidos. São transient porque um servlet é Serializable e estes objetos não precisam de o ser.
    protected final transient DataStore store = DataStore.get();
    protected final transient AuthService authService = new AuthService(store);
    protected final transient CatalogoService catalogoService = new CatalogoService(store);
    protected final transient AgricolaService agricolaService = new AgricolaService(store);
    protected final transient VendaService vendaService = new VendaService(store);
    protected final transient RelatorioService relatorioService = new RelatorioService(store);

    /** Uma ação de escrita (criar, remover, ...) que devolve a mensagem de sucesso a mostrar ao utilizador. */
    @FunctionalInterface
    protected interface Acao {
        String executar();
    }

    /**
     * Mostra a JSP /WEB-INF/views/{vista}.jsp. As JSP estão dentro de WEB-INF para não poderem
     * ser abertas diretamente no navegador: só se chega a elas passando por um servlet.
     */
    protected void render(HttpServletRequest req, HttpServletResponse res, String vista)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + vista + ".jsp").forward(req, res);
    }

    /** Redireciona para outra página da aplicação (o contexto, por exemplo /agrogest, é acrescentado aqui). */
    protected void redirect(HttpServletRequest req, HttpServletResponse res, String caminho) throws IOException {
        res.sendRedirect(req.getContextPath() + caminho);
    }

    /**
     * Padrão Post/Redirect/Get: executa a ação, guarda na sessão a mensagem de sucesso ou de erro
     * e redireciona para uma página GET. Assim, se o utilizador carregar em F5, o navegador não volta
     * a enviar o formulário (o que, por exemplo, criaria um produto repetido).
     */
    protected void executar(HttpServletRequest req, HttpServletResponse res, String destino, Acao acao)
            throws IOException {
        try {
            Flash.sucesso(req, acao.executar());
        } catch (NegocioException e) {
            Flash.erro(req, e.getMessage(), e.getDetalhes());
        }
        redirect(req, res, destino);
    }

    /** Utilizador com sessão iniciada, ou null se ainda ninguém entrou. */
    protected Utilizador utilizador(HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        return s == null ? null : (Utilizador) s.getAttribute(AuthFilter.UTILIZADOR);
    }

    /** Carrinho do cliente guardado na sessão. É criado na primeira vez que é pedido. */
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

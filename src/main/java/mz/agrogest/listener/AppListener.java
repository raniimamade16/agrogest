package mz.agrogest.listener;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import mz.agrogest.repo.DataStore;
import mz.agrogest.repo.Persistencia;
import mz.agrogest.service.AuthService;
import mz.agrogest.util.DadosIniciais;

/**
 * Ciclo de vida da aplicação:
 * arranque → restaura o estado do ficheiro (ou carrega dados de demonstração) e garante a conta admin;
 * encerramento do Tomcat → grava automaticamente o estado.
 */
@WebListener
public class AppListener implements ServletContextListener {

    public static final String PERSISTENCIA = "persistencia";
    private static final Logger LOG = Logger.getLogger(AppListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        Persistencia persistencia = new Persistencia(pastaDados(ctx));
        ctx.setAttribute(PERSISTENCIA, persistencia);
        // muda a cada arranque/deploy: as JSP acrescentam ?v=... ao CSS para o navegador não usar a cópia antiga
        ctx.setAttribute("versao", Long.toString(System.currentTimeMillis(), 36));

        DataStore store = DataStore.get();
        if (!persistencia.carregar(store)) {
            LOG.info("Sem ficheiro de dados: a carregar dados de demonstração.");
            DadosIniciais.popular(store);
        }
        new AuthService(store).garantirAdmin();
        LOG.info("AgroGest iniciado. Ficheiro de dados: " + persistencia.getFicheiro());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Persistencia persistencia = (Persistencia) sce.getServletContext().getAttribute(PERSISTENCIA);
        try {
            persistencia.gravar(DataStore.get());
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Falha ao gravar o estado no encerramento", e);
        }
    }

    /**
     * Pasta dos dados, por ordem de prioridade: propriedade de sistema {@code agrogest.dados},
     * context-param {@code agrogest.dados}, {@code $CATALINA_BASE/agrogest-dados}, {@code ~/agrogest-dados}.
     */
    private static Path pastaDados(ServletContext ctx) {
        String p = System.getProperty("agrogest.dados");
        if (p == null || p.isBlank()) {
            p = ctx.getInitParameter("agrogest.dados");
        }
        if (p == null || p.isBlank()) {
            String base = System.getProperty("catalina.base", System.getProperty("user.home"));
            p = Path.of(base, "agrogest-dados").toString();
        }
        return Path.of(p);
    }
}

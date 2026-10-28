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
 * Corre quando a aplicação arranca e quando pára (é um ServletContextListener).
 * - No arranque: tenta restaurar os dados do ficheiro .ser. Se ainda não houver ficheiro (primeira vez),
 *   carrega os dados de demonstração. Garante também que a conta de administrador existe.
 * - Quando o Tomcat encerra: grava automaticamente todo o estado no ficheiro.
 */
@WebListener
public class AppListener implements ServletContextListener {

    /** Nome do atributo onde fica o objeto Persistencia, para o PersistenciaServlet o poder usar. */
    public static final String PERSISTENCIA = "persistencia";
    private static final Logger LOG = Logger.getLogger(AppListener.class.getName());

    /** Chamado uma vez, quando o Tomcat arranca a aplicação. */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        Persistencia persistencia = new Persistencia(pastaDados(ctx));
        ctx.setAttribute(PERSISTENCIA, persistencia);
        // Número de versão que muda a cada arranque. As JSP juntam-no ao endereço do CSS (style.css?v=...)
        // para o navegador ir buscar a versão nova depois de cada atualização.
        ctx.setAttribute("versao", Long.toString(System.currentTimeMillis(), 36));

        DataStore store = DataStore.get();
        if (!persistencia.carregar(store)) {
            LOG.info("Sem ficheiro de dados: a carregar dados de demonstração.");
            DadosIniciais.popular(store);
        }
        new AuthService(store).garantirAdmin();
        LOG.info("AgroGest iniciado. Ficheiro de dados: " + persistencia.getFicheiro());
    }

    /** Chamado quando o Tomcat encerra (ou a aplicação é republicada): gravamos tudo antes de sair. */
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
     * Decide em que pasta fica o ficheiro de dados, por esta ordem:
     * 1. a propriedade de sistema -Dagrogest.dados (usada no serviço systemd);
     * 2. o context-param agrogest.dados do web.xml;
     * 3. a pasta agrogest-dados dentro da instalação do Tomcat (ou, em último caso, na pasta pessoal).
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

package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.listener.AppListener;
import mz.agrogest.repo.Persistencia;
import mz.agrogest.service.NegocioException;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Params;

/** Endpoint pedido no enunciado para gravar, restaurar ou eliminar manualmente o ficheiro de dados. */
@WebServlet("/admin/persistencia")
public class PersistenciaServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Persistencia p = persistencia();
        req.setAttribute("ficheiro", p.getFicheiro().toAbsolutePath());
        req.setAttribute("existe", p.existe());
        req.setAttribute("tamanho", p.tamanhoBytes());
        req.setAttribute("ultimaGravacao", p.ultimaGravacao()
                .map(i -> LocalDateTime.ofInstant(i, ZoneId.systemDefault())).orElse(null));
        render(req, res, "admin/persistencia");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        Persistencia p = persistencia();
        executar(req, res, "/admin/persistencia", () -> {
            try {
                return switch (Params.opcional(req, "acao")) {
                    case "gravar" -> {
                        p.gravar(store);
                        yield "Estado gravado com sucesso.";
                    }
                    case "restaurar" -> {
                        if (!p.carregar(store)) {
                            throw new NegocioException("Não existe ficheiro válido para restaurar.");
                        }
                        authService.garantirAdmin();
                        yield "Estado restaurado a partir do ficheiro.";
                    }
                    case "eliminar" -> {
                        yield p.eliminar()
                                ? "Ficheiro eliminado. Os dados em memória mantêm-se até o Tomcat encerrar (que volta a gravar)."
                                : "Não havia ficheiro para eliminar.";
                    }
                    default -> throw new NegocioException("Ação desconhecida.");
                };
            } catch (IOException e) {
                throw new NegocioException("Erro de E/S: " + e.getMessage());
            }
        });
    }

    /**
     * O objeto Persistencia é criado pelo AppListener no arranque e fica guardado no contexto da aplicação.
     */
    private Persistencia persistencia() {
        return (Persistencia) getServletContext().getAttribute(AppListener.PERSISTENCIA);
    }
}

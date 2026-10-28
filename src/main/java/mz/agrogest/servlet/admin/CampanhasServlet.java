package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Campanha;
import mz.agrogest.model.Cultivo;
import mz.agrogest.service.NegocioException;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Params;

/** Gestão de campanhas: criar, fechar, reabrir e remover. */
@WebServlet("/admin/campanhas")
public class CampanhasServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        var campanhas = store.campanhas();
        campanhas.sort(Comparator.comparing(Campanha::getInicio).reversed());
        // Contamos os cultivos de cada campanha para a JSP desativar o botão Remover quando há cultivos.
        Map<Integer, Long> nCultivos = store.cultivos().stream()
                .collect(Collectors.groupingBy(Cultivo::getCampanhaId, Collectors.counting()));
        req.setAttribute("campanhas", campanhas);
        req.setAttribute("nCultivos", nCultivos);
        render(req, res, "admin/campanhas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        executar(req, res, "/admin/campanhas", () -> switch (Params.opcional(req, "acao")) {
            case "criar" -> {
                Campanha c = agricolaService.criarCampanha(
                        Params.texto(req, "nome", "Nome"),
                        Params.data(req, "inicio", "Data de início"),
                        Params.data(req, "fim", "Data de fim"));
                yield "Campanha \"" + c.getNome() + "\" criada.";
            }
            case "fechar" -> {
                agricolaService.definirFechada(Params.inteiro(req, "id", "Campanha"), true);
                yield "Campanha fechada.";
            }
            case "reabrir" -> {
                agricolaService.definirFechada(Params.inteiro(req, "id", "Campanha"), false);
                yield "Campanha reaberta.";
            }
            case "remover" -> {
                agricolaService.removerCampanha(Params.inteiro(req, "id", "Campanha"));
                yield "Campanha removida.";
            }
            default -> throw new NegocioException("Ação desconhecida.");
        });
    }
}

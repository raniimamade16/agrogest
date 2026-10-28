package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.service.NegocioException;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Params;

@WebServlet("/admin/cultivos")
public class CultivosServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        int filtro = Params.inteiro(req, "campanhaId", 0);
        var cultivos = store.cultivos().stream()
                .filter(c -> filtro == 0 || c.getCampanhaId() == filtro)
                .sorted(Comparator.comparing(Cultivo::getDataPlantio).reversed())
                .collect(Collectors.toList());
        Map<Integer, Long> nColheitas = store.colheitas().stream()
                .collect(Collectors.groupingBy(Colheita::getCultivoId, Collectors.counting()));
        req.setAttribute("cultivos", cultivos);
        req.setAttribute("nColheitas", nColheitas);
        req.setAttribute("campanhas", store.campanhas());
        req.setAttribute("campanhasPorId", agricolaService.campanhasPorId());
        req.setAttribute("filtro", filtro);
        render(req, res, "admin/cultivos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        executar(req, res, "/admin/cultivos", () -> switch (Params.opcional(req, "acao")) {
            case "criar" -> {
                Cultivo c = agricolaService.criarCultivo(
                        Params.inteiro(req, "campanhaId", "Campanha"),
                        Params.texto(req, "cultura", "Cultura"),
                        Params.texto(req, "variedade", "Variedade"),
                        Params.decimal(req, "hectares", "Hectares"),
                        Params.data(req, "plantio", "Data de plantio"));
                yield "Cultivo \"" + c.getCultura() + "\" registado.";
            }
            case "remover" -> {
                agricolaService.removerCultivo(Params.inteiro(req, "id", "Cultivo"));
                yield "Cultivo removido.";
            }
            default -> throw new NegocioException("Ação desconhecida.");
        });
    }
}

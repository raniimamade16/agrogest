package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Colheita;
import mz.agrogest.service.NegocioException;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Formatos;
import mz.agrogest.util.Params;

/** Registo de colheitas. O valor total (kg x preço por kg) é calculado pelo próprio modelo Colheita. */
@WebServlet("/admin/colheitas")
public class ColheitasServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        var colheitas = store.colheitas().stream()
                .sorted(Comparator.comparing(Colheita::getData).reversed())
                .collect(Collectors.toList());
        req.setAttribute("colheitas", colheitas);
        req.setAttribute("totalKg", colheitas.stream().map(Colheita::getQuantidadeKg).reduce(BigDecimal.ZERO, BigDecimal::add));
        req.setAttribute("totalValor", agricolaService.valorTotalColheitas());
        req.setAttribute("cultivos", store.cultivos());
        req.setAttribute("cultivosPorId", agricolaService.cultivosPorId());
        req.setAttribute("campanhasPorId", agricolaService.campanhasPorId());
        render(req, res, "admin/colheitas");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        executar(req, res, "/admin/colheitas", () -> switch (Params.opcional(req, "acao")) {
            case "criar" -> {
                Colheita c = agricolaService.registarColheita(
                        Params.inteiro(req, "cultivoId", "Cultivo"),
                        Params.data(req, "data", "Data"),
                        Params.decimal(req, "kg", "Quantidade (kg)"),
                        Params.decimal(req, "precoKg", "Preço por kg"));
                yield "Colheita registada: " + Formatos.num(c.getQuantidadeKg()) + " kg × "
                        + Formatos.mt(c.getPrecoKg()) + " = " + Formatos.mt(c.getValorTotal()) + ".";
            }
            case "remover" -> {
                agricolaService.removerColheita(Params.inteiro(req, "id", "Colheita"));
                yield "Colheita removida.";
            }
            default -> throw new NegocioException("Ação desconhecida.");
        });
    }
}

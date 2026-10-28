package mz.agrogest.servlet.admin;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.service.NegocioException;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Ordenacao;
import mz.agrogest.util.Params;

@WebServlet("/admin/fornecedores")
public class FornecedoresServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        var lista = store.fornecedores();
        lista.sort(Ordenacao.porTexto(Fornecedor::getNome));
        req.setAttribute("fornecedores", lista);
        req.setAttribute("tipos", TipoFornecedor.values());
        render(req, res, "admin/fornecedores");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        executar(req, res, "/admin/fornecedores", () -> switch (Params.opcional(req, "acao")) {
            case "criar" -> {
                Fornecedor f = catalogoService.criarFornecedor(
                        Params.texto(req, "nome", "Nome"),
                        Params.enumeracao(req, "tipo", "Tipo", TipoFornecedor.class),
                        Params.texto(req, "contacto", "Contacto"),
                        Params.texto(req, "provincia", "Província"));
                yield "Fornecedor \"" + f.getNome() + "\" criado.";
            }
            case "remover" -> {
                catalogoService.removerFornecedor(Params.inteiro(req, "id", "Fornecedor"));
                yield "Fornecedor removido.";
            }
            default -> throw new NegocioException("Ação desconhecida.");
        });
    }
}

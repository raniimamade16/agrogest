package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Carrinho;
import mz.agrogest.model.Produto;
import mz.agrogest.service.NegocioException;
import mz.agrogest.util.Params;

/**
 * Gere o carrinho de compras, que fica guardado na sessão HTTP.
 * GET /carrinho mostra o carrinho. As alterações chegam por POST para /carrinho/adicionar,
 * /carrinho/atualizar, /carrinho/remover e /carrinho/esvaziar; o getPathInfo() diz-nos qual foi pedida.
 */
@WebServlet({"/carrinho", "/carrinho/*"})
public class CarrinhoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Carrinho c = carrinho(req);
        req.setAttribute("problemas", vendaService.verificarDisponibilidade(c));
        render(req, res, "carrinho");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String acao = req.getPathInfo() == null ? "" : req.getPathInfo();
        Carrinho c = carrinho(req);
        switch (acao) {
            case "/adicionar" -> executar(req, res, voltarPara(req), () -> {
                Produto p = catalogoService.obterProduto(Params.inteiro(req, "produtoId", "Produto"));
                int qtd = quantidadePositiva(req);
                // Regra a) do enunciado: somando o que já está no carrinho, não se pode passar o stock.
                int total = c.quantidadeDe(p.getId()) + qtd;
                if (total > p.getQuantidade()) {
                    throw new NegocioException("Indisponível: só há " + p.getQuantidade() + " " + p.getUnidade()
                            + " de \"" + p.getNome() + "\" em stock (no carrinho: " + c.quantidadeDe(p.getId()) + ").");
                }
                c.adicionar(p, qtd);
                return qtd + " " + p.getUnidade() + " de \"" + p.getNome() + "\" adicionado(s) ao carrinho.";
            });
            case "/atualizar" -> executar(req, res, "/carrinho", () -> {
                Produto p = catalogoService.obterProduto(Params.inteiro(req, "produtoId", "Produto"));
                int qtd = Params.inteiro(req, "quantidade", "Quantidade");
                if (qtd > p.getQuantidade()) {
                    throw new NegocioException("Indisponível: só há " + p.getQuantidade() + " " + p.getUnidade()
                            + " de \"" + p.getNome() + "\" em stock.");
                }
                c.atualizar(p.getId(), qtd);
                return qtd <= 0 ? "Item removido." : "Quantidade atualizada.";
            });
            case "/remover" -> executar(req, res, "/carrinho", () -> {
                c.remover(Params.inteiro(req, "produtoId", "Produto"));
                return "Item removido do carrinho.";
            });
            case "/esvaziar" -> executar(req, res, "/carrinho", () -> {
                c.esvaziar();
                return "Carrinho esvaziado.";
            });
            default -> res.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /** Lê a quantidade do formulário e recusa zero ou valores negativos. */
    private static int quantidadePositiva(HttpServletRequest req) {
        int qtd = Params.inteiro(req, "quantidade", "Quantidade");
        if (qtd <= 0) {
            throw new NegocioException("A quantidade deve ser maior que zero.");
        }
        return qtd;
    }

    /**
     * Depois de adicionar, volta ao catálogo mantendo a categoria que estava filtrada.
     * Só aceitamos letras maiúsculas, para ninguém conseguir usar este parâmetro para redirecionar para outro site.
     */
    private static String voltarPara(HttpServletRequest req) {
        String cat = req.getParameter("categoria");
        return cat == null || cat.isBlank() || !cat.matches("[A-Z]+") ? "/catalogo" : "/catalogo?categoria=" + cat;
    }
}

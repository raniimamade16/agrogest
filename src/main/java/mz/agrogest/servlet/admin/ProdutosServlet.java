package mz.agrogest.servlet.admin;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Categoria;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.service.NegocioException;
import mz.agrogest.servlet.BaseServlet;
import mz.agrogest.util.Ordenacao;
import mz.agrogest.util.Params;

/** Gestão de produtos: criar, repor stock ou alterar o preço, e remover. */
@WebServlet("/admin/produtos")
public class ProdutosServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        var produtos = store.produtos();
        produtos.sort(Ordenacao.porTexto(Produto::getNome));
        var fornecedores = store.fornecedores();
        fornecedores.sort(Ordenacao.porTexto(Fornecedor::getNome));
        req.setAttribute("produtos", produtos);
        req.setAttribute("listaFornecedores", fornecedores);
        req.setAttribute("fornecedores", catalogoService.fornecedoresPorId());
        req.setAttribute("categorias", Categoria.values());
        render(req, res, "admin/produtos");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        executar(req, res, "/admin/produtos", () -> switch (Params.opcional(req, "acao")) {
            case "criar" -> {
                String validade = Params.opcional(req, "validade");
                Produto p = catalogoService.criarProduto(
                        Params.texto(req, "nome", "Nome"),
                        Params.enumeracao(req, "categoria", "Categoria", Categoria.class),
                        Params.inteiro(req, "fornecedorId", "Fornecedor"),
                        Params.decimal(req, "preco", "Preço"),
                        Params.inteiro(req, "quantidade", "Quantidade"),
                        Params.texto(req, "unidade", "Unidade"),
                        Params.opcional(req, "descricao"),
                        Params.opcional(req, "foto"),
                        validade.isEmpty() ? null : Params.data(req, "validade", "Validade"));
                // Deixamos criar, mas avisamos: um produto já expirado não aparece no catálogo.
                if (p.getValidade() != null && p.getValidade().isBefore(LocalDate.now())) {
                    yield "Produto \"" + p.getNome() + "\" criado (atenção: a validade já expirou, não aparecerá no catálogo).";
                }
                yield "Produto \"" + p.getNome() + "\" criado.";
            }
            case "atualizar" -> {
                int id = Params.inteiro(req, "id", "Produto");
                int reposicao = Params.inteiro(req, "reposicao", 0);
                String preco = Params.opcional(req, "preco");
                BigDecimal novoPreco = preco.isEmpty() ? null : Params.decimal(req, "preco", "Preço");
                catalogoService.atualizarProduto(id, reposicao, novoPreco);
                Produto p = catalogoService.obterProduto(id);
                yield "\"" + p.getNome() + "\" atualizado: stock " + p.getQuantidade() + " " + p.getUnidade() + ".";
            }
            case "remover" -> {
                catalogoService.removerProduto(Params.inteiro(req, "id", "Produto"));
                yield "Produto removido.";
            }
            default -> throw new NegocioException("Ação desconhecida.");
        });
    }
}

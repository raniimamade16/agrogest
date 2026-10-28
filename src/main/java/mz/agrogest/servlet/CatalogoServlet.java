package mz.agrogest.servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mz.agrogest.model.Categoria;

@WebServlet("/catalogo")
public class CatalogoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Categoria categoria = null;
        try {
            String c = req.getParameter("categoria");
            categoria = c == null || c.isBlank() ? null : Categoria.valueOf(c);
        } catch (IllegalArgumentException ignorada) {
            // categoria desconhecida → mostra todas
        }
        req.setAttribute("categoriaAtual", categoria);
        req.setAttribute("categorias", Categoria.values());
        req.setAttribute("produtos", catalogoService.catalogo(categoria));
        req.setAttribute("fornecedores", catalogoService.fornecedoresPorId());
        render(req, res, "catalogo");
    }
}

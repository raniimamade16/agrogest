package mz.agrogest.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Diz ao navegador durante quanto tempo pode guardar os ficheiros estáticos.
 *
 * Encontrámos este problema nos testes: o Maven grava os ficheiros do WAR com a data fixa de 1980
 * e, sem cabeçalhos de cache, o navegador achava que o CSS antigo continuava válido durante anos
 * e não mostrava as alterações. Por isso:
 * - CSS e imagens levam "no-cache": o navegador pergunta sempre ao servidor se mudaram
 *   (quando não mudaram, a resposta é um 304, muito leve);
 * - as fontes ficam guardadas durante um ano, porque nunca mudam.
 */
@WebFilter({"/css/*", "/img/*", "/fonts/*"})
public class CacheFilter extends HttpFilter {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        boolean fonte = req.getServletPath().startsWith("/fonts/");
        res.setHeader("Cache-Control", fonte ? "public, max-age=31536000, immutable" : "no-cache");
        chain.doFilter(req, res);
    }
}

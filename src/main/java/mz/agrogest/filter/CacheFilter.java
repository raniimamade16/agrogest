package mz.agrogest.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Cabeçalhos de cache para os ficheiros estáticos.
 * O WAR é gerado com datas fixas (1980), por isso sem estes cabeçalhos o navegador
 * considerava o CSS antigo "fresco" durante anos e não via as novas versões.
 * <ul>
 *   <li>/css/* e /img/* — {@code no-cache}: o navegador revalida sempre (resposta 304 se não mudou);</li>
 *   <li>/fonts/* — guardadas durante um ano (os ficheiros de fontes nunca mudam).</li>
 * </ul>
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

package mz.agrogest.util;

import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Mensagens "flash": avisos de sucesso ou de erro que têm de sobreviver a um redirect.
 * Guardamo-las na sessão antes de redirecionar; o cabeçalho comum das JSP mostra-as uma
 * única vez e apaga-as logo a seguir.
 */
public final class Flash {

    public static final String ATRIBUTO = "flash";

    private Flash() {
    }

    public static void sucesso(HttpServletRequest req, String texto) {
        definir(req, "sucesso", texto, List.of());
    }

    public static void erro(HttpServletRequest req, String texto) {
        erro(req, texto, List.of());
    }

    public static void erro(HttpServletRequest req, String texto, List<String> detalhes) {
        definir(req, "erro", texto, detalhes);
    }

    private static void definir(HttpServletRequest req, String tipo, String texto, List<String> detalhes) {
        req.getSession().setAttribute(ATRIBUTO, Map.of("tipo", tipo, "texto", texto, "detalhes", detalhes));
    }
}

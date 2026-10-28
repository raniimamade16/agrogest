package mz.agrogest.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletRequest;
import mz.agrogest.service.NegocioException;

/** Leitura e validação de parâmetros de formulários. Lança {@link NegocioException} com mensagens claras. */
public final class Params {

    private Params() {
    }

    public static String texto(HttpServletRequest req, String nome, String rotulo) {
        String v = req.getParameter(nome);
        if (v == null || v.isBlank()) {
            throw new NegocioException("O campo \"" + rotulo + "\" é obrigatório.");
        }
        return v.trim();
    }

    public static String opcional(HttpServletRequest req, String nome) {
        String v = req.getParameter(nome);
        return v == null ? "" : v.trim();
    }

    public static int inteiro(HttpServletRequest req, String nome, String rotulo) {
        try {
            return Integer.parseInt(texto(req, nome, rotulo));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + rotulo + "\" deve ser um número inteiro.");
        }
    }

    /** Inteiro opcional; devolve {@code padrao} se ausente ou inválido. */
    public static int inteiro(HttpServletRequest req, String nome, int padrao) {
        try {
            return Integer.parseInt(req.getParameter(nome).trim());
        } catch (RuntimeException e) {
            return padrao;
        }
    }

    public static BigDecimal decimal(HttpServletRequest req, String nome, String rotulo) {
        try {
            return Formatos.dinheiro(texto(req, nome, rotulo));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + rotulo + "\" deve ser um número (ex.: 320 ou 22,50).");
        }
    }

    public static LocalDate data(HttpServletRequest req, String nome, String rotulo) {
        try {
            return LocalDate.parse(texto(req, nome, rotulo));
        } catch (DateTimeParseException e) {
            throw new NegocioException("O campo \"" + rotulo + "\" deve ser uma data válida.");
        }
    }

    public static <E extends Enum<E>> E enumeracao(HttpServletRequest req, String nome, String rotulo, Class<E> tipo) {
        try {
            return Enum.valueOf(tipo, texto(req, nome, rotulo));
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Valor inválido para \"" + rotulo + "\".");
        }
    }
}

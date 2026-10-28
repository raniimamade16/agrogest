package mz.agrogest.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import jakarta.servlet.http.HttpServletRequest;
import mz.agrogest.service.NegocioException;

/**
 * Lê e valida os campos que chegam dos formulários.
 * Quando um campo falta ou tem um valor inválido, lança uma NegocioException com uma mensagem
 * que o utilizador percebe (por exemplo, "O campo Preço é obrigatório"). Assim os servlets
 * não ficam cheios de ifs repetidos.
 */
public final class Params {

    private Params() {
    }

    /** Campo de texto obrigatório, sem os espaços à volta. */
    public static String texto(HttpServletRequest req, String nome, String rotulo) {
        String v = req.getParameter(nome);
        if (v == null || v.isBlank()) {
            throw new NegocioException("O campo \"" + rotulo + "\" é obrigatório.");
        }
        return v.trim();
    }

    /** Campo de texto opcional: devolve uma string vazia se não vier preenchido. */
    public static String opcional(HttpServletRequest req, String nome) {
        String v = req.getParameter(nome);
        return v == null ? "" : v.trim();
    }

    /** Número inteiro obrigatório. */
    public static int inteiro(HttpServletRequest req, String nome, String rotulo) {
        try {
            return Integer.parseInt(texto(req, nome, rotulo));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + rotulo + "\" deve ser um número inteiro.");
        }
    }

    /** Número inteiro opcional: se faltar ou for inválido, devolve o valor por omissão. */
    public static int inteiro(HttpServletRequest req, String nome, int padrao) {
        try {
            return Integer.parseInt(req.getParameter(nome).trim());
        } catch (RuntimeException e) {
            return padrao;
        }
    }

    /** Número decimal obrigatório (aceita vírgula ou ponto). */
    public static BigDecimal decimal(HttpServletRequest req, String nome, String rotulo) {
        try {
            return Formatos.dinheiro(texto(req, nome, rotulo));
        } catch (NumberFormatException e) {
            throw new NegocioException("O campo \"" + rotulo + "\" deve ser um número (ex.: 320 ou 22,50).");
        }
    }

    /** Data obrigatória, no formato enviado pelo <input type="date">. */
    public static LocalDate data(HttpServletRequest req, String nome, String rotulo) {
        try {
            return LocalDate.parse(texto(req, nome, rotulo));
        } catch (DateTimeParseException e) {
            throw new NegocioException("O campo \"" + rotulo + "\" deve ser uma data válida.");
        }
    }

    /** Valor de uma lista fechada (enum), como a categoria do produto ou o tipo de fornecedor. */
    public static <E extends Enum<E>> E enumeracao(HttpServletRequest req, String nome, String rotulo, Class<E> tipo) {
        try {
            return Enum.valueOf(tipo, texto(req, nome, rotulo));
        } catch (IllegalArgumentException e) {
            throw new NegocioException("Valor inválido para \"" + rotulo + "\".");
        }
    }
}

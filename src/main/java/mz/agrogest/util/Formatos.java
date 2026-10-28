package mz.agrogest.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/**
 * Formatação para as vistas. Expostas às JSP como funções EL
 * (ver /WEB-INF/agrogest.tld, prefixo {@code ag}).
 */
public final class Formatos {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private Formatos() {
    }

    private static DecimalFormat formato(String padrao) {
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator(',');
        s.setGroupingSeparator('.');
        return new DecimalFormat(padrao, s);
    }

    /** 70400 → "70.400,00 MT" */
    public static String mt(Object valor) {
        return valor == null ? "" : formato("#,##0.00").format(valor) + " MT";
    }

    /** 8.5 → "8,5" ; 3200 → "3.200" */
    public static String num(Object valor) {
        return valor == null ? "" : formato("#,##0.##").format(valor);
    }

    public static String data(Object t) {
        if (t instanceof LocalDateTime ldt) {
            return DATA.format(ldt);
        }
        return t instanceof Temporal tmp ? DATA.format(tmp) : "";
    }

    public static String dataHora(Object t) {
        return t instanceof LocalDateTime ldt ? DATA_HORA.format(ldt) : data(t);
    }

    /** Valor para {@code <input type="date">}. */
    public static String iso(Object t) {
        return t instanceof LocalDate ld ? ISO.format(ld) : "";
    }

    /** URL absoluto fica como está; caminho relativo é prefixado com o contexto da aplicação. */
    public static String foto(String url, String contexto) {
        if (url == null || url.isBlank()) {
            return contexto + "/img/sem-foto.svg";
        }
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
            return url;
        }
        return contexto + "/" + url;
    }

    public static BigDecimal dinheiro(String s) {
        return new BigDecimal(s.trim().replace(" ", "").replace(",", "."));
    }
}

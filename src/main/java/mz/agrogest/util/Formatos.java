package mz.agrogest.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/**
 * Funções de formatação usadas nas JSP: datas, dinheiro em meticais e números.
 * Estão registadas como funções EL no ficheiro /WEB-INF/agrogest.tld, por isso nas JSP escrevemos,
 * por exemplo, ${ag:mt(produto.preco)}. Precisámos delas porque a JSTL não sabe formatar LocalDate
 * e queríamos o formato usado em Moçambique: ponto nos milhares e vírgula nas décimas.
 */
public final class Formatos {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    private Formatos() {
    }

    /**
     * Cria o formatador com ponto nos milhares e vírgula nas décimas.
     * Criamos um novo de cada vez porque o DecimalFormat não pode ser partilhado entre threads.
     */
    private static DecimalFormat formato(String padrao) {
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setDecimalSeparator(',');
        s.setGroupingSeparator('.');
        return new DecimalFormat(padrao, s);
    }

    /** Formata dinheiro: 70400 fica "70.400,00 MT". */
    public static String mt(Object valor) {
        return valor == null ? "" : formato("#,##0.00").format(valor) + " MT";
    }

    /** Formata números sem casas decimais a mais: 8.5 fica "8,5" e 3200 fica "3.200". */
    public static String num(Object valor) {
        return valor == null ? "" : formato("#,##0.##").format(valor);
    }

    /** Data no formato dd/mm/aaaa. Aceita LocalDate e LocalDateTime. */
    public static String data(Object t) {
        if (t instanceof LocalDateTime ldt) {
            return DATA.format(ldt);
        }
        return t instanceof Temporal tmp ? DATA.format(tmp) : "";
    }

    /** Data e hora no formato dd/mm/aaaa hh:mm. */
    public static String dataHora(Object t) {
        return t instanceof LocalDateTime ldt ? DATA_HORA.format(ldt) : data(t);
    }

    /** Data no formato aaaa-mm-dd, que é o que o <input type="date"> do HTML espera. */
    public static String iso(Object t) {
        return t instanceof LocalDate ld ? ISO.format(ld) : "";
    }

    /**
     * Endereço da fotografia de um produto. Um URL completo fica como está; um caminho relativo
     * recebe o contexto da aplicação à frente. Sem foto, usamos uma imagem genérica.
     */
    public static String foto(String url, String contexto) {
        if (url == null || url.isBlank()) {
            return contexto + "/img/sem-foto.svg";
        }
        if (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("/")) {
            return url;
        }
        return contexto + "/" + url;
    }

    /** Converte o valor escrito num formulário em número, aceitando vírgula ou ponto ("22,50" ou "22.50"). */
    public static BigDecimal dinheiro(String s) {
        return new BigDecimal(s.trim().replace(" ", "").replace(",", "."));
    }
}

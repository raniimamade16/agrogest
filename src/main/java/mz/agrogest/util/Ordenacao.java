package mz.agrogest.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/** Ajuda a ordenar as listagens pela coluna que o utilizador escolheu (parâmetros ordem e dir do endereço). */
public final class Ordenacao {

    /**
     * Compara texto como em português: ignora maiúsculas e acentos, por isso "Água" fica junto de "agro" e não no fim da lista.
     */
    public static final Collator COLLATOR = Collator.getInstance(Locale.forLanguageTag("pt-PT"));

    static {
        COLLATOR.setStrength(Collator.PRIMARY);
    }

    private Ordenacao() {
    }

    /** Comparador de texto em português para um campo qualquer, por exemplo o nome. */
    public static <T> Comparator<T> porTexto(Function<T, String> campo) {
        return Comparator.comparing(campo, COLLATOR);
    }

    /**
     * Ordena a lista pelo campo pedido, em ordem crescente ou decrescente.
     * Se o campo não for conhecido, usa o campo por omissão. Devolve o campo realmente usado,
     * para a JSP saber que coluna deve marcar.
     */
    public static <T> String ordenar(List<T> lista, String campo, String dir,
                                     Map<String, Comparator<T>> criterios, String padrao) {
        String usado = criterios.containsKey(campo) ? campo : padrao;
        Comparator<T> c = criterios.get(usado);
        lista.sort("desc".equalsIgnoreCase(dir) ? c.reversed() : c);
        return usado;
    }
}

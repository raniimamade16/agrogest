package mz.agrogest.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/** Ordenação das listagens a partir dos parâmetros {@code ordem} e {@code dir} do pedido. */
public final class Ordenacao {

    /** Comparação de texto em português (ignora maiúsculas e acentos: "Água" ao pé de "agro"). */
    public static final Collator COLLATOR = Collator.getInstance(Locale.forLanguageTag("pt-PT"));

    static {
        COLLATOR.setStrength(Collator.PRIMARY);
    }

    private Ordenacao() {
    }

    public static <T> Comparator<T> porTexto(Function<T, String> campo) {
        return Comparator.comparing(campo, COLLATOR);
    }

    /**
     * Ordena a lista in-place. Se {@code campo} não existir em {@code criterios}, usa {@code padrao}.
     * @return o campo efetivamente usado (para a vista marcar a coluna ativa)
     */
    public static <T> String ordenar(List<T> lista, String campo, String dir,
                                     Map<String, Comparator<T>> criterios, String padrao) {
        String usado = criterios.containsKey(campo) ? campo : padrao;
        Comparator<T> c = criterios.get(usado);
        lista.sort("desc".equalsIgnoreCase(dir) ? c.reversed() : c);
        return usado;
    }
}

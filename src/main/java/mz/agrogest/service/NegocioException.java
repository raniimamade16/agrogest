package mz.agrogest.service;

import java.util.List;

/**
 * Lançada quando uma regra de negócio é violada, por exemplo stock insuficiente ou email repetido.
 * A mensagem é escrita para o utilizador final e aparece no ecrã. A lista de detalhes serve para
 * mostrar vários problemas de uma vez (por exemplo, um por cada produto do carrinho).
 *
 * É uma RuntimeException para não obrigar a declarar "throws" em todos os métodos.
 */
public class NegocioException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final List<String> detalhes;

    public NegocioException(String mensagem) {
        this(mensagem, List.of());
    }

    public NegocioException(String mensagem, List<String> detalhes) {
        super(mensagem);
        this.detalhes = List.copyOf(detalhes);
    }

    public List<String> getDetalhes() {
        return detalhes;
    }
}

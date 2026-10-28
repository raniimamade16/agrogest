package mz.agrogest.service;

import java.util.List;

/** Violação de uma regra de negócio; a mensagem é mostrada ao utilizador. */
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

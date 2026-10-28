package mz.agrogest.service;

/** O utilizador autenticado não tem permissão sobre o recurso (resulta em HTTP 403). */
public class AcessoNegadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}

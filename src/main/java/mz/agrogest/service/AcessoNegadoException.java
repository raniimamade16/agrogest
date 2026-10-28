package mz.agrogest.service;

/**
 * Lançada quando alguém com sessão iniciada tenta ver algo que não é seu,
 * como a factura de outro cliente. O servlet transforma-a numa resposta HTTP 403.
 */
public class AcessoNegadoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}

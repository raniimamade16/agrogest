package mz.agrogest.service;

import java.util.Optional;
import java.util.regex.Pattern;

import mz.agrogest.model.Perfil;
import mz.agrogest.model.Utilizador;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.PasswordUtil;

/**
 * Tudo o que tem a ver com contas: login, registo de clientes, conta de administrador e ativação de clientes.
 */
public class AuthService {

    // Credenciais fixas do administrador, definidas no enunciado. A palavra-passe só é usada no
    // arranque para gerar o hash; o que fica guardado em memória e no ficheiro é o hash.
    public static final String ADMIN_EMAIL = "admin@agro.mz";
    public static final String ADMIN_PASSWORD = "admin234";

    // Validação simples do formato do email: algo@algo.dominio
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final DataStore store;

    public AuthService(DataStore store) {
        this.store = store;
    }

    /** Cria a conta de administrador se ainda não existir. É chamado sempre que a aplicação arranca. */
    public void garantirAdmin() {
        synchronized (store) {
            if (store.utilizadorPorEmail(ADMIN_EMAIL).isEmpty()) {
                store.adicionar(new Utilizador(store.proximoIdUtilizador(), "Administrador", ADMIN_EMAIL,
                        PasswordUtil.hash(ADMIN_PASSWORD), Perfil.ADMIN, "+258 84 000 0000", "Nampula"));
            }
        }
    }

    /**
     * Procura o utilizador pelo email e compara a palavra-passe com o hash guardado. Devolve vazio se não bater certo.
     */
    public Optional<Utilizador> autenticar(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return store.utilizadorPorEmail(email.trim())
                .filter(u -> PasswordUtil.verificar(password, u.getPasswordHash()));
    }

    /** Regista um novo cliente depois de validar o email, o tamanho da palavra-passe e a confirmação. */
    public Utilizador registarCliente(String nome, String email, String password, String confirmacao,
                                      String telefone, String endereco) {
        email = email.trim().toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new NegocioException("Email inválido.");
        }
        if (password.length() < 4) {
            throw new NegocioException("A palavra-passe deve ter pelo menos 4 caracteres.");
        }
        if (!password.equals(confirmacao)) {
            throw new NegocioException("As palavras-passe não coincidem.");
        }
        // A pergunta "este email já existe?" e a inserção têm de acontecer juntas, sem ninguém pelo meio.
        // Sem o synchronized, dois registos ao mesmo tempo com o mesmo email podiam passar os dois.
        synchronized (store) {
            if (store.utilizadorPorEmail(email).isPresent()) {
                throw new NegocioException("Já existe uma conta com o email " + email + ".");
            }
            Utilizador u = new Utilizador(store.proximoIdUtilizador(), nome, email,
                    PasswordUtil.hash(password), Perfil.CLIENTE, telefone, endereco);
            store.adicionar(u);
            return u;
        }
    }

    /** Ativa ou desativa a conta de um cliente. A conta do administrador nunca pode ser desativada. */
    public void alternarAtivo(int clienteId) {
        synchronized (store) {
            Utilizador u = store.utilizador(clienteId)
                    .orElseThrow(() -> new NegocioException("Cliente não encontrado."));
            if (u.isAdmin()) {
                throw new NegocioException("A conta de administrador não pode ser desativada.");
            }
            u.setAtivo(!u.isAtivo());
        }
    }
}

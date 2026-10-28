package mz.agrogest.service;

import java.util.Optional;
import java.util.regex.Pattern;

import mz.agrogest.model.Perfil;
import mz.agrogest.model.Utilizador;
import mz.agrogest.repo.DataStore;
import mz.agrogest.util.PasswordUtil;

/** Autenticação, auto-registo de clientes e gestão de contas. */
public class AuthService {

    public static final String ADMIN_EMAIL = "admin@agro.mz";
    public static final String ADMIN_PASSWORD = "admin234";

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final DataStore store;

    public AuthService(DataStore store) {
        this.store = store;
    }

    /** Garante que a conta fixa de administrador existe (chamado no arranque). */
    public void garantirAdmin() {
        synchronized (store) {
            if (store.utilizadorPorEmail(ADMIN_EMAIL).isEmpty()) {
                store.adicionar(new Utilizador(store.proximoIdUtilizador(), "Administrador", ADMIN_EMAIL,
                        PasswordUtil.hash(ADMIN_PASSWORD), Perfil.ADMIN, "+258 84 000 0000", "Nampula"));
            }
        }
    }

    public Optional<Utilizador> autenticar(String email, String password) {
        if (email == null || password == null) {
            return Optional.empty();
        }
        return store.utilizadorPorEmail(email.trim())
                .filter(u -> PasswordUtil.verificar(password, u.getPasswordHash()));
    }

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
        // verificação de unicidade + inserção têm de ser atómicas
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

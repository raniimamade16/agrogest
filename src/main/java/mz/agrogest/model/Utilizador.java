package mz.agrogest.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Representa qualquer pessoa que entra no sistema, seja o administrador ou um cliente.
 *
 * Usamos uma só classe para os dois perfis porque os dados são praticamente os mesmos;
 * o que muda é o campo "perfil", que o AuthFilter consulta para decidir o que cada um pode abrir.
 * É Serializable porque fica guardado na sessão HTTP e também no ficheiro de dados.
 */
public class Utilizador implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String email;
    /** Só guardamos o hash da palavra-passe (PBKDF2), nunca o texto original. Ver PasswordUtil. */
    private String passwordHash;
    private Perfil perfil;
    // Um cliente desativado pelo administrador deixa de conseguir entrar, mas o seu histórico mantém-se.
    private boolean ativo = true;
    private String telefone;
    private String endereco;
    private LocalDateTime dataRegisto = LocalDateTime.now();

    public Utilizador() {
    }

    public Utilizador(int id, String nome, String email, String passwordHash, Perfil perfil,
                      String telefone, String endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.passwordHash = passwordHash;
        this.perfil = perfil;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    /** Atalho usado no filtro de acesso e nas JSP (por exemplo, ${eu.admin}). */
    public boolean isAdmin() {
        return perfil == Perfil.ADMIN;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(LocalDateTime dataRegisto) { this.dataRegisto = dataRegisto; }
}

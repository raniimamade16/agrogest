package mz.agrogest.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Uma campanha agrícola corresponde a um ciclo de produção, por exemplo de outubro a junho.
 * Depois de fechada, a campanha é considerada terminada e já não aceita novos cultivos
 * (pode ser reaberta pelo administrador se for preciso corrigir alguma coisa).
 */
public class Campanha implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private LocalDate inicio;
    private LocalDate fim;
    private boolean fechada;

    public Campanha() {
    }

    public Campanha(int id, String nome, LocalDate inicio, LocalDate fim) {
        this.id = id;
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public LocalDate getInicio() { return inicio; }
    public void setInicio(LocalDate inicio) { this.inicio = inicio; }
    public LocalDate getFim() { return fim; }
    public void setFim(LocalDate fim) { this.fim = fim; }
    public boolean isFechada() { return fechada; }
    public void setFechada(boolean fechada) { this.fechada = fechada; }
}

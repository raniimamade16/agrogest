package mz.agrogest.repo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Produto;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;

/** Estado completo do sistema, tal como é gravado no ficheiro .ser. */
public class Snapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    LocalDateTime gravadoEm = LocalDateTime.now();

    List<Utilizador> utilizadores = new ArrayList<>();
    List<Fornecedor> fornecedores = new ArrayList<>();
    List<Produto> produtos = new ArrayList<>();
    List<Campanha> campanhas = new ArrayList<>();
    List<Cultivo> cultivos = new ArrayList<>();
    List<Colheita> colheitas = new ArrayList<>();
    List<Venda> vendas = new ArrayList<>();

    int seqUtilizador;
    int seqFornecedor;
    int seqProduto;
    int seqCampanha;
    int seqCultivo;
    int seqColheita;
    int seqVenda;

    public LocalDateTime getGravadoEm() {
        return gravadoEm;
    }
}

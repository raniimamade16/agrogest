package mz.agrogest.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import mz.agrogest.model.Campanha;
import mz.agrogest.model.Categoria;
import mz.agrogest.model.Colheita;
import mz.agrogest.model.Cultivo;
import mz.agrogest.model.Fornecedor;
import mz.agrogest.model.Perfil;
import mz.agrogest.model.Produto;
import mz.agrogest.model.TipoFornecedor;
import mz.agrogest.model.Utilizador;
import mz.agrogest.model.Venda;
import mz.agrogest.model.VendaItem;
import mz.agrogest.repo.DataStore;

/**
 * Dados de demonstração carregados no primeiro arranque (quando não há ficheiro .ser).
 *
 * Produtos, variedades e preços correspondem ao mercado de insumos do norte de Moçambique:
 * as variedades de semente (milho Matuba, feijão nhemba IT-18, amendoim Nametil, mapira Macia)
 * são variedades libertadas pelo IIAM e cultivadas na província de Nampula. Os fornecedores e os
 * clientes são fictícios (nomes, contactos e endereços inventados para a demonstração).
 * As fotografias vêm do Wikimedia Commons (ver docs/CREDITOS-IMAGENS.md).
 */
public final class DadosIniciais {

    public static final String PASSWORD_CLIENTES_DEMO = "cliente123";

    private DadosIniciais() {
    }

    public static void popular(DataStore s) {
        LocalDate hoje = LocalDate.now();

        // ---------- fornecedores ----------
        Fornecedor sementes = forn(s, "Sementes do Norte, Lda", TipoFornecedor.SEMENTES, "+258 84 312 4410", "Nampula");
        Fornecedor adubos = forn(s, "AgroInsumos Nacala, Lda", TipoFornecedor.FERTILIZANTES, "+258 86 205 7731", "Nampula");
        Fornecedor maquinas = forn(s, "Mecânica Agrícola da Zambézia", TipoFornecedor.MAQUINARIA, "+258 82 448 0912", "Zambézia");
        Fornecedor coop = forn(s, "Cooperativa Agrícola de Malema", TipoFornecedor.SEMENTES, "+258 87 119 2365", "Nampula");

        // ---------- produtos ----------
        Produto milho = prod(s, "Semente de Milho Matuba", Categoria.SEMENTE, sementes, "85", 600, "kg",
                "Milho branco de polinização aberta (IIAM), ciclo de 110–120 dias, tolerante à seca. 25 kg/ha.",
                "milho", hoje.plusMonths(10));
        Produto nhemba = prod(s, "Semente de Feijão Nhemba IT-18", Categoria.SEMENTE, coop, "120", 350, "kg",
                "Feijão nhemba de ciclo curto (70–80 dias), grão creme. Ideal em consociação com milho. 20 kg/ha.",
                "nhemba", hoje.plusMonths(8));
        Produto amendoim = prod(s, "Semente de Amendoim Nametil", Categoria.SEMENTE, coop, "150", 280, "kg",
                "Amendoim de grão vermelho, variedade adaptada aos solos arenosos de Nampula. 80 kg/ha descascado.",
                "amendoim", hoje.plusMonths(7));
        Produto gergelim = prod(s, "Semente de Gergelim Lindi", Categoria.SEMENTE, sementes, "180", 150, "kg",
                "Gergelim branco para exportação, ciclo de 90–110 dias. Semear 5 kg/ha em linhas.",
                "gergelim", hoje.plusMonths(9));
        Produto mapira = prod(s, "Semente de Mapira Macia", Categoria.SEMENTE, sementes, "70", 220, "kg",
                "Mapira (sorgo) de grão branco, resistente à seca, boa para zonas de pouca chuva. 10 kg/ha.",
                "mapira", hoje.plusMonths(11));
        Produto nitrato = prod(s, "Adubo Nitrato de Amónio 33,5% N", Categoria.FERTILIZANTE, adubos, "2300", 90, "saco",
                "Saco de 50 kg. Adubo azotado de cobertura para milho e mapira, aplicar 4–6 semanas após a sementeira.",
                "nitrato", hoje.plusYears(2));
        Produto ureia = prod(s, "Ureia 46% N", Categoria.FERTILIZANTE, adubos, "2150", 70, "saco",
                "Saco de 50 kg. Adubo azotado granulado de alta concentração; enterrar logo após a aplicação.",
                "ureia", hoje.plusYears(2));
        Produto composto = prod(s, "Composto orgânico", Categoria.FERTILIZANTE, coop, "450", 140, "saco",
                "Saco de 25 kg de composto curtido (restolho e estrume). Melhora a retenção de água do solo.",
                "composto", hoje.plusMonths(6));
        Produto enxada = prod(s, "Enxada de cabo longo", Categoria.FERRAMENTA, maquinas, "650", 45, "un.",
                "Enxada em aço forjado com cabo de madeira de 1,2 m, para lavoura e sacha.",
                "enxada", null);
        Produto catana = prod(s, "Catana de 18\"", Categoria.FERRAMENTA, maquinas, "350", 60, "un.",
                "Catana em aço carbono com lâmina de 45 cm, para desmatação e corte de caniço.",
                "catana", null);
        Produto regador = prod(s, "Regador de 10 litros", Categoria.FERRAMENTA, maquinas, "550", 35, "un.",
                "Regador metálico com crivo removível, para viveiros e hortas.",
                "regador", null);
        Produto pulverizador = prod(s, "Pulverizador de dorso 16 L", Categoria.FERRAMENTA, maquinas, "3200", 18, "un.",
                "Pulverizador manual de dorso com lança regulável, para herbicidas e insecticidas.",
                "pulverizador", null);
        prod(s, "Motocultivador diesel 12 cv", Categoria.MAQUINARIA, maquinas, "185000", 3, "un.",
                "Tractor de duas rodas com fresa e charrua, motor diesel de 12 cv. Lavra até 1 ha por dia.",
                "motocultivador", null);
        Produto debulhadora = prod(s, "Debulhadora manual de milho", Categoria.MAQUINARIA, maquinas, "7500", 6, "un.",
                "Debulhadora de manivela em ferro fundido, até 150 kg de milho por hora sem electricidade.",
                "debulhadora", null);
        Produto motobomba = prod(s, "Motobomba a gasolina 3\"", Categoria.MAQUINARIA, maquinas, "18500", 5, "un.",
                "Motobomba de 6,5 cv com entrada e saída de 3\", até 60 m³/h. Para rega a partir de rios e poços.",
                "motobomba", null);

        // ---------- clientes ----------
        String pw = PasswordUtil.hash(PASSWORD_CLIENTES_DEMO);
        Utilizador ana = cli(s, "Ana Mussa", "ana@teste.mz", pw, "+258 84 511 2034", "Bairro de Muahivire, Nampula", 90);
        Utilizador carlos = cli(s, "Carlos Namashulua", "carlos@teste.mz", pw, "+258 82 640 1178", "Rua dos Continuadores, Ribáuè", 85);
        Utilizador fatima = cli(s, "Fátima Abdala", "fatima@teste.mz", pw, "+258 86 733 9021", "Av. Paulo Samuel Kankhomba, Nacala-Porto", 80);
        cli(s, "Joaquim Momade", "joaquim@teste.mz", pw, "+258 84 290 4456", "Bairro de Namicopo, Nampula", 70);
        Utilizador abdul = cli(s, "Abdul Rachide", "abdul@teste.mz", pw, "+258 87 315 6620", "Sede do distrito, Malema", 65);
        Utilizador ernesto = cli(s, "Ernesto Mutola", "ernesto@teste.mz", pw, "+258 84 902 3317", "Localidade de Chinga, Murrupula", 60);
        Utilizador maria = cli(s, "Maria Cossa", "maria@teste.mz", pw, "+258 82 117 8845", "Av. Eduardo Mondlane, Monapo", 50);
        cli(s, "Luísa Nhantumbo", "luisa@teste.mz", pw, "+258 86 448 2109", "Posto Administrativo de Muite, Mecubúri", 20);

        // ---------- vendas (início da campanha 2026/2027: preparação de terras e sementeira) ----------
        LocalDateTime agora = LocalDateTime.now().withSecond(0).withNano(0);
        venda(s, ana, agora.minusDays(48).withHour(9).withMinute(15), item(milho, 25), item(nitrato, 2));
        venda(s, carlos, agora.minusDays(41).withHour(10).withMinute(40), item(enxada, 3), item(catana, 2));
        venda(s, fatima, agora.minusDays(35).withHour(14).withMinute(5), item(gergelim, 20), item(composto, 6));
        venda(s, abdul, agora.minusDays(29).withHour(8).withMinute(50), item(mapira, 15), item(pulverizador, 1));
        venda(s, ana, agora.minusDays(22).withHour(11).withMinute(20), item(nhemba, 10), item(ureia, 1));
        venda(s, ernesto, agora.minusDays(17).withHour(15).withMinute(30), item(debulhadora, 1), item(regador, 2));
        venda(s, carlos, agora.minusDays(12).withHour(9).withMinute(0), item(amendoim, 40));
        venda(s, maria, agora.minusDays(8).withHour(13).withMinute(45), item(motobomba, 1), item(regador, 1));
        venda(s, fatima, agora.minusDays(5).withHour(10).withMinute(10), item(nitrato, 4), item(milho, 30));
        venda(s, ana, agora.minusDays(2).withHour(16).withMinute(25), item(composto, 10), item(enxada, 2));

        // ---------- campanhas, cultivos e colheitas (já concluídas) ----------
        Campanha c1 = campanha(s, "Campanha 2024/2025", LocalDate.of(2024, 10, 1), LocalDate.of(2025, 6, 30));
        Cultivo c1milho = cultivo(s, c1, "Milho", "Matuba", "4", LocalDate.of(2024, 11, 25));
        colheita(s, c1milho, LocalDate.of(2025, 4, 10), "7200", "16");

        Campanha c2 = campanha(s, "Campanha 2025/2026", LocalDate.of(2025, 10, 1), LocalDate.of(2026, 6, 30));
        Cultivo c2milho = cultivo(s, c2, "Milho", "Matuba", "5", LocalDate.of(2025, 11, 20));
        Cultivo c2nhemba = cultivo(s, c2, "Feijão nhemba", "IT-18", "3", LocalDate.of(2025, 12, 15));
        Cultivo c2gergelim = cultivo(s, c2, "Gergelim", "Lindi", "4", LocalDate.of(2026, 1, 10));
        colheita(s, c2milho, LocalDate.of(2026, 4, 15), "9500", "18");
        colheita(s, c2nhemba, LocalDate.of(2026, 3, 5), "2400", "45");
        colheita(s, c2gergelim, LocalDate.of(2026, 5, 20), "2000", "95");
    }

    private static Fornecedor forn(DataStore s, String nome, TipoFornecedor tipo, String contacto, String prov) {
        Fornecedor f = new Fornecedor(s.proximoIdFornecedor(), nome, tipo, contacto, prov);
        s.adicionar(f);
        return f;
    }

    private static Produto prod(DataStore s, String nome, Categoria cat, Fornecedor f, String preco, int qtd,
                                String unidade, String desc, String foto, LocalDate validade) {
        Produto p = new Produto(s.proximoIdProduto(), nome, cat, f.getId(), new BigDecimal(preco), qtd,
                unidade, desc, "img/produtos/" + foto + ".jpg", validade);
        s.adicionar(p);
        return p;
    }

    private static Utilizador cli(DataStore s, String nome, String email, String hash, String tel, String end,
                                  int diasDesdeRegisto) {
        Utilizador u = new Utilizador(s.proximoIdUtilizador(), nome, email, hash, Perfil.CLIENTE, tel, end);
        u.setDataRegisto(LocalDateTime.now().minusDays(diasDesdeRegisto));
        s.adicionar(u);
        return u;
    }

    private static VendaItem item(Produto p, int qtd) {
        p.setQuantidade(p.getQuantidade() - qtd);
        return new VendaItem(p.getId(), p.getNome(), p.getUnidade(), qtd, p.getPreco());
    }

    private static void venda(DataStore s, Utilizador c, LocalDateTime data, VendaItem... itens) {
        Venda v = new Venda();
        v.setId(s.proximoIdVenda());
        v.setData(data);
        v.setNumeroFactura("FT-" + data.getYear() + "-" + v.getId());
        v.setClienteId(c.getId());
        v.setClienteNome(c.getNome());
        v.setClienteEmail(c.getEmail());
        v.setClienteTelefone(c.getTelefone());
        v.setClienteEndereco(c.getEndereco());
        v.getItens().addAll(List.of(itens));
        s.adicionar(v);
    }

    private static Campanha campanha(DataStore s, String nome, LocalDate inicio, LocalDate fim) {
        Campanha c = new Campanha(s.proximoIdCampanha(), nome, inicio, fim);
        c.setFechada(true);
        s.adicionar(c);
        return c;
    }

    private static Cultivo cultivo(DataStore s, Campanha c, String cultura, String variedade, String ha,
                                   LocalDate plantio) {
        Cultivo cu = new Cultivo(s.proximoIdCultivo(), c.getId(), cultura, variedade, new BigDecimal(ha), plantio);
        s.adicionar(cu);
        return cu;
    }

    private static void colheita(DataStore s, Cultivo cu, LocalDate data, String kg, String precoKg) {
        s.adicionar(new Colheita(s.proximoIdColheita(), cu.getId(), data, new BigDecimal(kg), new BigDecimal(precoKg)));
    }
}

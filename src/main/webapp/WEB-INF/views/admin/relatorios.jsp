<c:set var="titulo" value="Relatórios" /><c:set var="menu" value="relatorios" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Relatórios de clientes</h1>
<nav class="separadores" aria-label="Secções">
    <a href="#por-nome">Por nome</a>
    <a href="#compraram">Que compraram</a>
    <a href="#assiduos">Mais assíduos</a>
    <a href="#sem-compras">Sem compras</a>
</nav>

<div class="duas-colunas">
    <section class="cartao" id="compraram">
        <h2>Clientes que compraram <small class="suave">por volume (valor gasto)</small></h2>
        <c:choose>
            <c:when test="${empty compraram}"><p class="suave">Nenhum cliente comprou ainda.</p></c:when>
            <c:otherwise>
                <c:set var="maximo" value="${compraram[0].totalGasto}" />
                <ol class="ranking">
                    <c:forEach var="r" items="${compraram}">
                        <li>
                            <div class="ranking-linha"><span><c:out value="${r.cliente.nome}" /></span><strong>${ag:mt(r.totalGasto)}</strong></div>
                            <div class="barra"><span style="width: ${r.totalGasto * 100 / maximo}%"></span></div>
                            <small class="suave">${r.numeroCompras} compra(s) · ${r.unidades} unidades</small>
                        </li>
                    </c:forEach>
                </ol>
            </c:otherwise>
        </c:choose>
    </section>

    <section class="cartao" id="assiduos">
        <h2>Clientes mais assíduos <small class="suave">por número de compras</small></h2>
        <c:choose>
            <c:when test="${empty assiduos}"><p class="suave">Nenhum cliente comprou ainda.</p></c:when>
            <c:otherwise>
                <table class="tabela">
                    <thead><tr><th>#</th><th>Cliente</th><th class="num">Compras</th><th>Última compra</th></tr></thead>
                    <tbody>
                    <c:forEach var="r" items="${assiduos}" varStatus="s">
                        <tr><td><span class="posicao">${s.count}</span></td><td><c:out value="${r.cliente.nome}" /></td>
                            <td class="num"><strong>${r.numeroCompras}</strong></td><td>${ag:data(r.ultimaCompra)}</td></tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </section>
</div>

<div class="duas-colunas">
    <section class="cartao" id="por-nome">
        <h2>Clientes ordenados por nome <small class="suave">(A a Z)</small></h2>
        <div class="tabela-rolar">
            <table class="tabela">
                <thead><tr><th>Nome</th><th>Email</th><th>Telefone</th></tr></thead>
                <tbody>
                <c:forEach var="c" items="${porNome}">
                    <tr><td><c:out value="${c.nome}" /></td><td><c:out value="${c.email}" /></td><td><c:out value="${c.telefone}" /></td></tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </section>

    <section class="cartao" id="sem-compras">
        <h2>Clientes sem compras</h2>
        <c:choose>
            <c:when test="${empty semCompras}"><p class="suave">Todos os clientes já compraram.</p></c:when>
            <c:otherwise>
                <ul class="lista-simples">
                    <c:forEach var="c" items="${semCompras}">
                        <li><span><c:out value="${c.nome}" /></span><small class="suave">registado em ${ag:data(c.dataRegisto)}</small></li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </section>
</div>
<%@ include file="../_rodape.jspf" %>

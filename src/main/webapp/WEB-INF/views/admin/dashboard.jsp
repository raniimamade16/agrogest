<c:set var="titulo" value="Painel" /><c:set var="menu" value="dashboard" />
<%@ include file="../_cabecalho.jspf" %>
<div class="cabecalho-pagina">
    <div>
        <h1>Painel</h1>
        <p class="suave">Resumo da actividade da exploração.</p>
    </div>
</div>

<section class="indicadores">
    <a class="indicador" href="${ctx}/admin/fornecedores"><span class="indicador-valor">${nFornecedores}</span><span>fornecedores</span></a>
    <a class="indicador" href="${ctx}/admin/produtos"><span class="indicador-valor">${nProdutos}</span><span>produtos</span></a>
    <a class="indicador" href="${ctx}/admin/listagens?tipo=clientes"><span class="indicador-valor">${nClientes}</span><span>clientes</span></a>
    <a class="indicador" href="${ctx}/admin/listagens?tipo=compras"><span class="indicador-valor">${nVendas}</span><span>vendas</span></a>
    <a class="indicador" href="${ctx}/admin/campanhas"><span class="indicador-valor">${nCampanhas}</span><span>campanhas</span></a>
</section>

<section class="indicadores indicadores-valor">
    <div class="indicador destaque"><span>Total facturado (c/ IVA)</span><span class="indicador-valor">${ag:mt(totalFacturado)}</span></div>
    <div class="indicador"><span>Valor das colheitas</span><span class="indicador-valor">${ag:mt(valorColheitas)}</span></div>
</section>

<div class="duas-colunas">
    <section class="cartao">
        <h2>Últimas vendas</h2>
        <c:choose>
            <c:when test="${empty ultimasVendas}"><p class="suave">Ainda não há vendas.</p></c:when>
            <c:otherwise>
                <div class="tabela-rolar">
                    <table class="tabela">
                        <thead><tr><th>Factura</th><th>Cliente</th><th>Data</th><th class="num">Total</th></tr></thead>
                        <tbody>
                        <c:forEach var="v" items="${ultimasVendas}">
                            <tr>
                                <td><a href="${ctx}/factura?id=${v.id}">${v.numeroFactura}</a></td>
                                <td><c:out value="${v.clienteNome}" /></td>
                                <td>${ag:data(v.data)}</td>
                                <td class="num">${ag:mt(v.total)}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
    <section class="cartao">
        <h2>Stock baixo <small class="suave">(menos de ${stockMinimo})</small></h2>
        <c:choose>
            <c:when test="${empty stockBaixo}"><p class="suave">Todos os produtos têm stock suficiente.</p></c:when>
            <c:otherwise>
                <ul class="lista-simples">
                    <c:forEach var="p" items="${stockBaixo}">
                        <li><span><c:out value="${p.nome}" /></span>
                            <span class="etiqueta ${p.quantidade eq 0 ? 'etiqueta-erro' : 'etiqueta-aviso'}">${p.quantidade} ${p.unidade}</span></li>
                    </c:forEach>
                </ul>
                <a class="ligacao-avancar" href="${ctx}/admin/produtos">Repor stock</a>
            </c:otherwise>
        </c:choose>
    </section>
</div>
<%@ include file="../_rodape.jspf" %>

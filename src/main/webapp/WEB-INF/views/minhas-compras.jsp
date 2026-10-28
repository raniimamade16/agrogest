<c:set var="titulo" value="Minhas compras" /><c:set var="menu" value="compras" />
<%@ include file="_cabecalho.jspf" %>
<h1>Minhas compras</h1>
<c:choose>
    <c:when test="${empty vendas}">
        <t:vazio titulo="Ainda não fez compras">Visite o <a href="${ctx}/catalogo">catálogo</a> para começar.</t:vazio>
    </c:when>
    <c:otherwise>
        <div class="cartao">
            <div class="tabela-rolar">
                <table class="tabela">
                    <thead><tr><th>Factura</th><th>Data</th><th>Produtos</th><th class="num">Total (c/ IVA)</th><th></th></tr></thead>
                    <tbody>
                    <c:forEach var="v" items="${vendas}">
                        <tr>
                            <td><strong>${v.numeroFactura}</strong></td>
                            <td>${ag:dataHora(v.data)}</td>
                            <td><c:forEach var="i" items="${v.itens}" varStatus="s"><c:out value="${i.produtoNome}" /> (${i.quantidade})${s.last ? '' : ', '}</c:forEach></td>
                            <td class="num">${ag:mt(v.total)}</td>
                            <td><a class="botao botao-pequeno botao-secundario" href="${ctx}/factura?id=${v.id}">Ver factura</a></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="_rodape.jspf" %>

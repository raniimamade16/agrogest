<c:set var="titulo" value="Confirmar compra" /><c:set var="menu" value="carrinho" />
<c:set var="car" value="${sessionScope.carrinho}" />
<%@ include file="_cabecalho.jspf" %>
<h1>Confirmar compra</h1>

<c:if test="${not empty problemas}">
    <div class="alerta alerta-erro" role="alert">
        <p>Não é possível concluir a compra: há produtos indisponíveis.</p>
        <ul><c:forEach var="pr" items="${problemas}"><li><c:out value="${pr}" /></li></c:forEach></ul>
        <p><a href="${ctx}/carrinho">Voltar ao carrinho para ajustar</a></p>
    </div>
</c:if>

<div class="duas-colunas">
    <section class="cartao">
        <h2>Itens</h2>
        <div class="tabela-rolar">
            <table class="tabela">
                <thead><tr><th>Produto</th><th class="num">Qtd.</th><th class="num">Preço unit.</th><th class="num">Subtotal</th></tr></thead>
                <tbody>
                <c:forEach var="item" items="${car.itens}">
                    <tr>
                        <td><c:out value="${item.produto.nome}" /></td>
                        <td class="num">${item.quantidade} ${item.produto.unidade}</td>
                        <td class="num">${ag:mt(item.produto.preco)}</td>
                        <td class="num">${ag:mt(item.subtotal)}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </section>
    <aside class="cartao">
        <h2>Dados de facturação</h2>
        <dl class="ficha">
            <div><dt>Cliente</dt><dd><c:out value="${eu.nome}" /></dd></div>
            <div><dt>Email</dt><dd><c:out value="${eu.email}" /></dd></div>
            <div><dt>Telefone</dt><dd><c:out value="${eu.telefone}" /></dd></div>
            <div><dt>Endereço</dt><dd><c:out value="${eu.endereco}" /></dd></div>
        </dl>
        <dl class="totais">
            <div><dt>Subtotal</dt><dd>${ag:mt(car.subtotal)}</dd></div>
            <div><dt>IVA (16%)</dt><dd>${ag:mt(car.iva)}</dd></div>
            <div class="total"><dt>Total a pagar</dt><dd>${ag:mt(car.total)}</dd></div>
        </dl>
        <form method="post" action="${ctx}/compra">
            <button type="submit" class="botao botao-grande botao-largo" ${not empty problemas ? 'disabled' : ''}>Confirmar e emitir factura</button>
        </form>
        <a class="ligacao-voltar" href="${ctx}/carrinho">Voltar ao carrinho</a>
    </aside>
</div>
<%@ include file="_rodape.jspf" %>

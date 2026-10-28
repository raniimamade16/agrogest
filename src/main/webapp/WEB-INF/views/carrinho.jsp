<c:set var="titulo" value="Carrinho" /><c:set var="menu" value="carrinho" />
<c:set var="car" value="${sessionScope.carrinho}" />
<%@ include file="_cabecalho.jspf" %>
<div class="cabecalho-pagina">
    <h1>Carrinho</h1>
    <a class="botao botao-secundario" href="${ctx}/catalogo">Continuar a comprar</a>
</div>

<c:choose>
    <c:when test="${car.vazio}">
        <t:vazio titulo="O carrinho está vazio">Adicione produtos a partir do <a href="${ctx}/catalogo">catálogo</a>.</t:vazio>
    </c:when>
    <c:otherwise>
        <c:if test="${not empty problemas}">
            <div class="alerta alerta-aviso" role="alert">
                <p>Alguns produtos estão indisponíveis. Ajuste as quantidades antes de finalizar:</p>
                <ul><c:forEach var="pr" items="${problemas}"><li><c:out value="${pr}" /></li></c:forEach></ul>
            </div>
        </c:if>
        <div class="cartao">
            <div class="tabela-rolar">
                <table class="tabela">
                    <thead>
                    <tr><th>Produto</th><th class="num">Preço unit.</th><th>Quantidade</th><th class="num">Subtotal</th><th></th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${car.itens}">
                        <tr>
                            <td>
                                <div class="celula-produto">
                                    <img src="${ag:foto(item.produto.foto, ctx)}" alt="" width="44" height="44">
                                    <div><strong><c:out value="${item.produto.nome}" /></strong>
                                        <small class="suave">Em stock: ${item.produto.quantidade} ${item.produto.unidade}</small></div>
                                </div>
                            </td>
                            <td class="num">${ag:mt(item.produto.preco)}</td>
                            <td>
                                <form method="post" action="${ctx}/carrinho/atualizar" class="form-linha">
                                    <input type="hidden" name="produtoId" value="${item.produto.id}">
                                    <input type="number" name="quantidade" value="${item.quantidade}" min="0" max="${item.produto.quantidade}" aria-label="Quantidade" class="campo-curto">
                                    <button type="submit" class="botao botao-pequeno botao-secundario">Atualizar</button>
                                </form>
                            </td>
                            <td class="num">${ag:mt(item.subtotal)}</td>
                            <td>
                                <form method="post" action="${ctx}/carrinho/remover">
                                    <input type="hidden" name="produtoId" value="${item.produto.id}">
                                    <button type="submit" class="botao botao-pequeno botao-perigo">Remover</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <div class="rodape-cartao">
                <form method="post" action="${ctx}/carrinho/esvaziar">
                    <button type="submit" class="botao botao-secundario">Esvaziar carrinho</button>
                </form>
                <dl class="totais">
                    <div><dt>Subtotal</dt><dd>${ag:mt(car.subtotal)}</dd></div>
                    <div><dt>IVA (16%)</dt><dd>${ag:mt(car.iva)}</dd></div>
                    <div class="total"><dt>Total</dt><dd>${ag:mt(car.total)}</dd></div>
                </dl>
            </div>
        </div>
        <div class="acoes-fim">
            <a class="botao botao-grande" href="${ctx}/compra">Finalizar compra</a>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="_rodape.jspf" %>

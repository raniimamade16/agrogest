<c:set var="titulo" value="Catálogo" /><c:set var="menu" value="catalogo" />
<%@ include file="_cabecalho.jspf" %>
<div class="cabecalho-pagina">
    <div>
        <h1>Catálogo</h1>
        <p class="suave">Produtos disponíveis em armazém. Os preços não incluem IVA (16%).</p>
    </div>
    <c:if test="${not empty sessionScope.carrinho and not sessionScope.carrinho.vazio}">
        <a class="botao" href="${ctx}/carrinho">Ver carrinho (${sessionScope.carrinho.totalItens})</a>
    </c:if>
</div>

<nav class="filtros" aria-label="Filtrar por categoria">
    <a href="${ctx}/catalogo" class="filtro ${empty categoriaAtual ? 'ativo' : ''}">Todas</a>
    <c:forEach var="cat" items="${categorias}">
        <a href="${ctx}/catalogo?categoria=${cat}" class="filtro ${cat eq categoriaAtual ? 'ativo' : ''}">${fn:toLowerCase(cat)}</a>
    </c:forEach>
</nav>

<c:choose>
    <c:when test="${empty produtos}">
        <t:vazio titulo="Sem produtos disponíveis">Não há produtos em stock nesta categoria de momento.</t:vazio>
    </c:when>
    <c:otherwise>
        <div class="grelha-produtos">
            <c:forEach var="p" items="${produtos}">
                <article class="produto">
                    <div class="produto-foto">
                        <img src="${ag:foto(p.foto, ctx)}" alt="<c:out value='${p.nome}'/>" loading="lazy">
                        <span class="etiqueta etiqueta-${fn:toLowerCase(p.categoria)}">${fn:toLowerCase(p.categoria)}</span>
                    </div>
                    <div class="produto-corpo">
                        <h2><c:out value="${p.nome}" /></h2>
                        <p class="produto-desc"><c:out value="${p.descricao}" /></p>
                        <dl class="produto-dados">
                            <div><dt>Fornecedor</dt><dd><c:out value="${fornecedores[p.fornecedorId].nome}" /></dd></div>
                            <div><dt>Disponível</dt><dd>${p.quantidade} ${p.unidade}</dd></div>
                            <div><dt>Validade</dt><dd>${empty p.validade ? '—' : ag:data(p.validade)}</dd></div>
                        </dl>
                        <p class="produto-preco">${ag:mt(p.preco)} <small>/ ${p.unidade}</small></p>
                        <form method="post" action="${ctx}/carrinho/adicionar" class="produto-comprar">
                            <input type="hidden" name="produtoId" value="${p.id}">
                            <input type="hidden" name="categoria" value="${categoriaAtual}">
                            <label class="oculto" for="qtd-${p.id}">Quantidade</label>
                            <input id="qtd-${p.id}" type="number" name="quantidade" value="1" min="1" max="${p.quantidade}" required>
                            <button type="submit" class="botao">Adicionar</button>
                        </form>
                    </div>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="_rodape.jspf" %>

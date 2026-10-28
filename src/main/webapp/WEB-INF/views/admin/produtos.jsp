<c:set var="titulo" value="Produtos" /><c:set var="menu" value="produtos" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Produtos</h1>
<div class="layout-gestao">
    <section class="cartao">
        <h2>Novo produto</h2>
        <c:choose>
            <c:when test="${empty listaFornecedores}">
                <p class="suave">Crie primeiro um <a href="${ctx}/admin/fornecedores">fornecedor</a>.</p>
            </c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/admin/produtos" class="formulario">
                    <input type="hidden" name="acao" value="criar">
                    <label>Nome <input name="nome" required placeholder="Semente Feijão nhemba"></label>
                    <div class="linha-campos">
                        <label>Categoria
                            <select name="categoria" required>
                                <c:forEach var="cat" items="${categorias}"><option value="${cat}">${cat}</option></c:forEach>
                            </select>
                        </label>
                        <label>Fornecedor
                            <select name="fornecedorId" required>
                                <c:forEach var="f" items="${listaFornecedores}"><option value="${f.id}"><c:out value="${f.nome}" /></option></c:forEach>
                            </select>
                        </label>
                    </div>
                    <div class="linha-campos">
                        <label>Preço (MT) <input name="preco" inputmode="decimal" required placeholder="320"></label>
                        <label>Quantidade <input type="number" name="quantidade" min="0" required placeholder="400"></label>
                        <label>Unidade <input name="unidade" required placeholder="kg" list="unidades"></label>
                    </div>
                    <datalist id="unidades"><option value="kg"><option value="saco"><option value="litro"><option value="unidade"></datalist>
                    <label>Validade <input type="date" name="validade"></label>
                    <label>Foto (URL) <input name="foto" placeholder="https://… ou img/produtos/milho.jpg"></label>
                    <label>Descrição <textarea name="descricao" rows="3"></textarea></label>
                    <button type="submit" class="botao">Criar produto</button>
                </form>
            </c:otherwise>
        </c:choose>
    </section>
    <section class="cartao">
        <h2>Lista <small class="suave">(${fn:length(produtos)})</small></h2>
        <c:choose>
            <c:when test="${empty produtos}"><t:vazio titulo="Sem produtos">Crie o primeiro produto no formulário.</t:vazio></c:when>
            <c:otherwise>
                <div class="tabela-rolar">
                    <table class="tabela">
                        <thead><tr><th>Produto</th><th class="num">Preço</th><th class="num">Stock</th><th>Validade</th><th>Repor stock / alterar preço</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="p" items="${produtos}">
                            <tr class="${p.expirado ? 'linha-alerta' : ''}">
                                <td>
                                    <div class="celula-produto">
                                        <img src="${ag:foto(p.foto, ctx)}" alt="" width="40" height="40">
                                        <div><strong><c:out value="${p.nome}" /></strong>
                                            <small class="suave">${p.categoria} · <c:out value="${fornecedores[p.fornecedorId].nome}" /></small></div>
                                    </div>
                                </td>
                                <td class="num">${ag:mt(p.preco)}</td>
                                <td class="num"><span class="${p.quantidade eq 0 ? 'texto-erro' : ''}">${p.quantidade} ${p.unidade}</span></td>
                                <td>${empty p.validade ? '—' : ag:data(p.validade)}<c:if test="${p.expirado}"> <span class="etiqueta etiqueta-erro">expirado</span></c:if></td>
                                <td>
                                    <form method="post" action="${ctx}/admin/produtos" class="form-linha">
                                        <input type="hidden" name="acao" value="atualizar"><input type="hidden" name="id" value="${p.id}">
                                        <input type="number" name="reposicao" min="0" placeholder="+ qtd" class="campo-curto" aria-label="Quantidade a repor">
                                        <input name="preco" inputmode="decimal" placeholder="novo preço" class="campo-curto" aria-label="Novo preço">
                                        <button type="submit" class="botao botao-pequeno botao-secundario">Atualizar</button>
                                    </form>
                                </td>
                                <td>
                                    <form method="post" action="${ctx}/admin/produtos">
                                        <input type="hidden" name="acao" value="remover"><input type="hidden" name="id" value="${p.id}">
                                        <button type="submit" class="botao botao-pequeno botao-perigo">Remover</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
<%@ include file="../_rodape.jspf" %>

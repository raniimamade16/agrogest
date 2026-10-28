<c:set var="titulo" value="Listagens" /><c:set var="menu" value="listagens" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Listagens</h1>
<nav class="separadores" aria-label="Tipo de listagem">
    <a href="?tipo=produtos" class="${tipo eq 'produtos' ? 'ativo' : ''}">Produtos</a>
    <a href="?tipo=fornecedores" class="${tipo eq 'fornecedores' ? 'ativo' : ''}">Fornecedores</a>
    <a href="?tipo=clientes" class="${tipo eq 'clientes' ? 'ativo' : ''}">Clientes</a>
    <a href="?tipo=armazem" class="${tipo eq 'armazem' ? 'ativo' : ''}">Armazém</a>
    <a href="?tipo=compras" class="${tipo eq 'compras' ? 'ativo' : ''}">Compras detalhadas</a>
</nav>
<p class="suave">Clique no cabeçalho de uma coluna para ordenar (clique de novo para inverter).</p>

<c:choose>
    <%-- ===================== PRODUTOS / ARMAZÉM ===================== --%>
    <c:when test="${tipo eq 'produtos' or tipo eq 'armazem'}">
        <c:if test="${tipo eq 'armazem'}">
            <p class="resumo-listagem">Produtos em armazém (stock &gt; 0): <strong>${fn:length(lista)}</strong> · valor em stock: <strong>${ag:mt(valorArmazem)}</strong></p>
        </c:if>
        <div class="cartao"><div class="tabela-rolar">
            <table class="tabela">
                <thead><tr>
                    <th><t:ordem campo="nome" rotulo="Nome" /></th>
                    <th><t:ordem campo="categoria" rotulo="Categoria" /></th>
                    <th>Fornecedor</th>
                    <th class="num"><t:ordem campo="preco" rotulo="Preço" /></th>
                    <th class="num"><t:ordem campo="stock" rotulo="Stock" /></th>
                    <th class="num"><t:ordem campo="valor" rotulo="Valor em stock" /></th>
                    <th><t:ordem campo="data" rotulo="Validade" /></th>
                </tr></thead>
                <tbody>
                <c:forEach var="p" items="${lista}">
                    <tr>
                        <td><strong><c:out value="${p.nome}" /></strong></td>
                        <td>${p.categoria}</td>
                        <td><c:out value="${fornecedores[p.fornecedorId].nome}" /></td>
                        <td class="num">${ag:mt(p.preco)}</td>
                        <td class="num">${p.quantidade} ${p.unidade}</td>
                        <td class="num">${ag:mt(p.preco * p.quantidade)}</td>
                        <td>${empty p.validade ? '—' : ag:data(p.validade)}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="7" class="suave">Sem registos.</td></tr></c:if>
                </tbody>
            </table>
        </div></div>
    </c:when>

    <%-- ===================== FORNECEDORES ===================== --%>
    <c:when test="${tipo eq 'fornecedores'}">
        <div class="cartao"><div class="tabela-rolar">
            <table class="tabela">
                <thead><tr>
                    <th><t:ordem campo="nome" rotulo="Nome" /></th>
                    <th><t:ordem campo="tipo" rotulo="Tipo" /></th>
                    <th>Contacto</th>
                    <th><t:ordem campo="provincia" rotulo="Província" /></th>
                </tr></thead>
                <tbody>
                <c:forEach var="f" items="${lista}">
                    <tr>
                        <td><strong><c:out value="${f.nome}" /></strong></td>
                        <td>${f.tipo}</td>
                        <td><c:out value="${f.contacto}" /></td>
                        <td><c:out value="${f.provincia}" /></td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="4" class="suave">Sem registos.</td></tr></c:if>
                </tbody>
            </table>
        </div></div>
    </c:when>

    <%-- ===================== CLIENTES ===================== --%>
    <c:when test="${tipo eq 'clientes'}">
        <div class="cartao"><div class="tabela-rolar">
            <table class="tabela">
                <thead><tr>
                    <th><t:ordem campo="nome" rotulo="Nome" /></th>
                    <th>Contactos</th>
                    <th>Endereço</th>
                    <th><t:ordem campo="data" rotulo="Registo" /></th>
                    <th class="num"><t:ordem campo="compras" rotulo="Compras" /></th>
                    <th class="num"><t:ordem campo="valor" rotulo="Total gasto" /></th>
                    <th>Conta</th>
                </tr></thead>
                <tbody>
                <c:forEach var="r" items="${lista}">
                    <tr>
                        <td><strong><c:out value="${r.cliente.nome}" /></strong></td>
                        <td><c:out value="${r.cliente.email}" /><br><small class="suave"><c:out value="${r.cliente.telefone}" /></small></td>
                        <td><c:out value="${r.cliente.endereco}" /></td>
                        <td>${ag:data(r.cliente.dataRegisto)}</td>
                        <td class="num">${r.numeroCompras}</td>
                        <td class="num">${ag:mt(r.totalGasto)}</td>
                        <td>
                            <form method="post" action="${ctx}/admin/clientes" class="form-linha">
                                <input type="hidden" name="id" value="${r.cliente.id}">
                                <span class="etiqueta ${r.cliente.ativo ? 'etiqueta-ok' : 'etiqueta-erro'}">${r.cliente.ativo ? 'Ativa' : 'Inativa'}</span>
                                <button type="submit" class="botao botao-pequeno botao-secundario">${r.cliente.ativo ? 'Desativar' : 'Ativar'}</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty lista}"><tr><td colspan="7" class="suave">Sem registos.</td></tr></c:if>
                </tbody>
            </table>
        </div></div>
    </c:when>

    <%-- ===================== COMPRAS DETALHADAS ===================== --%>
    <c:when test="${tipo eq 'compras'}">
        <div class="barra-ordem">
            <span>Ordenar por:</span>
            <t:ordem campo="data" rotulo="Data" />
            <t:ordem campo="valor" rotulo="Valor" />
            <t:ordem campo="nome" rotulo="Cliente" />
            <span class="empurrar">${fn:length(lista)} compras · total <strong>${ag:mt(totalGeral)}</strong></span>
        </div>
        <c:if test="${empty lista}"><t:vazio titulo="Sem compras">Ainda não foram efetuadas compras.</t:vazio></c:if>
        <c:forEach var="v" items="${lista}">
            <article class="cartao compra">
                <header class="compra-topo">
                    <div>
                        <a href="${ctx}/factura?id=${v.id}" class="compra-numero">${v.numeroFactura}</a>
                        <span class="suave">${ag:dataHora(v.data)}</span>
                    </div>
                    <strong class="compra-total">${ag:mt(v.total)}</strong>
                </header>
                <dl class="ficha ficha-horizontal">
                    <div><dt>Cliente</dt><dd><c:out value="${v.clienteNome}" /></dd></div>
                    <div><dt>Endereço</dt><dd><c:out value="${v.clienteEndereco}" /></dd></div>
                    <div><dt>Telefone</dt><dd><c:out value="${v.clienteTelefone}" /></dd></div>
                </dl>
                <div class="tabela-rolar">
                    <table class="tabela tabela-compacta">
                        <thead><tr><th>Produto</th><th class="num">Quantidade</th><th class="num">Valor unitário</th><th class="num">Subtotal</th></tr></thead>
                        <tbody>
                        <c:forEach var="i" items="${v.itens}">
                            <tr><td><c:out value="${i.produtoNome}" /></td><td class="num">${i.quantidade} ${i.unidade}</td>
                                <td class="num">${ag:mt(i.precoUnit)}</td><td class="num">${ag:mt(i.subtotal)}</td></tr>
                        </c:forEach>
                        </tbody>
                        <tfoot>
                        <tr><td colspan="3" class="num">Subtotal</td><td class="num">${ag:mt(v.subtotal)}</td></tr>
                        <tr><td colspan="3" class="num">IVA (16%)</td><td class="num">${ag:mt(v.iva)}</td></tr>
                        <tr><th colspan="3" class="num">Total</th><th class="num">${ag:mt(v.total)}</th></tr>
                        </tfoot>
                    </table>
                </div>
            </article>
        </c:forEach>
    </c:when>
</c:choose>
<%@ include file="../_rodape.jspf" %>

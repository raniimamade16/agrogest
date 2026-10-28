<c:set var="titulo" value="Factura ${venda.numeroFactura}" /><c:set var="menu" value="${eu.admin ? 'listagens' : 'compras'}" />
<%@ include file="_cabecalho.jspf" %>
<div class="barra-factura nao-imprimir">
    <a class="ligacao-voltar" href="${ctx}${eu.admin ? '/admin/listagens?tipo=compras' : '/minhas-compras'}">Voltar</a>
    <button type="button" class="botao" onclick="window.print()">Imprimir factura</button>
</div>

<article class="factura">
    <header class="factura-topo">
        <div class="factura-emissor">
            <img src="${ctx}/img/logo.svg?v=${applicationScope.versao}" alt="" width="44" height="44">
            <div>
                <strong>AgroGest, Lda</strong><br>
                Av. do Trabalho, Nampula — Moçambique<br>
                NUIT 400 000 000 · +258 84 000 0000
            </div>
        </div>
        <div class="factura-numero">
            <span>Factura</span>
            <strong>${venda.numeroFactura}</strong>
            <small>Emitida em ${ag:dataHora(venda.data)}</small>
        </div>
    </header>

    <section class="factura-cliente">
        <h2>Cliente</h2>
        <p><strong><c:out value="${venda.clienteNome}" /></strong><br>
            <c:out value="${venda.clienteEndereco}" /><br>
            Tel.: <c:out value="${venda.clienteTelefone}" /> · <c:out value="${venda.clienteEmail}" /></p>
    </section>

    <table class="tabela factura-itens">
        <thead><tr><th>#</th><th>Descrição</th><th class="num">Quantidade</th><th class="num">Preço unit.</th><th class="num">Subtotal</th></tr></thead>
        <tbody>
        <c:forEach var="i" items="${venda.itens}" varStatus="s">
            <tr>
                <td>${s.count}</td>
                <td><c:out value="${i.produtoNome}" /></td>
                <td class="num">${i.quantidade} ${i.unidade}</td>
                <td class="num">${ag:mt(i.precoUnit)}</td>
                <td class="num">${ag:mt(i.subtotal)}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <dl class="totais factura-totais">
        <div><dt>Subtotal</dt><dd>${ag:mt(venda.subtotal)}</dd></div>
        <div><dt>IVA (16%)</dt><dd>${ag:mt(venda.iva)}</dd></div>
        <div class="total"><dt>Total</dt><dd>${ag:mt(venda.total)}</dd></div>
    </dl>

    <footer class="factura-rodape">Obrigado pela sua preferência. Documento processado por computador.</footer>
</article>
<%@ include file="_rodape.jspf" %>

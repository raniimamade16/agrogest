<c:set var="titulo" value="Campanhas" /><c:set var="menu" value="campanhas" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Campanhas agrícolas</h1>
<div class="layout-gestao">
    <section class="cartao">
        <h2>Nova campanha</h2>
        <form method="post" action="${ctx}/admin/campanhas" class="formulario">
            <input type="hidden" name="acao" value="criar">
            <label>Nome <input name="nome" required placeholder="Campanha 2026/2027"></label>
            <div class="linha-campos">
                <label>Início <input type="date" name="inicio" required></label>
                <label>Fim <input type="date" name="fim" required></label>
            </div>
            <button type="submit" class="botao">Criar campanha</button>
        </form>
    </section>
    <section class="cartao">
        <h2>Lista</h2>
        <c:choose>
            <c:when test="${empty campanhas}"><t:vazio titulo="Sem campanhas">Crie a primeira campanha.</t:vazio></c:when>
            <c:otherwise>
                <div class="tabela-rolar">
                    <table class="tabela">
                        <thead><tr><th>Campanha</th><th>Período</th><th>Estado</th><th class="num">Cultivos</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="c" items="${campanhas}">
                            <c:set var="n" value="${empty nCultivos[c.id] ? 0 : nCultivos[c.id]}" />
                            <tr>
                                <td><strong><c:out value="${c.nome}" /></strong></td>
                                <td>${ag:data(c.inicio)} → ${ag:data(c.fim)}</td>
                                <td><span class="etiqueta ${c.fechada ? '' : 'etiqueta-ok'}">${c.fechada ? 'Fechada' : 'Aberta'}</span></td>
                                <td class="num"><a href="${ctx}/admin/cultivos?campanhaId=${c.id}">${n}</a></td>
                                <td class="acoes">
                                    <form method="post" action="${ctx}/admin/campanhas">
                                        <input type="hidden" name="id" value="${c.id}">
                                        <input type="hidden" name="acao" value="${c.fechada ? 'reabrir' : 'fechar'}">
                                        <button type="submit" class="botao botao-pequeno botao-secundario">${c.fechada ? 'Reabrir' : 'Fechar'}</button>
                                    </form>
                                    <form method="post" action="${ctx}/admin/campanhas">
                                        <input type="hidden" name="id" value="${c.id}"><input type="hidden" name="acao" value="remover">
                                        <button type="submit" class="botao botao-pequeno botao-perigo" ${n gt 0 ? 'disabled title="Tem cultivos associados"' : ''}>Remover</button>
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

<c:set var="titulo" value="Cultivos" /><c:set var="menu" value="cultivos" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Cultivos</h1>
<div class="layout-gestao">
    <section class="cartao">
        <h2>Registar cultivo</h2>
        <form method="post" action="${ctx}/admin/cultivos" class="formulario">
            <input type="hidden" name="acao" value="criar">
            <label>Campanha
                <select name="campanhaId" required>
                    <c:forEach var="c" items="${campanhas}">
                        <option value="${c.id}" ${c.fechada ? 'disabled' : ''} ${c.id eq filtro ? 'selected' : ''}><c:out value="${c.nome}" />${c.fechada ? ' (fechada)' : ''}</option>
                    </c:forEach>
                </select>
            </label>
            <div class="linha-campos">
                <label>Cultura <input name="cultura" required placeholder="Feijão nhemba"></label>
                <label>Variedade <input name="variedade" required placeholder="Macia"></label>
            </div>
            <div class="linha-campos">
                <label>Hectares <input name="hectares" inputmode="decimal" required placeholder="8"></label>
                <label>Data de plantio <input type="date" name="plantio" required></label>
            </div>
            <button type="submit" class="botao">Registar cultivo</button>
        </form>
    </section>
    <section class="cartao">
        <div class="cabecalho-cartao">
            <h2>Lista</h2>
            <form method="get" action="${ctx}/admin/cultivos" class="form-linha">
                <select name="campanhaId" aria-label="Filtrar por campanha">
                    <option value="0">Todas as campanhas</option>
                    <c:forEach var="c" items="${campanhas}"><option value="${c.id}" ${c.id eq filtro ? 'selected' : ''}><c:out value="${c.nome}" /></option></c:forEach>
                </select>
                <button type="submit" class="botao botao-pequeno botao-secundario">Filtrar</button>
            </form>
        </div>
        <c:choose>
            <c:when test="${empty cultivos}"><t:vazio titulo="Sem cultivos">Registe um cultivo numa campanha aberta.</t:vazio></c:when>
            <c:otherwise>
                <div class="tabela-rolar">
                    <table class="tabela">
                        <thead><tr><th>Cultura</th><th>Campanha</th><th class="num">Hectares</th><th>Plantio</th><th class="num">Colheitas</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="cu" items="${cultivos}">
                            <c:set var="n" value="${empty nColheitas[cu.id] ? 0 : nColheitas[cu.id]}" />
                            <tr>
                                <td><strong><c:out value="${cu.cultura}" /></strong> <small class="suave"><c:out value="${cu.variedade}" /></small></td>
                                <td><c:out value="${campanhasPorId[cu.campanhaId].nome}" /></td>
                                <td class="num">${ag:num(cu.hectares)} ha</td>
                                <td>${ag:data(cu.dataPlantio)}</td>
                                <td class="num"><a href="${ctx}/admin/colheitas">${n}</a></td>
                                <td>
                                    <form method="post" action="${ctx}/admin/cultivos">
                                        <input type="hidden" name="acao" value="remover"><input type="hidden" name="id" value="${cu.id}">
                                        <button type="submit" class="botao botao-pequeno botao-perigo" ${n gt 0 ? 'disabled title="Tem colheitas associadas"' : ''}>Remover</button>
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

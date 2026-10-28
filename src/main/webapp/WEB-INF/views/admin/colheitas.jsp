<c:set var="titulo" value="Colheitas" /><c:set var="menu" value="colheitas" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Colheitas</h1>
<div class="layout-gestao">
    <section class="cartao">
        <h2>Registar colheita</h2>
        <c:choose>
            <c:when test="${empty cultivos}"><p class="suave">Registe primeiro um <a href="${ctx}/admin/cultivos">cultivo</a>.</p></c:when>
            <c:otherwise>
                <form method="post" action="${ctx}/admin/colheitas" class="formulario">
                    <input type="hidden" name="acao" value="criar">
                    <label>Cultivo
                        <select name="cultivoId" required>
                            <c:forEach var="cu" items="${cultivos}">
                                <option value="${cu.id}"><c:out value="${cu.cultura} (${cu.variedade}) — ${campanhasPorId[cu.campanhaId].nome}" /></option>
                            </c:forEach>
                        </select>
                    </label>
                    <label>Data <input type="date" name="data" required></label>
                    <div class="linha-campos">
                        <label>Quantidade (kg) <input name="kg" inputmode="decimal" required placeholder="3200"></label>
                        <label>Preço por kg (MT) <input name="precoKg" inputmode="decimal" required placeholder="22"></label>
                    </div>
                    <p class="suave">O valor total é calculado automaticamente (kg × preço/kg).</p>
                    <button type="submit" class="botao">Registar colheita</button>
                </form>
            </c:otherwise>
        </c:choose>
    </section>
    <section class="cartao">
        <h2>Lista</h2>
        <c:choose>
            <c:when test="${empty colheitas}"><t:vazio titulo="Sem colheitas">Ainda não foram registadas colheitas.</t:vazio></c:when>
            <c:otherwise>
                <div class="tabela-rolar">
                    <table class="tabela">
                        <thead><tr><th>Data</th><th>Cultivo</th><th class="num">Quantidade</th><th class="num">Preço/kg</th><th class="num">Valor total</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="co" items="${colheitas}">
                            <c:set var="cu" value="${cultivosPorId[co.cultivoId]}" />
                            <tr>
                                <td>${ag:data(co.data)}</td>
                                <td><strong><c:out value="${cu.cultura}" /></strong> <small class="suave"><c:out value="${campanhasPorId[cu.campanhaId].nome}" /></small></td>
                                <td class="num">${ag:num(co.quantidadeKg)} kg</td>
                                <td class="num">${ag:mt(co.precoKg)}</td>
                                <td class="num"><strong>${ag:mt(co.valorTotal)}</strong></td>
                                <td>
                                    <form method="post" action="${ctx}/admin/colheitas">
                                        <input type="hidden" name="acao" value="remover"><input type="hidden" name="id" value="${co.id}">
                                        <button type="submit" class="botao botao-pequeno botao-perigo">Remover</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                        <tfoot><tr><th colspan="2">Total</th><th class="num">${ag:num(totalKg)} kg</th><th></th><th class="num">${ag:mt(totalValor)}</th><th></th></tr></tfoot>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</div>
<%@ include file="../_rodape.jspf" %>

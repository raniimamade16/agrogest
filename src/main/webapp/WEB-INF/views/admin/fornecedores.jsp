<c:set var="titulo" value="Fornecedores" /><c:set var="menu" value="fornecedores" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Fornecedores</h1>
<div class="layout-gestao">
    <section class="cartao">
        <h2>Novo fornecedor</h2>
        <form method="post" action="${ctx}/admin/fornecedores" class="formulario">
            <input type="hidden" name="acao" value="criar">
            <label>Nome <input name="nome" required placeholder="Sementes do Sul, Lda"></label>
            <label>Tipo
                <select name="tipo" required>
                    <c:forEach var="t" items="${tipos}"><option value="${t}">${t}</option></c:forEach>
                </select>
            </label>
            <label>Contacto <input name="contacto" required placeholder="+258 84 000 0000"></label>
            <label>Província <input name="provincia" required placeholder="Maputo"></label>
            <button type="submit" class="botao">Criar fornecedor</button>
        </form>
    </section>
    <section class="cartao">
        <h2>Lista <small class="suave">(${fn:length(fornecedores)})</small></h2>
        <c:choose>
            <c:when test="${empty fornecedores}"><t:vazio titulo="Sem fornecedores">Crie o primeiro fornecedor no formulário.</t:vazio></c:when>
            <c:otherwise>
                <div class="tabela-rolar">
                    <table class="tabela">
                        <thead><tr><th>#</th><th>Nome</th><th>Tipo</th><th>Contacto</th><th>Província</th><th></th></tr></thead>
                        <tbody>
                        <c:forEach var="f" items="${fornecedores}">
                            <tr>
                                <td class="suave">${f.id}</td>
                                <td><strong><c:out value="${f.nome}" /></strong></td>
                                <td><span class="etiqueta">${f.tipo}</span></td>
                                <td><c:out value="${f.contacto}" /></td>
                                <td><c:out value="${f.provincia}" /></td>
                                <td>
                                    <form method="post" action="${ctx}/admin/fornecedores">
                                        <input type="hidden" name="acao" value="remover"><input type="hidden" name="id" value="${f.id}">
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

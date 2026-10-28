<%@ page isErrorPage="true" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}" />
<c:set var="titulo" value="Erro ${codigo}" />
<%@ include file="_cabecalho.jspf" %>
<section class="erro-pagina">
    <p class="erro-codigo">${codigo}</p>
    <c:choose>
        <c:when test="${codigo eq 403}">
            <h1>Acesso negado</h1>
            <p>Não tem permissão para aceder a esta página.</p>
        </c:when>
        <c:when test="${codigo eq 404}">
            <h1>Página não encontrada</h1>
            <p>O endereço que procurou não existe ou foi removido.</p>
        </c:when>
        <c:otherwise>
            <h1>Ocorreu um erro inesperado</h1>
            <p>Tente novamente. Se o problema persistir, contacte o administrador.</p>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty requestScope['jakarta.servlet.error.message'] and codigo ne 500}">
        <p class="suave"><c:out value="${requestScope['jakarta.servlet.error.message']}" /></p>
    </c:if>
    <a class="botao" href="${ctx}/">Voltar ao início</a>
</section>
<%@ include file="_rodape.jspf" %>

<%@ tag pageEncoding="UTF-8" description="Cabeçalho de coluna ordenável (usa tipo/ordem/dir do pedido)" %>
<%@ attribute name="campo" required="true" %>
<%@ attribute name="rotulo" required="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ativo" value="${requestScope.ordem eq campo}" />
<c:set var="novaDir" value="${ativo and requestScope.dir eq 'asc' ? 'desc' : 'asc'}" />
<a class="ordenar${ativo ? ' ativo' : ''}" href="?tipo=${requestScope.tipo}&amp;ordem=${campo}&amp;dir=${novaDir}">${rotulo}<c:if test="${ativo}"><span class="seta${requestScope.dir eq 'desc' ? ' seta-desc' : ''}" aria-hidden="true"></span></c:if></a>

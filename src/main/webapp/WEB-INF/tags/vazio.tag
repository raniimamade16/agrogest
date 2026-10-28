<%@ tag pageEncoding="UTF-8" description="Estado vazio de uma lista" %>
<%@ attribute name="titulo" required="true" %>
<div class="vazio">
    <p class="vazio-titulo">${titulo}</p>
    <p><jsp:doBody /></p>
</div>

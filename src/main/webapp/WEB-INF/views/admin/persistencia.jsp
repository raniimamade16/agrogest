<c:set var="titulo" value="Persistência" /><c:set var="menu" value="persistencia" />
<%@ include file="../_cabecalho.jspf" %>
<h1>Persistência em ficheiro</h1>
<div class="duas-colunas">
    <section class="cartao">
        <h2>Estado do ficheiro</h2>
        <dl class="ficha">
            <div><dt>Ficheiro</dt><dd><code>${ficheiro}</code></dd></div>
            <div><dt>Existe</dt><dd><span class="etiqueta ${existe ? 'etiqueta-ok' : 'etiqueta-aviso'}">${existe ? 'Sim' : 'Não'}</span></dd></div>
            <c:if test="${existe}">
                <div><dt>Última gravação</dt><dd>${ag:dataHora(ultimaGravacao)}</dd></div>
                <div><dt>Tamanho</dt><dd>${ag:num(tamanho / 1024)} KB</dd></div>
            </c:if>
        </dl>
        <p class="suave">O estado é gravado automaticamente quando o Tomcat encerra e restaurado no arranque.
            A escrita é atómica: grava-se para <code>.tmp</code>, guarda-se a versão anterior em <code>.bak</code>
            e só depois se renomeia.</p>
    </section>
    <section class="cartao">
        <h2>Ações manuais</h2>
        <div class="lista-acoes">
            <form method="post" action="${ctx}/admin/persistencia">
                <input type="hidden" name="acao" value="gravar">
                <button type="submit" class="botao botao-largo">Gravar agora</button>
                <small class="suave">Serializa todo o estado em memória para o ficheiro.</small>
            </form>
            <form method="post" action="${ctx}/admin/persistencia">
                <input type="hidden" name="acao" value="restaurar">
                <button type="submit" class="botao botao-secundario botao-largo" ${existe ? '' : 'disabled'}>Restaurar do ficheiro</button>
                <small class="suave">Descarta as alterações não gravadas e recarrega o ficheiro.</small>
            </form>
            <form method="post" action="${ctx}/admin/persistencia">
                <input type="hidden" name="acao" value="eliminar">
                <button type="submit" class="botao botao-perigo botao-largo" ${existe ? '' : 'disabled'}>Eliminar ficheiro</button>
                <small class="suave">Apaga o ficheiro e o backup. Os dados em memória mantêm-se.</small>
            </form>
        </div>
    </section>
</div>
<%@ include file="../_rodape.jspf" %>

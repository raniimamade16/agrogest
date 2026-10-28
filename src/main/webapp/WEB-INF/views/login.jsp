<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Entrar · AgroGest</title>
    <link rel="icon" href="${ctx}/img/logo.svg?v=${applicationScope.versao}" type="image/svg+xml">
    <link rel="stylesheet" href="${ctx}/css/style.css?v=${applicationScope.versao}">
</head>
<body class="pagina-entrada">
<div class="entrada">
    <section class="entrada-marca">
        <img src="${ctx}/img/logo.svg?v=${applicationScope.versao}" alt="" width="56" height="56">
        <h1>Agro<strong>Gest</strong></h1>
        <p>Fornecedores, campanhas, colheitas e vendas da sua machamba num só lugar.</p>
        <ul>
            <li>Catálogo de sementes, fertilizantes e ferramentas</li>
            <li>Facturas com IVA emitidas na hora</li>
            <li>Relatórios de clientes e vendas</li>
        </ul>
    </section>
    <section class="entrada-form">
        <h2>Entrar</h2>
        <c:if test="${not empty sessionScope.flash}">
            <div class="alerta alerta-${sessionScope.flash.tipo}"><p><c:out value="${sessionScope.flash.texto}" /></p></div>
            <c:remove var="flash" scope="session" />
        </c:if>
        <c:if test="${not empty erro}"><div class="alerta alerta-erro" role="alert"><p>${erro}</p></div></c:if>
        <form method="post" action="${ctx}/login" class="formulario">
            <label>Email
                <input type="email" name="email" value="<c:out value='${email}'/>" required autofocus autocomplete="username">
            </label>
            <label>Palavra-passe
                <input type="password" name="password" required autocomplete="current-password">
            </label>
            <button type="submit" class="botao botao-largo">Entrar</button>
        </form>
        <p class="entrada-rodape">Ainda não tem conta? <a href="${ctx}/registo">Registe-se como cliente</a></p>
    </section>
</div>
</body>
</html>

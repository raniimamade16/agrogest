<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Criar conta · AgroGest</title>
    <link rel="icon" href="${ctx}/img/logo.svg?v=${applicationScope.versao}" type="image/svg+xml">
    <link rel="stylesheet" href="${ctx}/css/style.css?v=${applicationScope.versao}">
</head>
<body class="pagina-entrada">
<div class="entrada">
    <section class="entrada-marca">
        <img src="${ctx}/img/logo.svg?v=${applicationScope.versao}" alt="" width="56" height="56">
        <h1>Agro<strong>Gest</strong></h1>
        <p>Crie a sua conta de cliente para comprar sementes, fertilizantes e ferramentas e consultar as suas facturas.</p>
    </section>
    <section class="entrada-form">
        <h2>Criar conta</h2>
        <c:if test="${not empty erro}"><div class="alerta alerta-erro" role="alert"><p><c:out value="${erro}" /></p></div></c:if>
        <form method="post" action="${ctx}/registo" class="formulario">
            <label>Nome completo
                <input name="nome" value="<c:out value='${param.nome}'/>" required autocomplete="name">
            </label>
            <label>Email
                <input type="email" name="email" value="<c:out value='${param.email}'/>" required autocomplete="email">
            </label>
            <div class="linha-campos">
                <label>Palavra-passe
                    <input type="password" name="password" minlength="4" required autocomplete="new-password">
                </label>
                <label>Confirmar
                    <input type="password" name="confirmacao" minlength="4" required autocomplete="new-password">
                </label>
            </div>
            <label>Telefone
                <input type="tel" name="telefone" value="<c:out value='${param.telefone}'/>" placeholder="+258 84 000 0000" required autocomplete="tel">
            </label>
            <label>Endereço
                <input name="endereco" value="<c:out value='${param.endereco}'/>" placeholder="Av. Eduardo Mondlane, Nampula" required autocomplete="street-address">
            </label>
            <button type="submit" class="botao botao-largo">Criar conta</button>
        </form>
        <p class="entrada-rodape">Já tem conta? <a href="${ctx}/login">Entrar</a></p>
    </section>
</div>
</body>
</html>

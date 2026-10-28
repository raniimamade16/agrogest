package mz.agrogest.model;

/**
 * Os dois tipos de conta que existem no sistema.
 * O ADMIN é a conta fixa do agricultor, criada quando a aplicação arranca.
 * O CLIENTE é quem se regista sozinho na página de registo para poder comprar.
 */
public enum Perfil {
    ADMIN, CLIENTE
}

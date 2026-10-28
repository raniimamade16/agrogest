package mz.agrogest.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de palavras-passe com PBKDF2-HMAC-SHA256 e salt aleatório.
 * Formato guardado: {@code iteracoes:saltBase64:hashBase64}.
 */
public final class PasswordUtil {

    private static final int ITERACOES = 65_536;
    private static final int TAMANHO_BITS = 256;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hash(String password) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        byte[] h = pbkdf2(password.toCharArray(), salt, ITERACOES);
        Base64.Encoder b64 = Base64.getEncoder();
        return ITERACOES + ":" + b64.encodeToString(salt) + ":" + b64.encodeToString(h);
    }

    public static boolean verificar(String password, String guardado) {
        if (password == null || guardado == null) {
            return false;
        }
        String[] partes = guardado.split(":");
        if (partes.length != 3) {
            return false;
        }
        Base64.Decoder b64 = Base64.getDecoder();
        byte[] salt = b64.decode(partes[1]);
        byte[] esperado = b64.decode(partes[2]);
        byte[] calculado = pbkdf2(password.toCharArray(), salt, Integer.parseInt(partes[0]));
        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password, salt, iteracoes, TAMANHO_BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 indisponível", e);
        }
    }
}

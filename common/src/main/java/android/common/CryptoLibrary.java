package android.common;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

/**
 * Created by gerard on 4/16/16.
 */
public class CryptoLibrary {

    //Encryption
    private static CryptoLibrary library;
    public final static String RSA = "RSA";
    public static PublicKey uk;
    public static PrivateKey rk;

    protected CryptoLibrary() {
        // Exists only to defeat instantiation.
    }
    public static CryptoLibrary getLibrary() {
        if(library == null) {
            library = new CryptoLibrary();
        }
        return library;
    }

    public static void generateKey() throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);

        gen.initialize(512, new SecureRandom());

        KeyPair keyPair = gen.generateKeyPair();

        uk = keyPair.getPublic();

        rk = keyPair.getPrivate();

    }
}
package android.common;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by gerard on 4/16/16.
 */
public class CryptoLibrary {

    //Encryption
    private static CryptoLibrary library;
    //public final static String RSA = "RSA";
    //public static PublicKey uk;
    //public static PrivateKey rk;

    protected CryptoLibrary() {
        // Exists only to defeat instantiation.
    }
    public static CryptoLibrary getLibrary() {
        if(library == null) {
            library = new CryptoLibrary();
        }
        return library;
    }

   /* public static void generateKey() throws Exception {

        KeyPairGenerator gen = KeyPairGenerator.getInstance(RSA);

        gen.initialize(512, new SecureRandom());

        KeyPair keyPair = gen.generateKeyPair();

        getLibrary().uk = keyPair.getPublic();

        getLibrary().rk = keyPair.getPrivate();

    }



    private static byte[] encrypt(String text, PublicKey pubRSA)

            throws Exception {

        Cipher cipher = Cipher.getInstance(CryptoLibrary.getLibrary().RSA);

        cipher.init(Cipher.ENCRYPT_MODE, pubRSA);

        return cipher.doFinal(text.getBytes());

    }


    public final static String encrypt(String text) {

        try {

            return byte2hex(encrypt(text, CryptoLibrary.getLibrary().uk));

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }






    private static String byte2hex(byte[] b) {

        String hs = "";

        String stmp = "";

        for (int n = 0; n < b.length; n++) {

            stmp = Integer.toHexString(b[n] & 0xFF);

            if (stmp.length() == 1)

                hs += ("0" + stmp);

            else

                hs += stmp;

        }

        return hs.toUpperCase();

    }


    public static String decrypt(String data) {

        try {

            return new String(decrypt(hex2byte(data.getBytes())));

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }


    private static byte[] decrypt(byte[] src) throws Exception {

        Cipher cipher = Cipher.getInstance(library.RSA);

        cipher.init(Cipher.DECRYPT_MODE, CryptoLibrary.getLibrary().rk);

        return cipher.doFinal(src);

    }

    private static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)

            throw new IllegalArgumentException("hello");


        byte[] b2 = new byte[b.length / 2];


        for (int n = 0; n < b.length; n += 2) {

            String item = new String(b, n, 2);

            b2[n / 2] = (byte) Integer.parseInt(item, 16);

        }

        return b2;

    }*/


    /**
     * Usage:
     * <pre>
     * String crypto = SimpleCrypto.encrypt(masterpassword, cleartext)
     * ...
     * String cleartext = SimpleCrypto.decrypt(masterpassword, crypto)
     * </pre>
     * @author ferenc.hechler
     */

        public static String encrypt(String seed, String cleartext) throws Exception {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] result = encrypt(rawKey, cleartext.getBytes());
            return toHex(result);
        }

        public static String decrypt(String seed, String encrypted) throws Exception {
            byte[] rawKey = getRawKey(seed.getBytes());
            byte[] enc = toByte(encrypted);
            byte[] result = decrypt(rawKey, enc);
            return new String(result);
        }

        private static byte[] getRawKey(byte[] seed) throws Exception {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
            sr.setSeed(seed);
            kgen.init(128, sr); // 192 and 256 bits may not be available
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            return raw;
        }


        private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(clear);
            return encrypted;
        }

        private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] decrypted = cipher.doFinal(encrypted);
            return decrypted;
        }

        public static String toHex(String txt) {
            return toHex(txt.getBytes());
        }
        public static String fromHex(String hex) {
            return new String(toByte(hex));
        }

        public static byte[] toByte(String hexString) {
            int len = hexString.length()/2;
            byte[] result = new byte[len];
            for (int i = 0; i < len; i++)
                result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
            return result;
        }

        public static String toHex(byte[] buf) {
            if (buf == null)
                return "";
            StringBuffer result = new StringBuffer(2*buf.length);
            for (int i = 0; i < buf.length; i++) {
                appendHex(result, buf[i]);
            }
            return result.toString();
        }
        private final static String HEX = "0123456789ABCDEF";
        private static void appendHex(StringBuffer sb, byte b) {
            sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
        }

    }

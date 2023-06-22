package Cryptography;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class Rsa {
    private static KeyPairGenerator kpg;
    private static KeyPair kp;
    private static String privateKeypath = "resources/Keys/private.key";
    private static String publicKeypath = "resources/Keys/public.key";

    public static void encrypt(String filePath) throws Exception {
        // generate the key value pair
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        kp = kpg.generateKeyPair();
        saveKeys();
        // encrypt the file
        PrivateKey pvt = kp.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pvt);
        FileInputStream in = new FileInputStream(filePath);
        FileOutputStream out = new FileOutputStream("resources/database/filmesEncrypt.db");
        processFile(cipher, in, out);
        File f = new File(filePath);
        f.delete();

    }

    public static String decrypt(String filePath) throws Exception {
        String dbFilePath = "resources/database/filmes.db";
        File dbFile = new File(dbFilePath);
        dbFile.createNewFile(); // if file already exists will do nothing
        PublicKey pub;
        try {
            pub = getPublicKey();
        } catch (InvalidKeySpecException e) {
            return dbFilePath;
        }
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, pub);

        FileInputStream in = new FileInputStream(filePath);
        FileOutputStream out = new FileOutputStream(dbFile);
        try {
            processFile(cipher, in, out);
        } catch (javax.crypto.BadPaddingException e) {
            return dbFilePath;
        }
        return dbFilePath;
    }

    private static void processFile(Cipher ci, InputStream in, OutputStream out)
            throws javax.crypto.IllegalBlockSizeException,
            javax.crypto.BadPaddingException,
            java.io.IOException {
        byte[] ibuf = new byte[1024];
        int len;
        while ((len = in.read(ibuf)) != -1) {
            byte[] obuf = ci.update(ibuf, 0, len);
            if (obuf != null)
                out.write(obuf);
        }
        byte[] obuf = ci.doFinal();
        if (obuf != null)
            out.write(obuf);
    }

    private static void saveKeys() {
        try (FileOutputStream out = new FileOutputStream(privateKeypath)) {
            out.write(kp.getPrivate().getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (FileOutputStream out = new FileOutputStream(publicKeypath)) {
            out.write(kp.getPublic().getEncoded());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PublicKey getPublicKey() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(publicKeypath));
        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);
        return pub;
    }

    @SuppressWarnings("unused")
    private static PrivateKey getPrivateKey() throws Exception {
        byte[] bytes = Files.readAllBytes(Paths.get(privateKeypath));
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pvt = kf.generatePrivate(ks);
        return pvt;
    }

}

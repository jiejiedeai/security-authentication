package com.ht.authentication.util;

import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public class KeyStoreUtils {

    private static KeyStore keyStore;

    private static final String JDK_PATH ="C:\\Users\\qp\\oauth2.jks";

    private static final String JKS_PASSWORD ="oauth2";

    private static final String CERTALIAS ="oauth2";

    private static final String CERTPASSWORD ="oauth2";

    public KeyStoreUtils(){
        try {
            this.keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(JDK_PATH), JKS_PASSWORD.toCharArray());
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            e.printStackTrace();
        }finally {
        }

    }


    public String getPrivateKey(KeyStore keyStore) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(CERTALIAS, CERTPASSWORD.toCharArray());
        return new BASE64Encoder().encode(privateKey.getEncoded());
    }

    public String getPublicKey(KeyStore keyStore) throws KeyStoreException {
        PublicKey publicKey = keyStore.getCertificate(CERTALIAS).getPublicKey();
        return new BASE64Encoder().encode(publicKey.getEncoded());
    }

    public static void main(String[] args) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        KeyStoreUtils k = new KeyStoreUtils();
        String privateKey = k.getPrivateKey(KeyStoreUtils.keyStore);
        System.out.println(privateKey);
    }
}

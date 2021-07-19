package com.aragh.resource.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class PemUtils {


    public static KeyPair getKeyPair(Path privateKeyPath, Path publicKeyPath, String algorithm) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        PrivateKey privateKey = null;
        PublicKey publicKey = null;

        if (privateKeyPath != null) {
            byte[] encodedPrivateKey = IOUtils.toByteArray(privateKeyPath.toUri());
            String key = new String(encodedPrivateKey, 0, encodedPrivateKey.length).replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)----", "")
                    .replaceAll("\r\n", "")
                    .replaceAll("\n", "")
                    .trim();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key));
            privateKey= keyFactory.generatePrivate(spec);
       }

        if (publicKeyPath != null) {
            byte[] encodedPublicKey = IOUtils.toByteArray(publicKeyPath.toUri());
            String key = new String(encodedPublicKey, 0, encodedPublicKey.length).replaceAll("-----BEGIN (.*)-----", "")
                    .replaceAll("-----END (.*)----", "")
                    .replaceAll("\r\n", "")
                    .replaceAll("\n", "")
                    .trim();
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
            publicKey = keyFactory.generatePublic(publicKeySpec);
        }

        return new KeyPair(publicKey, privateKey);
    }
}

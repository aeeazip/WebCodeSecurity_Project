package com.wcs.project.service;

import com.wcs.project.dto.key.AsymmetricDto;
import com.wcs.project.dto.key.SymmetricDto;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

@Service
public class KeyService {
    private static int RSA_KEY_SIZE = 1024;
    private static int AES_KEY_SIZE = 128;

    public static final int getRsaKeySize() {
        return RSA_KEY_SIZE;
    }
    public static final int getAesKeySize() {
        return AES_KEY_SIZE;
    }

    // 비대칭키 생성 (공개키 + 개인키)
    public void generateAsymmetricKey(AsymmetricDto request) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA"); // key 알고리즘 지정
        keyPairGen.initialize(getRsaKeySize()); // keysize 지정
        KeyPair keyPair = keyPairGen.generateKeyPair(); // KeyPair 생성

        PublicKey publicKey = keyPair.getPublic(); // 공개키 획득
        PrivateKey privateKey = keyPair.getPrivate(); // 개인키 획득

        // 공개키 저장
        String publicKeyFile = request.getPublicKeyFile();
        try (FileOutputStream fos = new FileOutputStream(publicKeyFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(publicKey); // 자바 직렬화로 PublicKey 객체 출력
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 개인키 저장
        String privateKeyFile = request.getPrivateKeyFile();
        try (FileOutputStream fos = new FileOutputStream(privateKeyFile);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(privateKey); // 자바 직렬화로 PrivateKey 객체 출력
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 대칭키 생성 (비밀키)
    public void generateSymmetricKey(SymmetricDto request) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(getAesKeySize());  // key의 길이 지정
        Key secretKey = keyGen.generateKey();   // 비밀키 생성

        // 비밀키 저장
        String secretKeyFile = request.getSecretKeyFile();
        try (FileOutputStream fos = new FileOutputStream(secretKeyFile)) {
            try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(secretKey); // 생성된 비밀키 객체를 파일에 저장
            }
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

}

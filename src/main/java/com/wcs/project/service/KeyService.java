package com.wcs.project.service;

import com.wcs.project.dto.key.AsymmetricDto;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.*;

@Service
public class KeyService {
    // 비대칭키 생성 (공개키 + 개인키)
    public boolean generateAsymmetricKey(AsymmetricDto asymmetricDto) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA"); // key 알고리즘 지정
        keyPairGen.initialize(1024); // keysize 지정
        KeyPair keyPair = keyPairGen.generateKeyPair(); // KeyPair 생성

        PublicKey publicKey = keyPair.getPublic(); // 공개키 획득
        PrivateKey privateKey = keyPair.getPrivate(); // 개인키 획득

        // 공개키 저장
        String publicKeyFile = asymmetricDto.getPublicKeyFile();
        try (FileOutputStream fostream = new FileOutputStream(publicKeyFile);
             ObjectOutputStream oostream = new ObjectOutputStream(fostream)) {
                oostream.writeObject(publicKey); // 자바 직렬화로 PublicKey 객체 출력
        } catch (IOException e) {
            return false;
        }

        // 개인키 저장
        String privateKeyFile = asymmetricDto.getPrivateKeyFile();
        try (FileOutputStream fostream = new FileOutputStream(privateKeyFile);
             ObjectOutputStream oostream = new ObjectOutputStream(fostream)) {
                oostream.writeObject(privateKey); // 자바 직렬화로 PrivateKey 객체 출력
        } catch (IOException e) {
            return false;
        }

        return true; // 비대칭키 발급 성공
    }


}

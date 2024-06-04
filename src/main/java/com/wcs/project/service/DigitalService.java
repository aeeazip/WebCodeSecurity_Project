package com.wcs.project.service;

import com.wcs.project.dto.data.DataSet;
import com.wcs.project.dto.data.SendDataSet;
import com.wcs.project.dto.envelope.SignDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import java.io.*;
import java.security.*;

@Service
@Slf4j
public class DigitalService {
    // 비대칭 암호화 : 공개키, 개인키(사설키)
    // 대칭 암호화 : 대칭키(비밀키)

    public boolean digitalEnvelopeSign(SignDto request) {
        String dataFile = request.getDataFile(); // 데이터 파일명
        String data = request.getData(); // 데이터 내용 (평문 준비)
        String sPrivateKeyFile = request.getPrivateKeyFile(); // 보내는 사람의 개인키 파일명
        String sPublicKeyFile = request.getPublicKeyFile(); // 보내는 사람의 공개키 파일명
        String sSecretKeyFile = request.getSecretKeyFile(); // 보내는 사람의 대칭키 파일명
        String rPublicKeyFile = request.getSenderPublicKeyFile(); // 받는 사람의 공개키 파일명
        String sendFile = request.getSendFile(); // 보내고 싶은 파일명

        try {
            // 1. 원문 파일 생성
            byte[] readData = data.getBytes(); // 데이터 내용 byte형 배열로 변경
            try (FileOutputStream fos = new FileOutputStream(dataFile)) {
                fos.write(readData);
            }

            // 2. 원문 파일 내용에 대한 해시값 생성
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(readData);
            byte[] messageHash = messageDigest.digest(); // 해시값 계산 후 반환

            log.info("원문 파일에 대한 해시값을 생성했습니다.");

            // 3. 보내는 사람의 개인키 준비
            PrivateKey sPrivateKey;
            try (FileInputStream fis = new FileInputStream(sPrivateKeyFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                sPrivateKey = (PrivateKey) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                return false;
            }

            // 4. 보내는 사람의 개인키로 해시값 암호화하여 전자서명 생성
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(sPrivateKey);
            sig.update(messageHash);
            byte[] signature = sig.sign(); // 전자서명 생성

            String signedFile = request.getSignedFile(); // 서명을 저장할 파일 이름
            try (FileOutputStream fos = new FileOutputStream(signedFile)) {
                fos.write(signature);
            } catch (IOException e) {
                return false;
            }

            log.info("전자서명을 파일에 저장했습니다.");

            // 5. 보내는 사람의 비밀키 준비
            Key sSecretKey;
            try (FileInputStream fis = new FileInputStream(sSecretKeyFile);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                sSecretKey = (Key) ois.readObject();
            } catch(ClassNotFoundException | IOException e) {
                return false;
            }

            // 6. [원본 데이터 파일, 전자서명, 보내는 사람의 공개키]를 보내는 사람의 대칭키(비밀키)로 암호화
            DataSet dataSet = DataSet.builder()
                    .dataFile(dataFile) // 원본 데이터 파일명
                    .signature(signature) // 전자서명
                    .sPublicKeyFile(sPublicKeyFile) // 보내는 사람의 공개키
                    .build();

            // DataSet을 byte[] 타입으로 변형
            byte[] dataSetByte = null;
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(dataSet);
                dataSetByte = bos.toByteArray();
            } catch (IOException e) {
                return false;
            }

            /*
                cipher.doLogic()
                - 암호화 결과를 파일로 저장하지 않고 byte[]로 반환받기 위해 사용
                - 매개변수가 byte[] 타입이므로 Dataset을 byte[]로 변형
             */
            Cipher cipher1 = Cipher.getInstance("DES");
            cipher1.init(Cipher.ENCRYPT_MODE, sSecretKey);
            byte[] encryptedSet = cipher1.doFinal(dataSetByte);

            // 7. 받는 사람의 공개키 준비
            PublicKey rPublicKey;
            try (FileInputStream fis = new FileInputStream(rPublicKeyFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                rPublicKey = (PublicKey) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                return false;
            }

            // 8. 보내는 사람의 비밀키를 받는 사람의 공개키로 암호화 -> 전자봉투 생성
            Cipher cipher2 = Cipher.getInstance("RSA");
            cipher2.init(Cipher.ENCRYPT_MODE, rPublicKey);
            byte[] digitalEnvelope = cipher2.doFinal(sSecretKey.getEncoded());

            // 9. [암호문, 전자봉투]를 받는 사람에게 전송 (파일에 저장)
            SendDataSet sendDataSet = SendDataSet.builder()
                    .encryptedSet(encryptedSet)
                    .digitalEnvelope(digitalEnvelope)
                    .build();

            try (FileOutputStream fos = new FileOutputStream(sendFile);
                ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(sendDataSet);
            } catch (IOException e) {
                return false;
            }

            return true;
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException |
                 NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            return false;
        }
    }
}

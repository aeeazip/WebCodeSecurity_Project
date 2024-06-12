package com.wcs.project.service;

import com.wcs.project.dto.data.DataSet;
import com.wcs.project.dto.data.SendDataSet;
import com.wcs.project.dto.envelope.SignDto;
import com.wcs.project.dto.envelope.VerifyDto;
import com.wcs.project.exception.SignException;
import com.wcs.project.exception.VerifyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Arrays;

@Service
@Slf4j
public class DigitalService {
    // 비대칭 암호화 : 공개키, 개인키(사설키) 사용 | 대칭 암호화 : 대칭키(비밀키) 사용

    // 전자봉투 생성
    public void digitalEnvelopeSign(SignDto request) {
        String dataFile = request.getDataFile(); // 데이터 파일명
        char[] data = request.getData(); // 데이터 내용 (평문 준비)
        String sPrivateKeyFile = request.getPrivateKeyFile(); // 보내는 사람의 개인키 파일명
        String sPublicKeyFile = request.getPublicKeyFile(); // 보내는 사람의 공개키 파일명
        String sSecretKeyFile = request.getSecretKeyFile(); // 보내는 사람의 대칭키 파일명
        String rPublicKeyFile = request.getReceiverPublicKeyFile(); // 받는 사람의 공개키 파일명
        String sendFile = request.getSendFile(); // 보내고 싶은 파일명

        try {
            // 1. 원문 파일 생성
            byte[] readData = new String(data).getBytes(); // 데이터 내용 byte형 배열로 변경
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
                throw new RuntimeException(e);
            }

            // 4. 보내는 사람의 개인키로 해시값 암호화하여 전자서명 생성
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initSign(sPrivateKey); // 서명 초기화 (보내는 사람 개인키로 암호화)
            sig.update(messageHash);
            byte[] signature = sig.sign(); // 전자서명 생성 -> 암호화된 해시값 반환

            String signedFile = request.getSignedFile(); // 서명을 저장할 파일 이름
            try (FileOutputStream fos = new FileOutputStream(signedFile)) {
                fos.write(signature);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            log.info("전자서명을 파일에 저장했습니다.");

            // 5. 보내는 사람의 비밀키 준비
            Key sSecretKey;
            try (FileInputStream fis = new FileInputStream(sSecretKeyFile);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                sSecretKey = (Key) ois.readObject();
            } catch(ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }

            // 6. [원본 데이터 파일, 전자서명, 보내는 사람의 공개키]를 보내는 사람의 대칭키(비밀키)로 암호화
            DataSet dataSet = DataSet.builder()
                    .dataFile(dataFile) // 원본 데이터 파일명
                    .signature(signature) // 전자서명
                    .sPublicKeyFile(sPublicKeyFile) // 보내는 사람의 공개키
                    .build();

            // DataSet을 byte[] 타입으로 변형
            byte[] dataSetByte = new byte[0];
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(dataSet);
                dataSetByte = bos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            /*
                cipher.doLogic()
                - 암호화 결과를 파일로 저장하지 않고 byte[]로 반환받기 위해 사용
                - 매개변수가 byte[] 타입이므로 Dataset을 byte[]로 변형
             */
            Cipher cipher1 = Cipher.getInstance("AES");
            cipher1.init(Cipher.ENCRYPT_MODE, sSecretKey);
            byte[] encryptedSet = cipher1.doFinal(dataSetByte); // 대칭키로 암호화

            // 7. 받는 사람의 공개키 준비
            PublicKey rPublicKey;
            try (FileInputStream fis = new FileInputStream(rPublicKeyFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                rPublicKey = (PublicKey) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
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
                throw new RuntimeException(e);
            }

            Arrays.fill(data, ' '); // 민감 데이터 지우기
            log.info("전자봉투 생성에 성공했습니다.");
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException |
                 NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new SignException("전자봉투 생성에 실패했습니다.");
        }
    }

    // 전자봉투 개봉
    public VerifyDto.Response digitalEnvelopeVerify(VerifyDto.Request request) {
        String rPrivateKeyFile = request.getPrivateKeyFile(); // 받는 사람의 개인키
        String receiveFile = request.getReceiveFile(); // 개봉하고자 하는 파일명

        try {
            // 1. [암호문, 전자봉투] 역직렬화
            SendDataSet sendDataSet;
            try (FileInputStream fis = new FileInputStream(receiveFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                sendDataSet = (SendDataSet) ois.readObject();
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException(e);
            }

            // 2. 받는 사람의 개인키 준비
            PrivateKey rPrivateKey;
            try (FileInputStream fis = new FileInputStream(rPrivateKeyFile);
                ObjectInputStream ois = new ObjectInputStream(fis)) {
                    rPrivateKey = (PrivateKey) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // 2. 전자봉투를 받는 사람의 개인키로 복호화 -> 보낸 사람의 비밀키 획득
            byte[] digitalEnvelope = sendDataSet.getDigitalEnvelope();
            Cipher cipher1 = Cipher.getInstance("RSA");
            cipher1.init(Cipher.DECRYPT_MODE, rPrivateKey);
            byte[] decryptedKeyBytes = cipher1.doFinal(digitalEnvelope);

            /*
                new SecretKeySpec(복호화된 전자봉투 결과, 알고리즘)
                - 복호화된 byte 배열을 사용하여 SecretKey 재생성
                -> 보낸 사람의 비밀키 획득
             */
            SecretKey sSecretKey = new SecretKeySpec(decryptedKeyBytes, "AES"); // 복호화된 byte 배열을 사용하여 SecretKey 재생성

            // 3. 보낸 사람의 비밀키로 암호문 복호화
            byte[] encryptedSet = sendDataSet.getEncryptedSet(); // 암호문
            Cipher cipher2 = Cipher.getInstance("AES");
            cipher2.init(Cipher.DECRYPT_MODE, sSecretKey);
            byte[] dataSetByte = cipher2.doFinal(encryptedSet);

            // 4. 복호화한 암호문을 DataSet 타입으로 변경 (byte[] -> DataSet)
            DataSet dataSet;
            try(ByteArrayInputStream bis = new ByteArrayInputStream(dataSetByte);
                ObjectInputStream ois = new ObjectInputStream(bis)) {
                dataSet = (DataSet) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // 5. 보낸 사람의 공개키 준비
            String sPublicKeyFile = dataSet.getSPublicKeyFile();
            PublicKey sPublicKey;
            try (FileInputStream fis = new FileInputStream(sPublicKeyFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                sPublicKey = (PublicKey) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // 6. 원문으로 해시 생성
            String dataFile = dataSet.getDataFile();
            byte[] readData = new byte[0];
            try(FileInputStream fis = new FileInputStream(dataFile)) {
                readData = fis.readAllBytes();
            }

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update(readData);
            byte[] messageHash = messageDigest.digest(); // 해시값 계산 후 반환

            // 7. 암호화된 해시값 복호화 및 서명 검증
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(sPublicKey); // 검증에 사용할 공개키 지정
            sig.update(messageHash);

            // 8. 원문으로 생성한 해시 vs 복호화된 해시값 비교
            byte[] signature = dataSet.getSignature(); // 암호화된 해시값
            boolean result = sig.verify(signature); // 원문으로 만든 해시값과 전자서명에서 꺼낸 해시값 비교

            if(result) {
                log.info("전자봉투 개봉에 성공했습니다.");
                String data = new String(readData);
                data = data.replaceAll(", ", "");
                return new VerifyDto.Response(dataFile, data);
            } else {
                log.info("전자봉투 개봉에 실패했습니다.");
                throw new VerifyException("전자봉투 개봉에 실패했습니다.");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | IOException |
                 NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException e) {
            throw new VerifyException("전자봉투 개봉에 실패했습니다.");
        }
    }
}

package com.wcs.project.dto.envelope;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SignDto {
    @NotEmpty(message = "원본 데이터 파일명을 입력해주세요.")
    private String dataFile; // 원본 데이터 파일명

    @NotEmpty(message = "데이터 내용을 입력해주세요.")
    private char[] data; // 데이터 내용

    @NotEmpty(message = "개인키 파일명을 입력해주세요.")
    private String privateKeyFile; // 데이터 내용 암호화에 사용할 개인키 파일명

    @NotEmpty(message = "서명을 저장할 파일명을 입력해주세요.")
    private String signedFile; // 서명을 저장할 파일명

    @NotEmpty(message = "공개키 파일명을 입력해주세요.")
    private String publicKeyFile; // 공개키 파일명

    @NotEmpty(message = "대칭키 파일명을 입력해주세요.")
    private String secretKeyFile; // 대칭키(비밀키) 파일명

    @NotEmpty(message = "받는 사람의 공개키 파일명을 입력해주세요.")
    private String receiverPublicKeyFile; // 받는 사람의 공개키 파일명

    @NotEmpty(message = "전송할 파일명을 입력해주세요.")
    private String sendFile; // 전송할 파일명
}

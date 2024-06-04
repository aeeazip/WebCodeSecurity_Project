package com.wcs.project.dto.envelope;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
    철수 -> 영희

    1. 원문의 해시값을 철수의 개인키로 암호화
    2. 철수의 공개키, 암호화된 해시값, 원문을 철수의 대칭키(비밀키)로 암호화
    3. 철수의 대칭키(비밀키)를 영희의 공개키로 암호화
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignDto {
    @NotEmpty(message = "원본 데이터 파일명을 입력해주세요.")
    private String dataFile; // 원본 데이터 파일명

    @NotEmpty(message = "데이터 내용을 입력해주세요.")
    private String data; // 데이터 내용

    @NotEmpty(message = "개인키 파일명을 입력해주세요.")
    private String privateKeyFile; // 데이터 내용 암호화에 사용할 개인키 파일명

    @NotEmpty(message = "서명을 저장할 파일명을 입력해주세요.")
    private String signedFile; // 서명을 저장할 파일명

    @NotEmpty(message = "공개키 파일명을 입력해주세요.")
    private String publicKeyFile; // 공개키 파일명

    @NotEmpty(message = "대칭키 파일명을 입력해주세요.")
    private String secretKeyFile; // 대칭키(비밀키) 파일명

    @NotEmpty(message = "받는 사람의 공개키 파일명을 입력해주세요.")
    private String senderPublicKeyFile; // 받는 사람의 공개키 파일명

    @NotEmpty(message = "보내고 싶은 파일명을 입력해주세요.")
    private String sendFile; // 보낼 파일명
}

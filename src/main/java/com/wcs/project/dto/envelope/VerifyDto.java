package com.wcs.project.dto.envelope;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VerifyDto {
    @Getter
    @AllArgsConstructor
    public static class Request {
        @NotEmpty(message = "개봉할 파일명을 입력해주세요.")
        private String receiveFile; // 개봉할 파일명

        @NotEmpty(message = "개인키 파일명을 입력해주세요.")
        private String privateKeyFile; // 개인키 파일명

        @NotEmpty(message = "보낸 사람의 대칭키 파일명을 입력해주세요.")
        private String senderSecretKeyFile; // 보낸 사람의 대칭키(비밀키)
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        @NotEmpty
        private String dataFile; // 받은 파일명

        @NotEmpty
        private String data; // 파일 내용
    }
}

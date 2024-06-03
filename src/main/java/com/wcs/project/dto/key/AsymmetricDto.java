package com.wcs.project.dto.key;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AsymmetricDto {
    @NotEmpty(message = "공개키 파일 이름을 입력해주세요.")
    private String publicKeyFile; // 공개키 파일 이름

    @NotEmpty(message = "사설키 파일 이름을 입력해주세요.")
    private String privateKeyFile; // 개인키 파일 이름
}

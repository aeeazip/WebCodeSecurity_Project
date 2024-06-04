package com.wcs.project.dto.key;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SymmetricDto {
    @NotEmpty(message = "비밀키 파일 이름을 입력해주세요.")
    private String secretKeyFile; // 비밀키 파일 이름
}

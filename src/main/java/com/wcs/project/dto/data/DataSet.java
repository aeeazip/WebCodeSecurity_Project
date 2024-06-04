package com.wcs.project.dto.data;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class DataSet implements Serializable {
    @NotEmpty
    private String dataFile; // 원본 파일명

    @NotEmpty
    private byte[] signature; // 전자서명

    @NotEmpty
    private String sPublicKeyFile; // 보내는 사람의 공개키 파일명
}

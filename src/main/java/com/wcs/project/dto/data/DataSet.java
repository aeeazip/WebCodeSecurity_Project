package com.wcs.project.dto.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DataSet {
    private String dataFile; // 원본 파일명
    private byte[] signature; // 전자서명
    private String sPublicKeyFile; // 보내는 사람의 공개키 파일명
}

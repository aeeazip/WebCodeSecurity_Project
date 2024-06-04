package com.wcs.project.dto.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendDataSet {
    private byte[] encryptedSet; // 암호문
    private byte[] digitalEnvelope; // 전자봉투
}

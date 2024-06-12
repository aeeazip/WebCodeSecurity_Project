package com.wcs.project.dto.data;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@SuppressWarnings("serial")
public class SendDataSet implements Serializable {
    @NotEmpty
    private byte[] encryptedSet; // 암호문

    @NotEmpty
    private byte[] digitalEnvelope; // 전자봉투
}

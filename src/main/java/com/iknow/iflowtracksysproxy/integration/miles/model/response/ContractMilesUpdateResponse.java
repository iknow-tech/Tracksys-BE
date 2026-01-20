package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ContractMilesUpdateResponse {
    private String contractId;

    private List<String> updatedFields;

    private boolean read;

    private LocalDateTime updatedAt;
}

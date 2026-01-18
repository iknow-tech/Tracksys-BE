package com.iknow.iflowtracksysproxy.dto.request;

import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnassignDealerRequest {
    private String contractId;
    private String cancelledBy;
}

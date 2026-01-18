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
public class AssignDealerRequest {
    private String dealerId;

    private String dealerName;

    private String dealerContactId;

    private List<CustomerContractResponse> contracts;

    private String assignedBy;

}

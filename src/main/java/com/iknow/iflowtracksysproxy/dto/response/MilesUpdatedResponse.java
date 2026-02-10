package com.iknow.iflowtracksysproxy.dto.response;

import com.iknow.iflowtracksysproxy.integration.miles.model.response.NetAmountUpdateResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MilesUpdatedResponse {
    Boolean netAmountUpdateSuccess;
    Boolean otvUpdateSuccess;
    Boolean discountUpdateSuccess;
    Boolean creditApprovalUpdateSuccess;
    Boolean bulkProcessorSuccess;
    Boolean chassisNoUpdateSuccess;
    Boolean motorNoUpdateSuccess;


}

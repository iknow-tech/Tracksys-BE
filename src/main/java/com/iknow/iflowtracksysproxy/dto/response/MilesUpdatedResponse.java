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
    boolean netAmountUpdateSuccess;
    boolean otvUpdateSuccess;
    boolean discountUpdateSuccess;
    boolean creditApprovalUpdateSuccess;
    boolean bulkProcessorSuccess;
    boolean chassisNoUpdateSuccess;
    boolean motorNoUpdateSuccess;
    boolean licenceSerialNumberUpdateSuccess;
    boolean expirationDateUpdateSuccess;
    boolean hgsTagNoUpdateSuccess;
    boolean hgsDateUpdateSuccess;
    boolean licensePlataEquipmentUpdateSucceess;
    boolean licensePlataEquipmentTransferUpdateSucceess;
    boolean trafficInsuranceDateUpdateSuccess;
    boolean deliverySupplierUpdateSuccess;
    boolean registrationDateUpdateSuccess;
    boolean shipmentStartDateUpdateSuccess;
    boolean shipmentEndDateUpdateSuccess;

}

package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MilesService {

    private final MilesApi milesApi;

    /**
     * Get current session ID
     * 
     * @return Current session ID
     */
    public String getSessionId() {
        if (MilesApi.sessionId == null) {
            log.warn("Session ID is null, attempting to refresh");
            milesApi.refreshSessionId();
        }
        return MilesApi.sessionId;
    }

    public List<CustomerContractResponse> getCustomerContracts() {
        return milesApi.getCustomerContracts();
    }

    public List<StockVehicleContractResponse> getStockVehicleContracts() {
        return milesApi.getStockVehicleContracts();
    }

    public List<ContractsToBeRegisteredResponse> getContractsRegistered() {
        return milesApi.getContractsRegistered();
    }

    public NetAmountUpdateResponse updateNetAmount(NetAmountUpdateRequest request) {
        return milesApi.updateNetAmount(request);
    }

    public TaxUpdateResponse updateTax(TaxUpdateRequest request) {
        return milesApi.updateTax(request);
    }

    public DiscountUpdateResponse updateDiscount(DiscountUpdateRequest request, String vehicleOrderItem) {
        return milesApi.updateDiscount(request, vehicleOrderItem);
    }

    public ChassisNumberUpdateResponse updateChassisNumber(ChassisNumberUpdateRequest request, String fleetvehicleId) {
        return milesApi.updateChassisNumber(request, fleetvehicleId);
    }

    public PropertyTypeUpdateResponse updatePropertyType(PropertyTypeUpdateRequest request, String fleetvehicleId) {
        return milesApi.updatePropertyType(request, fleetvehicleId);
    }

    public SasiNoUpdateResponse updateSasiNo(SasiNoUpdateRequest request) {
        return milesApi.updateSasiNo(request);
    }

    public PropertyTypeUpdateResponse updateProperty(PropertyTypeUpdateRequest request, String fleetvehicleId) {
        return milesApi.updateProperty(request, fleetvehicleId);
    }

    public MulkUpdateResponse updateMulk(MulkUpdateRequest request) {
        return milesApi.updateMulk(request);
    }

    public BaseResponse updateRuhsatBelgeNo(RuhsatBelgeNoUpdateRequest request) {
        return milesApi.updateRuhsatBelgeNo(request);
    }

    public VehicleInspectionUpdateResponse getVehicleInspection(VehicleInspectionUpdateRequest request) {
        return  milesApi.getVehicleInspection(request);
    }

    public  ApprovalDateUpdateBaseResponse updateCreditApprovalDate(ApprovalDateUpdateRequest approvalDateUpdateRequest){
        return milesApi.updateCreditApprovalDate(approvalDateUpdateRequest);
    }

    /**
     * Get session info
     */
    public SessionInfo getSessionInfo() {
        return SessionInfo.builder()
                .sessionId(MilesApi.sessionId)
                .build();
    }

    @Builder
    @Data
    public static class SessionInfo {
        private String sessionId;
    }

}

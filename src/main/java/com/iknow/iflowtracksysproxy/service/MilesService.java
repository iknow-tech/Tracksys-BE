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

    public BaseResponse getVehicleDocuments(VehicleDocumentsRequest request) {
        return milesApi.getVehicleDocuments(request);
    }

    public BaseResponse updateVehicleInspectionDate(VehicleInspectionDateUpdateRequest request) {
        return milesApi.updateVehicleInspectionDate(request);
    }

    public BaseResponse updateHgsEtiketNo(HgsEtiketNoUpdateRequest request) {
        return milesApi.updateHgsEtiketNo(request);
    }

    public BaseResponse updateHgsTalepTarihi(HgsTalepTarihiUpdateRequest request) {
        return milesApi.updateHgsTalepTarihi(request);
    }

    public BaseResponse updatePlakaAvadanlikTalepTarihi(PlakaAvadanlikTalepTarihiUpdateRequest request) {
        return milesApi.updatePlakaAvadanlikTalepTarihi(request);
    }

    public BaseResponse updatePlakaAvadanlikAlindiTarihi(PlakaAvadanlikAlindiTarihiUpdateRequest request) {
        return milesApi.updatePlakaAvadanlikAlindiTarihi(request);
    }

    public BaseResponse updateTrafikSigortasiTalepTarihi(TrafikSigortasiTalepTarihiUpdateRequest request) {
        return milesApi.updateTrafikSigortasiTalepTarihi(request);
    }

    public BaseResponse updateSevkBitisTarihi(SevkBitisTarihiUpdateRequest request) {
        return milesApi.updateSevkBitisTarihi(request);
    }

    public VehicleInspectionUpdateResponse getVehicleInspection(VehicleInspectionUpdateRequest request) {
        return milesApi.getVehicleInspection(request);
    }

    public TrafficInsuranceGetResponse getTrafficInsurance(TrafficInsuranceGetRequest request) {
        return milesApi.getTrafficInsurance(request);
    }

    public TrafficRegistrationNumberUpdateResponse updateTrafficRegistrationNumber(
            TrafficRegistrationNumberUpdaterequest request) {
        return milesApi.updateTrafficRegistrationNumber(request);
    }

    public DeliveryDealerAreaUpdateResponse updateDeliveryDealerArea(DeliveryDealerAreaUpdateRequest request) {
        return milesApi.updateDeliveryDealerArea(request);
    }

    public  ApprovalDateUpdateBaseResponse updateCreditApprovalDate(ApprovalDateUpdateRequest approvalDateUpdateRequest){
        return milesApi.updateCreditApprovalDate(approvalDateUpdateRequest);
    }

    public List<GetDealerResponse> getDealerResponseList() {
        return milesApi.getDealerList();
    }

    public List<ResponsibleDealerResponse> getResponsibleDealerList() {
        return milesApi.getResponsibleDealerList();
    }

    public TriggerMWSBulkProcessorResponse triggerMWSBulkProcessor(TriggerMWSBulkProcessorRequest request) {
        return milesApi.triggerMWSBulkProcessor(request);
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

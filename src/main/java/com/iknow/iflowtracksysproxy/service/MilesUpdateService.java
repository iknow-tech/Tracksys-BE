package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.MilesUpdatedDto;
import com.iknow.iflowtracksysproxy.dto.response.MilesUpdatedResponse;
import com.iknow.iflowtracksysproxy.dto.response.OrderUpdateResult;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.respository.ContractDealerAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilesUpdateService {

    private final MilesService milesService;
    private final MilesApi milesApi;
    private final ContractDealerAssignmentRepository contractDealerAssignmentRepository;



    public MilesUpdatedResponse update(MilesUpdatedDto milesUpdatedDto) {
        MilesUpdatedResponse milesUpdatedResponse = new MilesUpdatedResponse();
        List<CustomerContractResponse> allContracts = milesService.getCustomerContracts();

            try {
                String contractId = milesUpdatedDto.getContractId();
                CustomerContractResponse contractResponse = allContracts.stream().filter(
                                contract -> contract.getId().equals(contractId)).findFirst()
                        .orElseThrow(() -> new Exception("İlgili Contract Bulunamadı"));

                // net price update
                if (milesUpdatedDto.getNetPrice() != null && !milesUpdatedDto.getNetPrice().equals("")) {
                    NetAmountUpdateRequest netAmountUpdateRequest = new NetAmountUpdateRequest();
                    netAmountUpdateRequest.setCurAmount(milesUpdatedDto.getNetPrice());
                    netAmountUpdateRequest.setRefAmount(milesUpdatedDto.getNetPrice());
                    netAmountUpdateRequest.setSroid("210");
                    netAmountUpdateRequest.setFieldId("1037");
                    netAmountUpdateRequest.setCurrencyId("350001");
                    netAmountUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderItemId());
                    NetAmountUpdateResponse netAmountUpdateResponse = milesService.updateNetAmount(netAmountUpdateRequest);
                    String businessErrorStr = netAmountUpdateResponse.getMetadata().getOperationstatus().getBusinesserror();
                    boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                    milesUpdatedResponse.setNetAmountUpdateSuccess(!hasBusinessError);

                    //indirim alanının güncellenmesi - 0
                    DiscountUpdateRequest discountUpdateRequest = new DiscountUpdateRequest();
                    discountUpdateRequest.setCurAmount("0");
                    discountUpdateRequest.setRefAmount("0");
                    discountUpdateRequest.setOrderId("210");
                    discountUpdateRequest.setFieldId("1040");
                    discountUpdateRequest.setCurrencyId("350001");
                    DiscountUpdateResponse discountUpdateResponse = milesService.updateDiscount(discountUpdateRequest, contractResponse.getVehicleOrderItemId());
                    String businessError = discountUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                    boolean hasBusinessErr = Boolean.parseBoolean(businessError);
                    milesUpdatedResponse.setDiscountUpdateSuccess(!hasBusinessErr);
                }

                // otv(vergi) update
                if (milesUpdatedDto.getOtv() != null && !milesUpdatedDto.getOtv().equals("")) {
                    TaxUpdateRequest taxUpdateRequest = new TaxUpdateRequest();
                    taxUpdateRequest.setCurAmount(milesUpdatedDto.getOtv());
                    taxUpdateRequest.setRefAmount(milesUpdatedDto.getOtv());
                    taxUpdateRequest.setOrderId("210");
                    taxUpdateRequest.setFieldId("1038");
                    taxUpdateRequest.setCurrencyId("350001");
                    taxUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderItemId());
                    TaxUpdateResponse taxUpdateResponse = milesService.updateTax(taxUpdateRequest);
                    String businessErrorStr = taxUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                    boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                    milesUpdatedResponse.setOtvUpdateSuccess(!hasBusinessError);
                }

                if(milesUpdatedDto.getCreditApprovalCheck()){
                    // kredi onay tarihi alanının güncellenemsi
                    ApprovalDateUpdateRequest approvalDateUpdateRequest = new ApprovalDateUpdateRequest();
                    approvalDateUpdateRequest.setApprovalDate(LocalDateTime.now().toString());
                    approvalDateUpdateRequest.setFieldId("1000062");
                    approvalDateUpdateRequest.setOrderId("205");
                    approvalDateUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderId());
                    ApprovalDateUpdateBaseResponse approvalDateUpdateResponse= milesService.updateCreditApprovalDate(approvalDateUpdateRequest);
                    String businessError = approvalDateUpdateResponse.getMetadata().getOperationstatus().getBusinesserror();
                    milesUpdatedResponse.setCreditApprovalUpdateSuccess(!Boolean.parseBoolean(businessError));

                    //     1.6.6 Vehicle Order Statüsünün Onaylandı Olarak Güncellenmesi
                    TriggerMWSBulkProcessorResponse triggerMWSBulkProcessorResponse= milesService.triggerMWSBulkProcessorStatu(contractResponse.getOrdersId());
                    milesUpdatedResponse.setBulkProcessorSuccess(triggerMWSBulkProcessorResponse == null ? false: triggerMWSBulkProcessorResponse.getData().getMwsJobInstance().getState().equals("Bitti"));
                }

                if(milesUpdatedDto.getChassisNumber() != null && !milesUpdatedDto.getChassisNumber().equals("")) {
                    // chassis Number Update
                    SasiNoUpdateRequest sasiNoUpdateRequest = new SasiNoUpdateRequest();
                    sasiNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                    sasiNoUpdateRequest.setFieldId("917");
                    sasiNoUpdateRequest.setSroid("68");
                    sasiNoUpdateRequest.setSasiNo(milesUpdatedDto.getChassisNumber());
                    SasiNoUpdateResponse sasiNoUpdateResponse= milesService.updateSasiNo(sasiNoUpdateRequest);
                    milesUpdatedResponse.setChassisNoUpdateSuccess(!Boolean.parseBoolean(sasiNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
                }

                if(milesUpdatedDto.getMotorNumber() != null && !milesUpdatedDto.getMotorNumber().equals("")) {
                    // motor Number update
                    SasiNoUpdateRequest motorNoUpdateRequest = new SasiNoUpdateRequest();
                    motorNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                    motorNoUpdateRequest.setFieldId("1962");
                    motorNoUpdateRequest.setSroid("68");
                    motorNoUpdateRequest.setSasiNo(milesUpdatedDto.getMotorNumber());
                    SasiNoUpdateResponse motorNoUpdateResponse= milesService.updateSasiNo(motorNoUpdateRequest);
                    milesUpdatedResponse.setMotorNoUpdateSuccess(!Boolean.parseBoolean(motorNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
                }



            } catch (Exception e) {
                log.error("Miles update error for contract {}", milesUpdatedDto.getContractId(), e);

            }

        return milesUpdatedResponse;

    }

    @Transactional
    public boolean vehicleOrderSupplierUpdate(List<CustomerContractResponse> customerContractResponses) throws Exception {
        Boolean isSuccess = false;
        try{
        for (CustomerContractResponse customerContractResponse : customerContractResponses) {
            ContractDealerAssignment contractDealerAssignment = contractDealerAssignmentRepository.findByContractId(customerContractResponse.getId()).isPresent() ? contractDealerAssignmentRepository.findByContractId(customerContractResponse.getId()).get() : null;
            VehicleOrderSupplierUpdateRequest request = new VehicleOrderSupplierUpdateRequest();
            request.setSupplierId(contractDealerAssignment.getDealerBusinessPartnerId());
            request.setContactId(contractDealerAssignment.getContractId());
            request.setOrdersId(customerContractResponse.getOrdersId());
            VehicleOrderSupplierUpdateBaseResponse response= milesApi.vehicleorderSupplierUpdate(request);

           isSuccess = response.getData()
                    .getResponseVehicleOrderSupplierUpdate()
                    .stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException("Supplier update sonucu yok"))
                    .getResult().equals("1");
        }}
        catch (Exception e){
            return false;
        }
        return isSuccess;
    }

}

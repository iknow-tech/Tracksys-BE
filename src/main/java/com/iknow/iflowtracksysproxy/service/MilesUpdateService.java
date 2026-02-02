package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.MilesUpdatedDto;
import com.iknow.iflowtracksysproxy.dto.response.MilesUpdatedResponse;
import com.iknow.iflowtracksysproxy.dto.response.OrderUpdateResult;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
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

    private final ContractDealerAssignmentService contractDealerAssignmentService;
    private final MilesApi milesApi;


    @Transactional
    public boolean vehicleOrderSupplierUpdate(String contractId) throws Exception {

        CustomerContractResponse contractResponse = milesService.getCustomerContracts().stream().filter(contract -> contract.getId().equals(contractId)).findFirst()
                .orElseThrow(() -> new Exception("İlgili Contract Bulunamadı"));
        ContractDealerAssignment contractDealerAssignment = contractDealerAssignmentService.findByContractId(contractId);

        VehicleOrderSupplierUpdateRequest request = new VehicleOrderSupplierUpdateRequest();
        request.setSupplierId(contractDealerAssignment.getDealerBusinessPartnerId());
        request.setContactId(contractDealerAssignment.getContractId());
        request.setOrdersId(contractResponse.getOrdersId());


        //  Miles update
        VehicleOrderSupplierUpdateBaseResponse response= milesApi.vehicleorderSupplierUpdate(request);

        boolean isSuccess = response.getData()
                .getResponseVehicleOrderSupplierUpdate()
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("Supplier update sonucu yok"))
                .getResult().equals("1");


        return isSuccess;

    }


    public MilesUpdatedResponse update(MilesUpdatedDto milesUpdatedDto) {
        MilesUpdatedResponse milesUpdatedResponse = new MilesUpdatedResponse();
        List<CustomerContractResponse> allContracts = milesService.getCustomerContracts();

            try {
                String contractId = milesUpdatedDto.getContractId();
                CustomerContractResponse contractResponse = allContracts.stream().filter(
                                contract -> contract.getId().equals(contractId)).findFirst()
                        .orElseThrow(() -> new Exception("İlgili Contract Bulunamadı"));

                // tedarikçi update
                   vehicleOrderSupplierUpdate(contractId);

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
                }
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

                //indirim alanının güncellenmesi
                DiscountUpdateRequest discountUpdateRequest = new DiscountUpdateRequest();
                discountUpdateRequest.setCurAmount("0");
                discountUpdateRequest.setRefAmount("0");
                discountUpdateRequest.setOrderId("210");
                discountUpdateRequest.setFieldId("1040");
                discountUpdateRequest.setCurrencyId("350001");
                DiscountUpdateResponse discountUpdateResponse = milesService.updateDiscount(discountUpdateRequest, contractResponse.getVehicleOrderItemId());
                String businessErrorStr = discountUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                milesUpdatedResponse.setDiscountUpdateSuccess(!hasBusinessError);

                // kredi onay tarihi alanının güncellenemsi
                ApprovalDateUpdateRequest approvalDateUpdateRequest = new ApprovalDateUpdateRequest();
                approvalDateUpdateRequest.setApprovalDate(LocalDateTime.now().toString());
                approvalDateUpdateRequest.setFieldId("1000062");
                approvalDateUpdateRequest.setOrderId("205");
                approvalDateUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderId());
                milesService.updateCreditApprovalDate(approvalDateUpdateRequest);

                //


            } catch (Exception e) {
                log.error("Miles update error for contract {}", milesUpdatedDto.getContractId(), e);

            }

        return milesUpdatedResponse;

    }

}

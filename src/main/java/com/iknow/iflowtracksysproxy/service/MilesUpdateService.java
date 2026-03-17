package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.MilesUpdatedDto;
import com.iknow.iflowtracksysproxy.dto.response.MilesUpdatedResponse;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.respository.ContractDealerAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilesUpdateService {

    private final MilesService milesService;
    private final MilesApi milesApi;
    private final ContractDealerAssignmentRepository contractDealerAssignmentRepository;

    public MilesUpdatedResponse update(MilesUpdatedDto milesUpdatedDto) {
        MilesUpdatedResponse milesUpdatedResponse = new MilesUpdatedResponse();
        ZoneId zone = ZoneId.of("Europe/Istanbul");

        CustomerContractResponse contractResponse = new CustomerContractResponse();
        if (milesUpdatedDto.getContractId() != null) {
            Map<String, CustomerContractResponse> contractMap =
                    milesService.getCustomerContractsByIds(Set.of(milesUpdatedDto.getContractId()));
            contractResponse = contractMap.getOrDefault(
                    milesUpdatedDto.getContractId(), new CustomerContractResponse());
        }

        VehicleInspectionUpdateRequest vehicleInspectionUpdateRequest = new VehicleInspectionUpdateRequest();
        vehicleInspectionUpdateRequest.setOrdersId(milesUpdatedDto.getFleetVehicleId());
        VehicleInspectionUpdateResponse vehicleInspectionUpdateResponse = null;

        boolean needsVehicleInspection =
                milesUpdatedDto.getLicenseSerialNumber() != null ||
                        milesUpdatedDto.getExpirationDate() != null ||
                        milesUpdatedDto.getHgsCode() != null ||
                        milesUpdatedDto.getHgsRequestedDate() != null ||
                        milesUpdatedDto.getLicensePlateEquipmentRequestDate() != null ||
                        milesUpdatedDto.getLicensePlateEquipmentTransferDate() != null ||
                        milesUpdatedDto.getTrafficInsuranceDate() != null ||
                        milesUpdatedDto.getRegistNoRequestDate() != null;

        if (needsVehicleInspection && milesUpdatedDto.getFleetVehicleId() != null) {
            vehicleInspectionUpdateResponse = milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
        }

        try {
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
                milesUpdatedResponse.setNetAmountUpdateSuccess(!Boolean.parseBoolean(businessErrorStr));

                // indirim alanının güncellenmesi - 0
                DiscountUpdateRequest discountUpdateRequest = new DiscountUpdateRequest();
                discountUpdateRequest.setCurAmount("0");
                discountUpdateRequest.setRefAmount("0");
                discountUpdateRequest.setOrderId("210");
                discountUpdateRequest.setFieldId("1040");
                discountUpdateRequest.setCurrencyId("350001");
                DiscountUpdateResponse discountUpdateResponse = milesService.updateDiscount(
                        discountUpdateRequest, contractResponse.getVehicleOrderItemId());
                String businessError = discountUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                milesUpdatedResponse.setDiscountUpdateSuccess(!Boolean.parseBoolean(businessError));
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
                milesUpdatedResponse.setOtvUpdateSuccess(!Boolean.parseBoolean(businessErrorStr));
            }

            // teslimat bayi alanının güncellenmesi
            if (milesUpdatedDto.getDelivery() != null && !milesUpdatedDto.getDelivery().equals("")) {
                DeliveryDealerAreaUpdateRequest deliveryDealerAreaUpdateRequest = new DeliveryDealerAreaUpdateRequest();
                deliveryDealerAreaUpdateRequest.setContractId(milesUpdatedDto.getContractId());
                deliveryDealerAreaUpdateRequest.setOrderId("86");
                deliveryDealerAreaUpdateRequest.setFieldId("1000272");
                deliveryDealerAreaUpdateRequest.setValue(milesUpdatedDto.getDelivery());
                DeliveryDealerAreaUpdateResponse deliveryDealerAreaUpdateResponse =
                        milesService.updateDeliveryDealerArea(deliveryDealerAreaUpdateRequest);
                String businessErrorStr = deliveryDealerAreaUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                milesUpdatedResponse.setDeliverySupplierUpdateSuccess(!Boolean.parseBoolean(businessErrorStr));
            }

            // sevk başlangıç tarihi
            if (milesUpdatedDto.getShipmentStartDate() != null) {
                SevkBaslangicTarihiUpdateRequest sevkBaslangicTarihiUpdateRequest = new SevkBaslangicTarihiUpdateRequest();
                sevkBaslangicTarihiUpdateRequest.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                sevkBaslangicTarihiUpdateRequest.setSroid("264");
                sevkBaslangicTarihiUpdateRequest.setFieldId("1000013");
                String regShipmentStartDate = milesUpdatedDto.getShipmentStartDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                sevkBaslangicTarihiUpdateRequest.setDateTimeValue(regShipmentStartDate);
                BaseResponse baseResponse = milesService.updateSevkBaslangicTarihi(sevkBaslangicTarihiUpdateRequest);
                String businessErrorStr = baseResponse.getMetadata().getOperationstatus().getBusinesserror();
                milesUpdatedResponse.setShipmentStartDateUpdateSuccess(!Boolean.parseBoolean(businessErrorStr));
            }

            // sevk bitiş tarihi
            if (milesUpdatedDto.getShipmentEndDate() != null) {
                SevkBitisTarihiUpdateRequest sevkBitisTarihiUpdateRequest = new SevkBitisTarihiUpdateRequest();
                sevkBitisTarihiUpdateRequest.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                sevkBitisTarihiUpdateRequest.setSroid("264");
                sevkBitisTarihiUpdateRequest.setFieldId("1000014");
                String regShipmentEndDate = milesUpdatedDto.getShipmentEndDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                sevkBitisTarihiUpdateRequest.setDateTimeValue(regShipmentEndDate);
                BaseResponse baseResponse = milesService.updateSevkBitisTarihi(sevkBitisTarihiUpdateRequest);
                String businessErrorStr = baseResponse.getMetadata().getOperationstatus().getBusinesserror();
                milesUpdatedResponse.setShipmentEndDateUpdateSuccess(!Boolean.parseBoolean(businessErrorStr));
            }

            // kredi onay tarihi
            if (milesUpdatedDto.getCreditApprovalCheck()) {
                ApprovalDateUpdateRequest approvalDateUpdateRequest = new ApprovalDateUpdateRequest();
                approvalDateUpdateRequest.setApprovalDate(LocalDateTime.now().toString());
                approvalDateUpdateRequest.setFieldId("1000062");
                approvalDateUpdateRequest.setOrderId("205");
                approvalDateUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderId());
                ApprovalDateUpdateBaseResponse approvalDateUpdateResponse =
                        milesService.updateCreditApprovalDate(approvalDateUpdateRequest);
                String businessError = approvalDateUpdateResponse.getMetadata().getOperationstatus().getBusinesserror();
                milesUpdatedResponse.setCreditApprovalUpdateSuccess(!Boolean.parseBoolean(businessError));

                TriggerMWSBulkProcessorResponse triggerMWSBulkProcessorResponse =
                        milesService.triggerMWSBulkProcessorStatu(contractResponse.getOrdersId());
                milesUpdatedResponse.setBulkProcessorSuccess(
                        triggerMWSBulkProcessorResponse != null &&
                                triggerMWSBulkProcessorResponse.getData().getMwsJobInstance().getState().equals("Bitti"));
            }

            // chassis number update
            if (milesUpdatedDto.getChassisNumber() != null && !milesUpdatedDto.getChassisNumber().equals("")) {
                SasiNoUpdateRequest sasiNoUpdateRequest = new SasiNoUpdateRequest();
                sasiNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                sasiNoUpdateRequest.setFieldId("917");
                sasiNoUpdateRequest.setSroid("68");
                sasiNoUpdateRequest.setSasiNo(milesUpdatedDto.getChassisNumber());
                SasiNoUpdateResponse sasiNoUpdateResponse = milesService.updateSasiNo(sasiNoUpdateRequest);
                milesUpdatedResponse.setChassisNoUpdateSuccess(!Boolean.parseBoolean(
                        sasiNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
            }

            // motor number update
            if (milesUpdatedDto.getMotorNumber() != null && !milesUpdatedDto.getMotorNumber().equals("")) {
                SasiNoUpdateRequest motorNoUpdateRequest = new SasiNoUpdateRequest();
                motorNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                motorNoUpdateRequest.setFieldId("1962");
                motorNoUpdateRequest.setSroid("68");
                motorNoUpdateRequest.setSasiNo(milesUpdatedDto.getMotorNumber());
                SasiNoUpdateResponse motorNoUpdateResponse = milesService.updateSasiNo(motorNoUpdateRequest);
                milesUpdatedResponse.setMotorNoUpdateSuccess(!Boolean.parseBoolean(
                        motorNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
            }

            VehicleInspectionUpdateResponse.VehicleDocument vehicleDoc = null;
            if (vehicleInspectionUpdateResponse != null &&
                    vehicleInspectionUpdateResponse.getData() != null &&
                    vehicleInspectionUpdateResponse.getData().getVehicleDocumentsSet() != null &&
                    !vehicleInspectionUpdateResponse.getData().getVehicleDocumentsSet().getVehicleDocuments().isEmpty()) {
                vehicleDoc = vehicleInspectionUpdateResponse.getData()
                        .getVehicleDocumentsSet().getVehicleDocuments().get(0);
            }

            if (milesUpdatedDto.getLicenseSerialNumber() != null && !milesUpdatedDto.getLicenseSerialNumber().equals("")) {
                String licenseDocumentNumber = vehicleDoc != null && vehicleDoc.getLicenseDocumentNumber() != null
                        ? vehicleDoc.getLicenseDocumentNumber().getValue() : null;
                RuhsatUpdateRequest req = new RuhsatUpdateRequest();
                req.setVehiclePropertyId(licenseDocumentNumber);
                req.setSroid("262");
                req.setFieldId("1326");
                req.setRuhsatBelgeNo(milesUpdatedDto.getLicenseSerialNumber());
                BaseResponse baseResponse = milesService.updateRuhsatBelgeNo(req);
                milesUpdatedResponse.setLicenceSerialNumberUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getExpirationDate() != null) {
                String vehicleInspection = vehicleDoc != null && vehicleDoc.getVehicleInspection() != null
                        ? vehicleDoc.getVehicleInspection().getValue() : null;
                VehicleInspectionDateUpdateRequest req = new VehicleInspectionDateUpdateRequest();
                req.setVehiclePropertyId(vehicleInspection);
                req.setSroid("262");
                req.setFieldId("2837");
                String regDate = milesUpdatedDto.getExpirationDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTimeValue(regDate);
                BaseResponse baseResponse = milesService.updateVehicleInspectionDate(req);
                milesUpdatedResponse.setExpirationDateUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsCode() != null && !milesUpdatedDto.getHgsCode().equals("")) {
                String hgsTagNo = vehicleDoc != null && vehicleDoc.getHgsTagNo() != null
                        ? vehicleDoc.getHgsTagNo().getValue() : null;
                HgsEtiketNoUpdateRequest req = new HgsEtiketNoUpdateRequest();
                req.setVehiclePropertyId(hgsTagNo);
                req.setSroid("262");
                req.setFieldId("1326");
                req.setHgsEtiketNo(milesUpdatedDto.getHgsCode());
                BaseResponse baseResponse = milesService.updateHgsEtiketNo(req);
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsRequestedDate() != null) {
                String hgsTagNo = vehicleDoc != null && vehicleDoc.getHgsTagNo() != null
                        ? vehicleDoc.getHgsTagNo().getValue() : null;
                HgsTalepTarihiUpdateRequest req = new HgsTalepTarihiUpdateRequest();
                req.setVehiclePropertyId(hgsTagNo);
                req.setSroid("262");
                req.setFieldId("2397");
                String regDate = milesUpdatedDto.getHgsRequestedDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTimeValue(regDate);
                BaseResponse baseResponse = milesService.updateHgsTalepTarihi(req);
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getLicensePlateEquipmentRequestDate() != null) {
                String licensePlateDate = vehicleDoc != null && vehicleDoc.getLicensePlateRequestDate() != null
                        ? vehicleDoc.getLicensePlateRequestDate().getValue() : null;
                PlakaAvadanlikTalepTarihiUpdateRequest req = new PlakaAvadanlikTalepTarihiUpdateRequest();
                req.setVehiclePropertyId(licensePlateDate);
                req.setSroid("262");
                req.setFieldId("2397");
                String regDate = milesUpdatedDto.getLicensePlateEquipmentRequestDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTimeValue(regDate);
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikTalepTarihi(req);
                milesUpdatedResponse.setLicensePlataEquipmentUpdateSucceess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getLicensePlateEquipmentTransferDate() != null) {
                String licensePlateDate = vehicleDoc != null && vehicleDoc.getLicensePlateRequestDate() != null
                        ? vehicleDoc.getLicensePlateRequestDate().getValue() : null;
                PlakaAvadanlikAlindiTarihiUpdateRequest req = new PlakaAvadanlikAlindiTarihiUpdateRequest();
                req.setVehiclePropertyId(licensePlateDate);
                req.setSroid("262");
                req.setFieldId("2396");
                String regDate = milesUpdatedDto.getLicensePlateEquipmentTransferDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTimeValue(regDate);
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikAlindiTarihi(req);
                milesUpdatedResponse.setLicensePlataEquipmentTransferUpdateSucceess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getTrafficInsuranceDate() != null) {
                String trafficInsurance = vehicleDoc != null && vehicleDoc.getTrafficInsurance() != null
                        ? vehicleDoc.getTrafficInsurance().getValue() : null;
                TrafikSigortasiTalepTarihiUpdateRequest req = new TrafikSigortasiTalepTarihiUpdateRequest();
                req.setVehiclePropertyId(trafficInsurance);
                req.setSroid("262");
                req.setFieldId("2397");
                String regDate = milesUpdatedDto.getTrafficInsuranceDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTimeValue(regDate);
                BaseResponse baseResponse = milesService.updateTrafikSigortasiTalepTarihi(req);
                milesUpdatedResponse.setTrafficInsuranceDateUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getRegistNoRequestDate() != null) {
                String trafficInsurance = vehicleDoc != null && vehicleDoc.getTrafficInsurance() != null
                        ? vehicleDoc.getTrafficInsurance().getValue() : null;
                TrafficRegistrationNumberUpdaterequest req = new TrafficRegistrationNumberUpdaterequest();
                req.setFieldId("2397");
                req.setOrderId("262");
                String regDate = milesUpdatedDto.getRegistNoRequestDate()
                        .atTime(LocalTime.now(zone).withNano(0))
                        .atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTime(regDate);
                req.setVehiclePropertyId(trafficInsurance);
                TrafficRegistrationNumberUpdateResponse response =
                        milesService.updateTrafficRegistrationNumber(req);
                milesUpdatedResponse.setRegistrationDateUpdateSuccess(!Boolean.parseBoolean(
                        response.getMetadata().getOperationStatus().getBusinessError()));
            }

        } catch (Exception e) {
            log.error("Miles update error for contract {}: {}", milesUpdatedDto.getContractId(), e.getMessage());
            throw new RuntimeException("Error updating miles vehicle documents", e);
        }

        return milesUpdatedResponse;
    }

    @Transactional
    public boolean vehicleOrderSupplierUpdate(CustomerContractResponse customerContract) throws Exception {
        try {
            ContractDealerAssignment contractDealerAssignment =
                    contractDealerAssignmentRepository.findByContractId(customerContract.getId())
                            .orElse(null);
            if (contractDealerAssignment == null) return false;

            VehicleOrderSupplierUpdateRequest request = new VehicleOrderSupplierUpdateRequest();
            request.setSupplierId(contractDealerAssignment.getDealerBusinessPartnerId());
            request.setContactId(contractDealerAssignment.getContractId());
            request.setOrdersId(customerContract.getOrdersId());
            VehicleOrderSupplierUpdateBaseResponse response = milesApi.vehicleorderSupplierUpdate(request);

            return response.getData()
                    .getResponseVehicleOrderSupplierUpdate()
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Supplier update sonucu yok"))
                    .getResult().equals("1");
        } catch (Exception e) {
            return false;
        }
    }

    public MilesUpdatedResponse addLicensePlateRow(MilesUpdatedDto milesUpdatedDto) {
        MilesUpdatedResponse milesUpdatedResponse = new MilesUpdatedResponse();
        ZoneId zone = ZoneId.of("Europe/Istanbul");

        CustomerContractResponse contractResponse = new CustomerContractResponse();
        if (milesUpdatedDto.getContractId() != null) {
            Map<String, CustomerContractResponse> contractMap =
                    milesService.getCustomerContractsByIds(Set.of(milesUpdatedDto.getContractId()));
            contractResponse = contractMap.getOrDefault(
                    milesUpdatedDto.getContractId(), new CustomerContractResponse());
        }

        VehicleInspectionUpdateRequest vehicleInspectionUpdateRequest = new VehicleInspectionUpdateRequest();
        vehicleInspectionUpdateRequest.setOrdersId(milesUpdatedDto.getFleetVehicleId());
        VehicleInspectionUpdateResponse vehicleInspectionUpdateResponse = null;
        if (milesUpdatedDto.getFleetVehicleId() != null) {
            vehicleInspectionUpdateResponse = milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
        }

        VehicleInspectionUpdateResponse.VehicleDocument vehicleDoc = null;
        if (vehicleInspectionUpdateResponse != null &&
                vehicleInspectionUpdateResponse.getData() != null &&
                vehicleInspectionUpdateResponse.getData().getVehicleDocumentsSet() != null &&
                !vehicleInspectionUpdateResponse.getData().getVehicleDocumentsSet().getVehicleDocuments().isEmpty()) {
            vehicleDoc = vehicleInspectionUpdateResponse.getData()
                    .getVehicleDocumentsSet().getVehicleDocuments().get(0);
        }

        try {
            if (milesUpdatedDto.getNetPrice() != null && !milesUpdatedDto.getNetPrice().equals("")) {
                NetAmountUpdateRequest netAmountUpdateRequest = new NetAmountUpdateRequest();
                netAmountUpdateRequest.setCurAmount(milesUpdatedDto.getNetPrice());
                netAmountUpdateRequest.setRefAmount(milesUpdatedDto.getNetPrice());
                netAmountUpdateRequest.setSroid("210");
                netAmountUpdateRequest.setFieldId("1037");
                netAmountUpdateRequest.setCurrencyId("350001");
                netAmountUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderItemId());
                NetAmountUpdateResponse netAmountUpdateResponse = milesService.updateNetAmount(netAmountUpdateRequest);
                milesUpdatedResponse.setNetAmountUpdateSuccess(!Boolean.parseBoolean(
                        netAmountUpdateResponse.getMetadata().getOperationstatus().getBusinesserror()));

                DiscountUpdateRequest discountUpdateRequest = new DiscountUpdateRequest();
                discountUpdateRequest.setCurAmount("0");
                discountUpdateRequest.setRefAmount("0");
                discountUpdateRequest.setOrderId("210");
                discountUpdateRequest.setFieldId("1040");
                discountUpdateRequest.setCurrencyId("350001");
                DiscountUpdateResponse discountUpdateResponse = milesService.updateDiscount(
                        discountUpdateRequest, contractResponse.getVehicleOrderItemId());
                milesUpdatedResponse.setDiscountUpdateSuccess(!Boolean.parseBoolean(
                        discountUpdateResponse.getMetadata().getOperationStatus().getBusinessError()));
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
                milesUpdatedResponse.setOtvUpdateSuccess(!Boolean.parseBoolean(
                        taxUpdateResponse.getMetadata().getOperationStatus().getBusinessError()));
            }

            if (milesUpdatedDto.getDelivery() != null && !milesUpdatedDto.getDelivery().equals("")) {
                DeliveryDealerAreaUpdateRequest req = new DeliveryDealerAreaUpdateRequest();
                req.setContractId(milesUpdatedDto.getContractId());
                req.setOrderId("210");
                req.setFieldId("1000272");
                req.setValue(milesUpdatedDto.getDelivery());
                DeliveryDealerAreaUpdateResponse response = milesService.updateDeliveryDealerArea(req);
                milesUpdatedResponse.setDeliverySupplierUpdateSuccess(!Boolean.parseBoolean(
                        response.getMetadata().getOperationStatus().getBusinessError()));
            }

            if (milesUpdatedDto.getShipmentStartDate() != null) {
                SevkBaslangicTarihiUpdateRequest req = new SevkBaslangicTarihiUpdateRequest();
                req.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                req.setSroid("264");
                req.setFieldId("1000013");
                req.setDateTimeValue(milesUpdatedDto.getShipmentStartDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateSevkBaslangicTarihi(req);
                milesUpdatedResponse.setDeliverySupplierUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getShipmentEndDate() != null) {
                SevkBitisTarihiUpdateRequest req = new SevkBitisTarihiUpdateRequest();
                req.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                req.setSroid("264");
                req.setFieldId("1000014");
                req.setDateTimeValue(milesUpdatedDto.getShipmentEndDate().atStartOfDay().toString());
                milesService.updateSevkBitisTarihi(req);
            }

            if (milesUpdatedDto.getCreditApprovalCheck()) {
                ApprovalDateUpdateRequest req = new ApprovalDateUpdateRequest();
                req.setApprovalDate(LocalDateTime.now().toString());
                req.setFieldId("1000062");
                req.setOrderId("205");
                req.setVehicleOrderItemId(contractResponse.getVehicleOrderId());
                ApprovalDateUpdateBaseResponse response = milesService.updateCreditApprovalDate(req);
                milesUpdatedResponse.setCreditApprovalUpdateSuccess(!Boolean.parseBoolean(
                        response.getMetadata().getOperationstatus().getBusinesserror()));

                TriggerMWSBulkProcessorResponse triggerResponse =
                        milesService.triggerMWSBulkProcessorStatu(contractResponse.getOrdersId());
                milesUpdatedResponse.setBulkProcessorSuccess(
                        triggerResponse != null &&
                                triggerResponse.getData().getMwsJobInstance().getState().equals("Bitti"));
            }

            if (milesUpdatedDto.getChassisNumber() != null && !milesUpdatedDto.getChassisNumber().equals("")) {
                SasiNoUpdateRequest req = new SasiNoUpdateRequest();
                req.setFleetVehicleId(contractResponse.getFleetVehicleId());
                req.setFieldId("917");
                req.setSroid("68");
                req.setSasiNo(milesUpdatedDto.getChassisNumber());
                SasiNoUpdateResponse response = milesService.updateSasiNo(req);
                milesUpdatedResponse.setChassisNoUpdateSuccess(!Boolean.parseBoolean(
                        response.getResponsemetadata().getOperationStatus().getBusinessError()));
            }

            if (milesUpdatedDto.getMotorNumber() != null && !milesUpdatedDto.getMotorNumber().equals("")) {
                SasiNoUpdateRequest req = new SasiNoUpdateRequest();
                req.setFleetVehicleId(contractResponse.getFleetVehicleId());
                req.setFieldId("1962");
                req.setSroid("68");
                req.setSasiNo(milesUpdatedDto.getMotorNumber());
                SasiNoUpdateResponse response = milesService.updateSasiNo(req);
                milesUpdatedResponse.setMotorNoUpdateSuccess(!Boolean.parseBoolean(
                        response.getResponsemetadata().getOperationStatus().getBusinessError()));
            }

            if (milesUpdatedDto.getLicenseSerialNumber() != null && !milesUpdatedDto.getLicenseSerialNumber().equals("")) {
                String licenseDocumentNumber = vehicleDoc != null && vehicleDoc.getLicenseDocumentNumber() != null
                        ? vehicleDoc.getLicenseDocumentNumber().getValue() : null;
                RuhsatUpdateRequest req = new RuhsatUpdateRequest();
                req.setVehiclePropertyId(licenseDocumentNumber);
                req.setSroid("262");
                req.setFieldId("1326");
                req.setRuhsatBelgeNo(milesUpdatedDto.getLicenseSerialNumber());
                BaseResponse baseResponse = milesService.updateRuhsatBelgeNo(req);
                milesUpdatedResponse.setLicenceSerialNumberUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getExpirationDate() != null) {
                String vehicleInspection = vehicleDoc != null && vehicleDoc.getVehicleInspection() != null
                        ? vehicleDoc.getVehicleInspection().getValue() : null;
                VehicleInspectionDateUpdateRequest req = new VehicleInspectionDateUpdateRequest();
                req.setVehiclePropertyId(vehicleInspection);
                req.setSroid("262");
                req.setFieldId("2837");
                String regDate = milesUpdatedDto.getExpirationDate()
                        .atStartOfDay().atZone(ZoneId.of("Europe/Istanbul"))
                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                req.setDateTimeValue(regDate);
                BaseResponse baseResponse = milesService.updateVehicleInspectionDate(req);
                milesUpdatedResponse.setExpirationDateUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsCode() != null && !milesUpdatedDto.getHgsCode().equals("")) {
                String hgsTagNo = vehicleDoc != null && vehicleDoc.getHgsTagNo() != null
                        ? vehicleDoc.getHgsTagNo().getValue() : null;
                HgsEtiketNoUpdateRequest req = new HgsEtiketNoUpdateRequest();
                req.setVehiclePropertyId(hgsTagNo);
                req.setSroid("262");
                req.setFieldId("1326");
                req.setHgsEtiketNo(milesUpdatedDto.getHgsCode());
                BaseResponse baseResponse = milesService.updateHgsEtiketNo(req);
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsRequestedDate() != null) {
                String hgsTagNo = vehicleDoc != null && vehicleDoc.getHgsTagNo() != null
                        ? vehicleDoc.getHgsTagNo().getValue() : null;
                HgsTalepTarihiUpdateRequest req = new HgsTalepTarihiUpdateRequest();
                req.setVehiclePropertyId(hgsTagNo);
                req.setSroid("262");
                req.setFieldId("2397");
                req.setDateTimeValue(milesUpdatedDto.getHgsRequestedDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateHgsTalepTarihi(req);
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getLicensePlateEquipmentRequestDate() != null) {
                String licensePlateDate = vehicleDoc != null && vehicleDoc.getLicensePlateRequestDate() != null
                        ? vehicleDoc.getLicensePlateRequestDate().getValue() : null;
                PlakaAvadanlikTalepTarihiUpdateRequest req = new PlakaAvadanlikTalepTarihiUpdateRequest();
                req.setVehiclePropertyId(licensePlateDate);
                req.setSroid("262");
                req.setFieldId("2397");
                req.setDateTimeValue(milesUpdatedDto.getLicensePlateEquipmentRequestDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikTalepTarihi(req);
                milesUpdatedResponse.setLicensePlataEquipmentUpdateSucceess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getLicensePlateEquipmentTransferDate() != null) {
                String licensePlateDate = vehicleDoc != null && vehicleDoc.getLicensePlateRequestDate() != null
                        ? vehicleDoc.getLicensePlateRequestDate().getValue() : null;
                PlakaAvadanlikAlindiTarihiUpdateRequest req = new PlakaAvadanlikAlindiTarihiUpdateRequest();
                req.setVehiclePropertyId(licensePlateDate);
                req.setSroid("262");
                req.setFieldId("2396");
                req.setDateTimeValue(milesUpdatedDto.getLicensePlateEquipmentTransferDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikAlindiTarihi(req);
                milesUpdatedResponse.setLicensePlataEquipmentTransferUpdateSucceess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getTrafficInsuranceDate() != null) {
                String trafficInsurance = vehicleDoc != null && vehicleDoc.getTrafficInsurance() != null
                        ? vehicleDoc.getTrafficInsurance().getValue() : null;
                TrafikSigortasiTalepTarihiUpdateRequest req = new TrafikSigortasiTalepTarihiUpdateRequest();
                req.setVehiclePropertyId(trafficInsurance);
                req.setSroid("262");
                req.setFieldId("2397");
                req.setDateTimeValue(milesUpdatedDto.getTrafficInsuranceDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateTrafikSigortasiTalepTarihi(req);
                milesUpdatedResponse.setTrafficInsuranceDateUpdateSuccess(!Boolean.parseBoolean(
                        baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

        } catch (Exception e) {
            log.error("Miles update error for contract {}: {}", milesUpdatedDto.getContractId(), e.getMessage(), e);
        }

        return milesUpdatedResponse;
    }
}

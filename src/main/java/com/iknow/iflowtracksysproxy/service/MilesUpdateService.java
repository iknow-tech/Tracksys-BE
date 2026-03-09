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
import java.util.Optional;

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
        CustomerContractResponse contractResponse = new CustomerContractResponse();
        ZoneId zone = ZoneId.of("Europe/Istanbul");

        try {
            if (milesUpdatedDto.getContractId() != null) {
                Optional<CustomerContractResponse> optionalCustomerContractResponse = allContracts.stream().filter(
                        contract -> contract.getId().equals(milesUpdatedDto.getContractId())).findFirst();
                if (optionalCustomerContractResponse.isPresent()) {
                    contractResponse = optionalCustomerContractResponse.get();
                }
            }
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
            // teslimat bayi alanının güncellenmesi
            if (milesUpdatedDto.getDelivery() != null && !milesUpdatedDto.getDelivery().equals("")) {
                DeliveryDealerAreaUpdateRequest deliveryDealerAreaUpdateRequest = new DeliveryDealerAreaUpdateRequest();
                deliveryDealerAreaUpdateRequest.setContractId(milesUpdatedDto.getContractId());
                deliveryDealerAreaUpdateRequest.setOrderId("86");
                deliveryDealerAreaUpdateRequest.setFieldId("1000272");
                deliveryDealerAreaUpdateRequest.setValue(milesUpdatedDto.getDelivery());
                DeliveryDealerAreaUpdateResponse deliveryDealerAreaUpdateResponse = milesService.updateDeliveryDealerArea(deliveryDealerAreaUpdateRequest);
                String businessErrorStr = deliveryDealerAreaUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                milesUpdatedResponse.setDeliverySupplierUpdateSuccess(!hasBusinessError);
            }

            // sevk başlangıç tarihi alanının güncellenmesi
            if (milesUpdatedDto.getShipmentStartDate()!= null && !milesUpdatedDto.getShipmentStartDate().equals("")) {
                SevkBaslangicTarihiUpdateRequest sevkBaslangicTarihiUpdateRequest = new SevkBaslangicTarihiUpdateRequest();
                sevkBaslangicTarihiUpdateRequest.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                sevkBaslangicTarihiUpdateRequest.setSroid("264");
                sevkBaslangicTarihiUpdateRequest.setFieldId("1000013");
                String regShipmentStartDate =milesUpdatedDto.getShipmentStartDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                sevkBaslangicTarihiUpdateRequest.setDateTimeValue(regShipmentStartDate);
                BaseResponse baseResponse = milesService.updateSevkBaslangicTarihi(sevkBaslangicTarihiUpdateRequest);
                String businessErrorStr = baseResponse.getMetadata().getOperationstatus().getBusinesserror();
                boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                milesUpdatedResponse.setShipmentStartDateUpdateSuccess(!hasBusinessError);
            }
            // sevk bitiş tarihi alanının güncellenmesi
            if (milesUpdatedDto.getShipmentEndDate()!= null && !milesUpdatedDto.getShipmentEndDate().equals("")) {
                SevkBitisTarihiUpdateRequest sevkBitisTarihiUpdateRequest = new SevkBitisTarihiUpdateRequest();
                sevkBitisTarihiUpdateRequest.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                sevkBitisTarihiUpdateRequest.setSroid("264");
                sevkBitisTarihiUpdateRequest.setFieldId("1000014");
                String regShipmentEndDate =milesUpdatedDto.getShipmentEndDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                sevkBitisTarihiUpdateRequest.setDateTimeValue(regShipmentEndDate);
                BaseResponse baseResponse = milesService.updateSevkBitisTarihi(sevkBitisTarihiUpdateRequest);
                String businessErrorStr = baseResponse.getMetadata().getOperationstatus().getBusinesserror();
                boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                milesUpdatedResponse.setShipmentEndDateUpdateSuccess(!hasBusinessError);
            }

            if (milesUpdatedDto.getCreditApprovalCheck()) {
                // kredi onay tarihi alanının güncellenemsi
                ApprovalDateUpdateRequest approvalDateUpdateRequest = new ApprovalDateUpdateRequest();
                approvalDateUpdateRequest.setApprovalDate(LocalDateTime.now().toString());
                approvalDateUpdateRequest.setFieldId("1000062");
                approvalDateUpdateRequest.setOrderId("205");
                approvalDateUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderId());
                ApprovalDateUpdateBaseResponse approvalDateUpdateResponse = milesService.updateCreditApprovalDate(approvalDateUpdateRequest);
                String businessError = approvalDateUpdateResponse.getMetadata().getOperationstatus().getBusinesserror();
                milesUpdatedResponse.setCreditApprovalUpdateSuccess(!Boolean.parseBoolean(businessError));

                //     1.6.6 Vehicle Order Statüsünün Onaylandı Olarak Güncellenmesi
                TriggerMWSBulkProcessorResponse triggerMWSBulkProcessorResponse = milesService.triggerMWSBulkProcessorStatu(contractResponse.getOrdersId());
                milesUpdatedResponse.setBulkProcessorSuccess(triggerMWSBulkProcessorResponse == null ? false : triggerMWSBulkProcessorResponse.getData().getMwsJobInstance().getState().equals("Bitti"));
            }

            if (milesUpdatedDto.getChassisNumber() != null && !milesUpdatedDto.getChassisNumber().equals("")) {
                // chassis Number Update
                SasiNoUpdateRequest sasiNoUpdateRequest = new SasiNoUpdateRequest();
                sasiNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                sasiNoUpdateRequest.setFieldId("917");
                sasiNoUpdateRequest.setSroid("68");
                sasiNoUpdateRequest.setSasiNo(milesUpdatedDto.getChassisNumber());
                SasiNoUpdateResponse sasiNoUpdateResponse = milesService.updateSasiNo(sasiNoUpdateRequest);
                milesUpdatedResponse.setChassisNoUpdateSuccess(!Boolean.parseBoolean(sasiNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
            }

            if (milesUpdatedDto.getMotorNumber() != null && !milesUpdatedDto.getMotorNumber().equals("")) {
                // motor Number update
                SasiNoUpdateRequest motorNoUpdateRequest = new SasiNoUpdateRequest();
                motorNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                motorNoUpdateRequest.setFieldId("1962");
                motorNoUpdateRequest.setSroid("68");
                motorNoUpdateRequest.setSasiNo(milesUpdatedDto.getMotorNumber());
                SasiNoUpdateResponse motorNoUpdateResponse = milesService.updateSasiNo(motorNoUpdateRequest);
                milesUpdatedResponse.setMotorNoUpdateSuccess(!Boolean.parseBoolean(motorNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
            }
            VehicleInspectionUpdateResponse vehicleInspectionUpdateResponse = new VehicleInspectionUpdateResponse();
            VehicleInspectionUpdateRequest vehicleInspectionUpdateRequest = new VehicleInspectionUpdateRequest();
            vehicleInspectionUpdateRequest.setOrdersId(milesUpdatedDto.getFleetVehicleId());

            if (milesUpdatedDto.getLicenseSerialNumber() != null && !milesUpdatedDto.getLicenseSerialNumber().equals("")) {
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                String licenseDocumentNumber = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getLicenseDocumentNumber() != null) {
                        licenseDocumentNumber =
                                document.getLicenseDocumentNumber().getValue();
                    }
                }
                RuhsatUpdateRequest ruhsatBelgeNoUpdateRequest = new RuhsatUpdateRequest();
                ruhsatBelgeNoUpdateRequest.setVehiclePropertyId(licenseDocumentNumber);
                ruhsatBelgeNoUpdateRequest.setSroid("262");
                ruhsatBelgeNoUpdateRequest.setFieldId("1326");
                ruhsatBelgeNoUpdateRequest.setRuhsatBelgeNo(milesUpdatedDto.getLicenseSerialNumber());
                BaseResponse baseResponse = milesService.updateRuhsatBelgeNo(ruhsatBelgeNoUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setLicenceSerialNumberUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getExpirationDate() != null && !milesUpdatedDto.getExpirationDate().equals("")) {
                String vehicleInspection = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getVehicleInspection() != null) {
                        vehicleInspection = document.getVehicleInspection().getValue();
                    }
                }
                VehicleInspectionDateUpdateRequest vehicleInspectionDateUpdateRequest = new VehicleInspectionDateUpdateRequest();
                vehicleInspectionDateUpdateRequest.setVehiclePropertyId(vehicleInspection);
                vehicleInspectionDateUpdateRequest.setSroid("262");
                vehicleInspectionDateUpdateRequest.setFieldId("2837");
                String regExpirationdate = milesUpdatedDto.getExpirationDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                vehicleInspectionDateUpdateRequest.setDateTimeValue(regExpirationdate);
                BaseResponse baseResponse = milesService.updateVehicleInspectionDate(vehicleInspectionDateUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setExpirationDateUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsCode() != null && !milesUpdatedDto.getHgsCode().equals("")) {
                String hgsTagNo = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getHgsTagNo() != null) {
                        hgsTagNo = document.getHgsTagNo().getValue();
                    }
                }
                HgsEtiketNoUpdateRequest hgsEtiketNoUpdateRequest = new HgsEtiketNoUpdateRequest();
                hgsEtiketNoUpdateRequest.setVehiclePropertyId(hgsTagNo);
                hgsEtiketNoUpdateRequest.setSroid("262");
                hgsEtiketNoUpdateRequest.setFieldId("1326");
                hgsEtiketNoUpdateRequest.setHgsEtiketNo(milesUpdatedDto.getHgsCode());
                BaseResponse baseResponse = milesService.updateHgsEtiketNo(hgsEtiketNoUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsRequestedDate() != null && !milesUpdatedDto.getHgsRequestedDate().equals("")) {
                String hgsTagNo = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getHgsTagNo() != null) {
                        hgsTagNo = document.getHgsTagNo().getValue();
                    }
                }

                HgsTalepTarihiUpdateRequest hgsTalepTarihiUpdateRequest = new HgsTalepTarihiUpdateRequest();
                hgsTalepTarihiUpdateRequest.setVehiclePropertyId(hgsTagNo);
                hgsTalepTarihiUpdateRequest.setSroid("262");
                hgsTalepTarihiUpdateRequest.setFieldId("2397");
                String regHgsRequestedDate = milesUpdatedDto.getHgsRequestedDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                hgsTalepTarihiUpdateRequest.setDateTimeValue(regHgsRequestedDate);
                BaseResponse baseResponse = milesService.updateHgsTalepTarihi(hgsTalepTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }
            if (milesUpdatedDto.getLicensePlateEquipmentRequestDate() != null && !milesUpdatedDto.getLicensePlateEquipmentRequestDate().equals("")) {
                String LicensePlateEquipmentRequestDate = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getLicensePlateRequestDate() != null) {
                        LicensePlateEquipmentRequestDate = document.getLicensePlateRequestDate().getValue();
                    }
                }
                PlakaAvadanlikTalepTarihiUpdateRequest plakaAvadanlikTalepTarihiUpdateRequest = new PlakaAvadanlikTalepTarihiUpdateRequest();
                plakaAvadanlikTalepTarihiUpdateRequest.setVehiclePropertyId(LicensePlateEquipmentRequestDate);
                plakaAvadanlikTalepTarihiUpdateRequest.setVehiclePropertyId(LicensePlateEquipmentRequestDate);
                plakaAvadanlikTalepTarihiUpdateRequest.setSroid("262");
                plakaAvadanlikTalepTarihiUpdateRequest.setFieldId("2397");
                String regLicensePlateEquipmentDate = milesUpdatedDto.getLicensePlateEquipmentRequestDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                plakaAvadanlikTalepTarihiUpdateRequest.setDateTimeValue(regLicensePlateEquipmentDate);
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikTalepTarihi(plakaAvadanlikTalepTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setLicensePlataEquipmentUpdateSucceess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getLicensePlateEquipmentTransferDate() != null && !milesUpdatedDto.getLicensePlateEquipmentTransferDate().equals("")) {
                String LicensePlateEquipmentRequestDate = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getLicensePlateRequestDate() != null) {
                        LicensePlateEquipmentRequestDate = document.getLicensePlateRequestDate().getValue();
                    }
                }
                PlakaAvadanlikAlindiTarihiUpdateRequest plakaAvadanlikAlindiTarihiUpdateRequest = new PlakaAvadanlikAlindiTarihiUpdateRequest();
                plakaAvadanlikAlindiTarihiUpdateRequest.setVehiclePropertyId(LicensePlateEquipmentRequestDate);
                plakaAvadanlikAlindiTarihiUpdateRequest.setSroid("262");
                plakaAvadanlikAlindiTarihiUpdateRequest.setFieldId("2396");
                String regLicensePlateEquipmentTransferDate = milesUpdatedDto.getLicensePlateEquipmentTransferDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                plakaAvadanlikAlindiTarihiUpdateRequest.setDateTimeValue(regLicensePlateEquipmentTransferDate);
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikAlindiTarihi(plakaAvadanlikAlindiTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setLicensePlataEquipmentTransferUpdateSucceess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getTrafficInsuranceDate() != null && !milesUpdatedDto.getTrafficInsuranceDate().equals("")) {
                String trafficInsurance = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getTrafficInsurance() != null) {
                        trafficInsurance = document.getTrafficInsurance().getValue();
                    }
                }
                TrafikSigortasiTalepTarihiUpdateRequest trafikSigortasiTalepTarihiUpdateRequest = new TrafikSigortasiTalepTarihiUpdateRequest();
                trafikSigortasiTalepTarihiUpdateRequest.setVehiclePropertyId(trafficInsurance);
                trafikSigortasiTalepTarihiUpdateRequest.setSroid("262");
                trafikSigortasiTalepTarihiUpdateRequest.setFieldId("2397");
                String regTrafficInsuranceDate = milesUpdatedDto.getTrafficInsuranceDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                trafikSigortasiTalepTarihiUpdateRequest.setDateTimeValue(regTrafficInsuranceDate);
                BaseResponse baseResponse = milesService.updateTrafikSigortasiTalepTarihi(trafikSigortasiTalepTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setTrafficInsuranceDateUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if(milesUpdatedDto.getRegistNoRequestDate() != null && !milesUpdatedDto.getRegistNoRequestDate().equals("")) {
                String trafficInsurance = null;
                vehicleInspectionUpdateResponse= milesService.getVehicleInspection(vehicleInspectionUpdateRequest);
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getTrafficInsurance() != null) {
                        trafficInsurance = document.getTrafficInsurance().getValue();
                    }
                }

                TrafficRegistrationNumberUpdaterequest trafficRegistrationNumberUpdaterequest = new TrafficRegistrationNumberUpdaterequest();
                trafficRegistrationNumberUpdaterequest.setFieldId("2397");
                trafficRegistrationNumberUpdaterequest.setOrderId("262");
                String regTrafficInsuranceDate = milesUpdatedDto.getRegistNoRequestDate().atTime(LocalTime.now(zone).withNano(0)).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                trafficRegistrationNumberUpdaterequest.setDateTime(regTrafficInsuranceDate);
                trafficRegistrationNumberUpdaterequest.setVehiclePropertyId(trafficInsurance);
                TrafficRegistrationNumberUpdateResponse trafficRegistrationNumberUpdateResponse =milesService.updateTrafficRegistrationNumber(trafficRegistrationNumberUpdaterequest);
                milesUpdatedResponse.setRegistrationDateUpdateSuccess(!Boolean.parseBoolean(trafficRegistrationNumberUpdateResponse.getMetadata().getOperationStatus().getBusinessError()));


            }
        } catch (Exception e) {
            log.error("Miles update error for contract {}", milesUpdatedDto.getContractId(), e.getMessage());
            throw new RuntimeException("Error updating miles vehicle documents", e);

        }

        return milesUpdatedResponse;

    }

    @Transactional
    public boolean vehicleOrderSupplierUpdate(CustomerContractResponse customerContract) throws Exception {
        Boolean isSuccess = false;
        try {
            ContractDealerAssignment contractDealerAssignment = contractDealerAssignmentRepository.findByContractId(customerContract.getId()).isPresent() ? contractDealerAssignmentRepository.findByContractId(customerContract.getId()).get() : null;
            VehicleOrderSupplierUpdateRequest request = new VehicleOrderSupplierUpdateRequest();
            request.setSupplierId(contractDealerAssignment.getDealerBusinessPartnerId());
            request.setContactId(contractDealerAssignment.getContractId());
            request.setOrdersId(customerContract.getOrdersId());
            VehicleOrderSupplierUpdateBaseResponse response = milesApi.vehicleorderSupplierUpdate(request);

            isSuccess = response.getData()
                    .getResponseVehicleOrderSupplierUpdate()
                    .stream()
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalStateException("Supplier update sonucu yok"))
                    .getResult().equals("1");
        } catch (Exception e) {
            return false;
        }
        return isSuccess;
    }

    public MilesUpdatedResponse addLicensePlateRow (MilesUpdatedDto milesUpdatedDto) {
        MilesUpdatedResponse milesUpdatedResponse = new MilesUpdatedResponse();
        List<CustomerContractResponse> allContracts = milesService.getCustomerContracts();
        CustomerContractResponse contractResponse = new CustomerContractResponse();
        try {
            if (milesUpdatedDto.getContractId() != null) {
                Optional<CustomerContractResponse> optionalCustomerContractResponse = allContracts.stream().filter(
                        contract -> contract.getId().equals(milesUpdatedDto.getContractId())).findFirst();
                if (optionalCustomerContractResponse.isPresent()) {
                    contractResponse = optionalCustomerContractResponse.get();
                }
            }
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
            // teslimat bayi alanının güncellenmesi
            if (milesUpdatedDto.getDelivery() != null && !milesUpdatedDto.getDelivery().equals("")) {
                DeliveryDealerAreaUpdateRequest deliveryDealerAreaUpdateRequest = new DeliveryDealerAreaUpdateRequest();
                deliveryDealerAreaUpdateRequest.setContractId(milesUpdatedDto.getContractId());
                deliveryDealerAreaUpdateRequest.setOrderId("210");
                deliveryDealerAreaUpdateRequest.setFieldId("1000272");
                deliveryDealerAreaUpdateRequest.setValue(milesUpdatedDto.getDelivery());
                DeliveryDealerAreaUpdateResponse deliveryDealerAreaUpdateResponse = milesService.updateDeliveryDealerArea(deliveryDealerAreaUpdateRequest);
                String businessErrorStr = deliveryDealerAreaUpdateResponse.getMetadata().getOperationStatus().getBusinessError();
                boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                milesUpdatedResponse.setDeliverySupplierUpdateSuccess(!hasBusinessError);
            }

            // sevk başlangıç tarihi alanının güncellenmesi
            if (milesUpdatedDto.getShipmentStartDate()!= null && !milesUpdatedDto.getShipmentStartDate().equals("")) {
                SevkBaslangicTarihiUpdateRequest sevkBaslangicTarihiUpdateRequest = new SevkBaslangicTarihiUpdateRequest();
                sevkBaslangicTarihiUpdateRequest.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                sevkBaslangicTarihiUpdateRequest.setSroid("264");
                sevkBaslangicTarihiUpdateRequest.setFieldId("1000013");
                sevkBaslangicTarihiUpdateRequest.setDateTimeValue(milesUpdatedDto.getShipmentStartDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateSevkBaslangicTarihi(sevkBaslangicTarihiUpdateRequest);
                String businessErrorStr = baseResponse.getMetadata().getOperationstatus().getBusinesserror();
                boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                milesUpdatedResponse.setDeliverySupplierUpdateSuccess(!hasBusinessError);
            }
            // sevk bitiş tarihi alanının güncellenmesi
            if (milesUpdatedDto.getShipmentEndDate()!= null && !milesUpdatedDto.getShipmentEndDate().equals("")) {
                SevkBitisTarihiUpdateRequest sevkBitisTarihiUpdateRequest = new SevkBitisTarihiUpdateRequest();
                sevkBitisTarihiUpdateRequest.setDeliveryConditionId(milesUpdatedDto.getDeliveryConditionId());
                sevkBitisTarihiUpdateRequest.setSroid("264");
                sevkBitisTarihiUpdateRequest.setFieldId("1000014");
                sevkBitisTarihiUpdateRequest.setDateTimeValue(milesUpdatedDto.getShipmentStartDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateSevkBitisTarihi(sevkBitisTarihiUpdateRequest);
            }

            if (milesUpdatedDto.getCreditApprovalCheck()) {
                // kredi onay tarihi alanının güncellenemsi
                ApprovalDateUpdateRequest approvalDateUpdateRequest = new ApprovalDateUpdateRequest();
                approvalDateUpdateRequest.setApprovalDate(LocalDateTime.now().toString());
                approvalDateUpdateRequest.setFieldId("1000062");
                approvalDateUpdateRequest.setOrderId("205");
                approvalDateUpdateRequest.setVehicleOrderItemId(contractResponse.getVehicleOrderId());
                ApprovalDateUpdateBaseResponse approvalDateUpdateResponse = milesService.updateCreditApprovalDate(approvalDateUpdateRequest);
                String businessError = approvalDateUpdateResponse.getMetadata().getOperationstatus().getBusinesserror();
                milesUpdatedResponse.setCreditApprovalUpdateSuccess(!Boolean.parseBoolean(businessError));

                //     1.6.6 Vehicle Order Statüsünün Onaylandı Olarak Güncellenmesi
                TriggerMWSBulkProcessorResponse triggerMWSBulkProcessorResponse = milesService.triggerMWSBulkProcessorStatu(contractResponse.getOrdersId());
                milesUpdatedResponse.setBulkProcessorSuccess(triggerMWSBulkProcessorResponse == null ? false : triggerMWSBulkProcessorResponse.getData().getMwsJobInstance().getState().equals("Bitti"));
            }

            if (milesUpdatedDto.getChassisNumber() != null && !milesUpdatedDto.getChassisNumber().equals("")) {
                // chassis Number Update
                SasiNoUpdateRequest sasiNoUpdateRequest = new SasiNoUpdateRequest();
                sasiNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                sasiNoUpdateRequest.setFieldId("917");
                sasiNoUpdateRequest.setSroid("68");
                sasiNoUpdateRequest.setSasiNo(milesUpdatedDto.getChassisNumber());
                SasiNoUpdateResponse sasiNoUpdateResponse = milesService.updateSasiNo(sasiNoUpdateRequest);
                milesUpdatedResponse.setChassisNoUpdateSuccess(!Boolean.parseBoolean(sasiNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
            }

            if (milesUpdatedDto.getMotorNumber() != null && !milesUpdatedDto.getMotorNumber().equals("")) {
                // motor Number update
                SasiNoUpdateRequest motorNoUpdateRequest = new SasiNoUpdateRequest();
                motorNoUpdateRequest.setFleetVehicleId(contractResponse.getFleetVehicleId());
                motorNoUpdateRequest.setFieldId("1962");
                motorNoUpdateRequest.setSroid("68");
                motorNoUpdateRequest.setSasiNo(milesUpdatedDto.getMotorNumber());
                SasiNoUpdateResponse motorNoUpdateResponse = milesService.updateSasiNo(motorNoUpdateRequest);
                milesUpdatedResponse.setMotorNoUpdateSuccess(!Boolean.parseBoolean(motorNoUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError()));
            }
            VehicleInspectionUpdateRequest vehicleInspectionUpdateRequest = new VehicleInspectionUpdateRequest();
            vehicleInspectionUpdateRequest.setOrdersId(milesUpdatedDto.getFleetVehicleId());
            VehicleInspectionUpdateResponse vehicleInspectionUpdateResponse = milesService.getVehicleInspection(vehicleInspectionUpdateRequest);

            if (milesUpdatedDto.getLicenseSerialNumber() != null && !milesUpdatedDto.getLicenseSerialNumber().equals("")) {
                String licenseDocumentNumber = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getLicenseDocumentNumber() != null) {
                        licenseDocumentNumber =
                                document.getLicenseDocumentNumber().getValue();
                    }
                }
                RuhsatUpdateRequest ruhsatBelgeNoUpdateRequest = new RuhsatUpdateRequest();
                ruhsatBelgeNoUpdateRequest.setVehiclePropertyId(licenseDocumentNumber);
                ruhsatBelgeNoUpdateRequest.setSroid("262");
                ruhsatBelgeNoUpdateRequest.setFieldId("1326");
                ruhsatBelgeNoUpdateRequest.setRuhsatBelgeNo(milesUpdatedDto.getLicenseSerialNumber());
                BaseResponse baseResponse = milesService.updateRuhsatBelgeNo(ruhsatBelgeNoUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setLicenceSerialNumberUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getExpirationDate() != null && !milesUpdatedDto.getExpirationDate().equals("")) {
                String vehicleInspection = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getVehicleInspection() != null) {
                        vehicleInspection = document.getVehicleInspection().getValue();
                    }
                }
                VehicleInspectionDateUpdateRequest vehicleInspectionDateUpdateRequest = new VehicleInspectionDateUpdateRequest();
                vehicleInspectionDateUpdateRequest.setVehiclePropertyId(vehicleInspection);
                vehicleInspectionDateUpdateRequest.setSroid("262");
                vehicleInspectionDateUpdateRequest.setFieldId("2837");
                String regExpirationDate = milesUpdatedDto.getExpirationDate().atStartOfDay().atZone(ZoneId.of("Europe/Istanbul")).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                vehicleInspectionDateUpdateRequest.setDateTimeValue(regExpirationDate);
                BaseResponse baseResponse = milesService.updateVehicleInspectionDate(vehicleInspectionDateUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setExpirationDateUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsCode()!= null && !milesUpdatedDto.getHgsCode().equals("")) {
                String hgsTagNo = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getHgsTagNo() != null) {
                        hgsTagNo = document.getHgsTagNo().getValue();
                    }
                }
                HgsEtiketNoUpdateRequest hgsEtiketNoUpdateRequest = new HgsEtiketNoUpdateRequest();
                hgsEtiketNoUpdateRequest.setVehiclePropertyId(hgsTagNo);
                hgsEtiketNoUpdateRequest.setSroid("262");
                hgsEtiketNoUpdateRequest.setFieldId("1326");
                hgsEtiketNoUpdateRequest.setHgsEtiketNo(milesUpdatedDto.getHgsCode());
                BaseResponse baseResponse = milesService.updateHgsEtiketNo(hgsEtiketNoUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getHgsRequestedDate() != null && !milesUpdatedDto.getHgsRequestedDate().equals("")) {
                String hgsTagNo = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getHgsTagNo() != null) {
                        hgsTagNo = document.getHgsTagNo().getValue();
                    }
                }
                HgsTalepTarihiUpdateRequest hgsTalepTarihiUpdateRequest = new HgsTalepTarihiUpdateRequest();
                hgsTalepTarihiUpdateRequest.setVehiclePropertyId(hgsTagNo);
                hgsTalepTarihiUpdateRequest.setSroid("262");
                hgsTalepTarihiUpdateRequest.setFieldId("2397");
                hgsTalepTarihiUpdateRequest.setDateTimeValue(milesUpdatedDto.getHgsRequestedDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateHgsTalepTarihi(hgsTalepTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setHgsTagNoUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }
            if (milesUpdatedDto.getLicensePlateEquipmentRequestDate() != null && !milesUpdatedDto.getLicensePlateEquipmentRequestDate().equals("")) {
                String LicensePlateEquipmentRequestDate = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getLicensePlateRequestDate() != null) {
                        LicensePlateEquipmentRequestDate = document.getLicensePlateRequestDate().getValue();
                    }
                }
                PlakaAvadanlikTalepTarihiUpdateRequest plakaAvadanlikTalepTarihiUpdateRequest = new PlakaAvadanlikTalepTarihiUpdateRequest();
                plakaAvadanlikTalepTarihiUpdateRequest.setVehiclePropertyId(LicensePlateEquipmentRequestDate);
                plakaAvadanlikTalepTarihiUpdateRequest.setSroid("262");
                plakaAvadanlikTalepTarihiUpdateRequest.setFieldId("2397");
                plakaAvadanlikTalepTarihiUpdateRequest.setDateTimeValue(milesUpdatedDto.getLicensePlateEquipmentRequestDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikTalepTarihi(plakaAvadanlikTalepTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setLicensePlataEquipmentUpdateSucceess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getLicensePlateEquipmentTransferDate() != null && !milesUpdatedDto.getLicensePlateEquipmentTransferDate().equals("")) {
                String LicensePlateEquipmentRequestDate = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getLicensePlateRequestDate() != null) {
                        LicensePlateEquipmentRequestDate = document.getLicensePlateRequestDate().getValue();
                    }
                }
                PlakaAvadanlikAlindiTarihiUpdateRequest plakaAvadanlikAlindiTarihiUpdateRequest = new PlakaAvadanlikAlindiTarihiUpdateRequest();
                plakaAvadanlikAlindiTarihiUpdateRequest.setVehiclePropertyId(LicensePlateEquipmentRequestDate);
                plakaAvadanlikAlindiTarihiUpdateRequest.setSroid("262");
                plakaAvadanlikAlindiTarihiUpdateRequest.setFieldId("2396");
                plakaAvadanlikAlindiTarihiUpdateRequest.setDateTimeValue(milesUpdatedDto.getLicensePlateEquipmentTransferDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updatePlakaAvadanlikAlindiTarihi(plakaAvadanlikAlindiTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setLicensePlataEquipmentTransferUpdateSucceess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }

            if (milesUpdatedDto.getTrafficInsuranceDate() != null && !milesUpdatedDto.getTrafficInsuranceDate().equals("")) {
                String trafficInsurance = null;
                if (vehicleInspectionUpdateResponse != null && vehicleInspectionUpdateResponse.getData() != null) {
                    VehicleInspectionUpdateResponse.VehicleDocument document =
                            vehicleInspectionUpdateResponse.getData()
                                    .getVehicleDocumentsSet()
                                    .getVehicleDocuments()
                                    .get(0);

                    if (document.getTrafficInsurance() != null) {
                        trafficInsurance = document.getTrafficInsurance().getValue();
                    }
                }
                TrafikSigortasiTalepTarihiUpdateRequest trafikSigortasiTalepTarihiUpdateRequest = new TrafikSigortasiTalepTarihiUpdateRequest();
                trafikSigortasiTalepTarihiUpdateRequest.setVehiclePropertyId(trafficInsurance);
                trafikSigortasiTalepTarihiUpdateRequest.setSroid("262");
                trafikSigortasiTalepTarihiUpdateRequest.setFieldId("2397");
                trafikSigortasiTalepTarihiUpdateRequest.setDateTimeValue(milesUpdatedDto.getTrafficInsuranceDate().atStartOfDay().toString());
                BaseResponse baseResponse = milesService.updateTrafikSigortasiTalepTarihi(trafikSigortasiTalepTarihiUpdateRequest);
                DataResponse.MWSBulkAttributeUpdate mwsBulkAttributeUpdate = baseResponse.getData().getMwsBulkAttributeUpdate();
                milesUpdatedResponse.setTrafficInsuranceDateUpdateSuccess(!Boolean.parseBoolean(baseResponse.getMetadata().getOperationstatus().getBusinesserror()));
            }


        } catch (Exception e) {
            log.error("Miles update error for contract {}", milesUpdatedDto.getContractId(), e);

        }

        return milesUpdatedResponse;

    }




}

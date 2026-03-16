package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.dto.request.DealerInvoiceMailRequest;
import com.iknow.iflowtracksysproxy.entity.*;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.respository.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MilesService {

    private final MilesApi milesApi;
    private final ContractDealerAssignmentRepository contractDealerAssignmentRepository;
    private final ContractLeasingAssignmentRepository contractLeasingAssignmentRepository;
    private final CustomerContractCache customerContractCache;
    private final MilesContractSyncService milesContractSyncService;
    private final ContractProformaRepository contractProformaRepository;
    private final DeliveryDocumentRepository deliveryDocumentRepository;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final MailService mailService;

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
        log.info("Session ID: {}", MilesApi.sessionId);
        return MilesApi.sessionId;
    }


    public List<CustomerContractResponse> getCustomerContracts() {

        List<CustomerContractResponse> contracts = milesApi.getCustomerContracts();
        if (contracts == null || contracts.isEmpty()) {
            return contracts;
        }

        List<String> contractIds = contracts.stream()
                .map(CustomerContractResponse::getId)
                .collect(Collectors.toList());

        List<ContractDealerAssignment> dealerAssignments = contractDealerAssignmentRepository.findAll();
        List<ContractLeasingAssignment> leasingAssignments = contractLeasingAssignmentRepository.findByStatus("ACTIVE");

        List<DeliveryDocument> deliveryDocuments = deliveryDocumentRepository.findByContractIdIn(contractIds);
        Map<String, DeliveryDocument> deliveryDocMap = deliveryDocuments.stream()
                .collect(Collectors.toMap(DeliveryDocument::getContractId, d -> d));

        Set<String> proformaContractIds = new HashSet<>(
                contractProformaRepository.findContractIdsByContractIdIn(contractIds)
        );

        Map<String, ContractDealerAssignment> dealerMap = dealerAssignments.stream()
                .collect(Collectors.toMap(ContractDealerAssignment::getContractId, a -> a));

        Map<String, ContractLeasingAssignment> leasingMap = leasingAssignments.stream()
                .collect(Collectors.toMap(ContractLeasingAssignment::getContractId, a -> a));

        for (CustomerContractResponse contractResponse : contracts) {
            DeliveryDocument doc = deliveryDocMap.get(contractResponse.getId());
            if (doc != null) {
                contractResponse.setDeliveryDocumentId(doc.getId().toString());
                contractResponse.setDeliveryDocumentName(doc.getFileName());
            }

            ContractLeasingAssignment leasing = leasingMap.get(contractResponse.getId());
            if (leasing != null) {
                contractResponse.setAssignedLeasing(leasing.getLeasingName());
                contractResponse.setSysEnumerationId(leasing.getLeasingEnumId());
            } else {
                contractResponse.setAssignedLeasing(null);
                contractResponse.setSysEnumerationId(null);
            }

            ContractDealerAssignment dealer = dealerMap.get(contractResponse.getId());
            if (dealer != null && dealer.getStatus().equals("ACTIVE")) {
                contractResponse.setAssignedDealer(dealer.getDealerName());
                contractResponse.setDeliveryMethod(dealer.getDeliveryMethod());
                if (dealer.getLeasingInvoiceDate() != null) { // leasing fatura tarihi
                    contractResponse.setLeasingInvoiceDate(dealer.getLeasingInvoiceDate());
                    // miles update(Leasing ile Satın Alınan Araçlarda Vehicle Order Açıklamasının Finansal Kiralama Olarak Güncellenmesi)
                    VehicleOrderDescUpdateRequest vehicleOrderDescUpdateRequest = new VehicleOrderDescUpdateRequest();
                    vehicleOrderDescUpdateRequest.setVehicleOrderId(contractResponse.getOrdersId());
                    vehicleOrderDescUpdateRequest.setSroid("266");
                    vehicleOrderDescUpdateRequest.setValue("Leasing- " + leasing.getLeasingName() + " " + dealer.getLeasingInvoiceDate() + " Fatura");
                    vehicleOrderDescUpdateRequest.setFieldId("1371");
                    VehicleOrderDescUpdateResponse vehicleOrderDescUpdateResponse = updateVehicleOrderDesc(vehicleOrderDescUpdateRequest);
                    String businessErrorStr = vehicleOrderDescUpdateResponse.getMetadata().getOperationstatus().getBusinesserror();
                    boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
                    contractResponse.setUpdateVehicleOrderDesc(!hasBusinessError);
                }
                if (dealer.getStatus().equals(ContractStatus.DELIVERED.toString())) {
                    contractResponse.setStatus(ContractStatus.DELIVERED);
                    contractResponse.setDeliveredBy(null);
                    contractResponse.setOrderDeliveredDate(LocalDateTime.now());
                }
                contractResponse.setContractOrderStatus(dealer.getContractOrderStatus());
            }
            contractResponse.setHasProforma(proformaContractIds.contains(contractResponse.getId()));
        }

        return contracts;
    }

    public List<StockVehicleContractResponse> getStockVehicleContracts() {
        return milesApi.getStockVehicleContracts();
    }

    public List<ContractsToBeRegisteredResponse> getContractsRegistered() {
        List<ContractsToBeRegisteredResponse> contractsToBeRegisteredResponseList = milesApi.getContractsRegistered();
        List<ContractDealerAssignment> dealerAssignments = contractDealerAssignmentRepository.findAll();

        Map<String, ContractDealerAssignment> dealerMap =
                dealerAssignments.stream()
                        .collect(Collectors.toMap(
                                ContractDealerAssignment::getContractId,
                                a -> a
                        ));
        for (ContractsToBeRegisteredResponse registeredResponse : contractsToBeRegisteredResponseList) {
            ContractDealerAssignment dealer = dealerMap.get(registeredResponse.getContractId());
            if (dealer != null) {
                registeredResponse.setAssignedDealer(dealer.getDealerName());
            }
            Optional<VehicleDocumentAssignment> optionalVehicleDocumentAssignment = vehicleDocumentRepository.findByContractIdAndStatus(registeredResponse.getContractId(), "ACTIVE");
            if (optionalVehicleDocumentAssignment.isPresent()) {
                VehicleDocumentAssignment vehicleDocumentAssignment = optionalVehicleDocumentAssignment.get();
                registeredResponse.setLicenseSerialNumber(vehicleDocumentAssignment.getLicenseSerialNumber());
                registeredResponse.setExpirationDate(vehicleDocumentAssignment.getExpirationDate());
                registeredResponse.setHgsCode(vehicleDocumentAssignment.getHgsCode());
                registeredResponse.setHgsDate(vehicleDocumentAssignment.getExpirationDate());
                registeredResponse.setLicensePlateEquipmentRequestDate(vehicleDocumentAssignment.getLicensePlateEquipmentRequestDate());
                registeredResponse.setLicensePlateEquipmentTransferDate(vehicleDocumentAssignment.getLicensePlateEquipmentTransferDate());
                registeredResponse.setTrafficInsuranceDate(vehicleDocumentAssignment.getTrafficInsuranceDate());
                registeredResponse.setHgsRequestedDate(vehicleDocumentAssignment.getHgsRequestedDate());
                registeredResponse.setRegistNoRequestDate(vehicleDocumentAssignment.getRegistNoRequestDate());
                registeredResponse.setLicensePlate(vehicleDocumentAssignment.getLicensePlate());

            }

        }
        return contractsToBeRegisteredResponseList;
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

    public BaseResponse updateRuhsatBelgeNo(RuhsatUpdateRequest request) {
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

    public BaseResponse updateSevkBaslangicTarihi(SevkBaslangicTarihiUpdateRequest request) {
        return milesApi.updateSevkBaslangicTarihi(request);
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

    //kredi onay tarihinin güncellenmesi
    public ApprovalDateUpdateBaseResponse updateCreditApprovalDate(ApprovalDateUpdateRequest approvalDateUpdateRequest) {
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

    public List<GetLeasingResponse> getLeasingResponseList() {
        return milesApi.getLeasingsList();
    }

    public TriggerMWSBulkProcessor_ApproveContractResponse triggerMWSBulkProcessor(TriggerMWSBulkProcessor_ApproveContractRequest request) {
        LocalDateTime deliveryDate = OffsetDateTime.parse(request.getDeliveryDate())
                .toLocalDateTime();
        String formatted = deliveryDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        request.setDeliveryDate(formatted);
        TriggerMWSBulkProcessor_ApproveContractResponse response = milesApi.approveContract(request);
        String businessErrorStr = response.getMetadata().getOperationStatus().getBusinessError();
        ContractDealerAssignment contractDealerAssignment = contractDealerAssignmentRepository.findByContractId(request.getContractId()).isPresent() ? contractDealerAssignmentRepository.findByContractId(request.getContractId()).get() : null;
        boolean hasBusinessError = Boolean.parseBoolean(businessErrorStr);
        if (!hasBusinessError) {
            contractDealerAssignment.setContractOrderStatus(ContractOrderStatus.ORDER_DELIVERED);
            contractDealerAssignment.setStatus(ContractStatus.DELIVERED.toString());
        }
        return response;
    }

    public PRJ_SM_OwnerShipResponse getOwnerShip() {
        return milesApi.getOwnerShip();
    }

    public SaveLicenseCertificateResponse saveLicenseCertificate(SaveLicenseCertificateRequest request) {
        return milesApi.saveLicenseCertificate(request);
    }

    public TracksysUsersResponse getTracksysUsers() {
        return milesApi.getTracksysUsers();
    }

    public VehicleOrderDescUpdateResponse updateVehicleOrderDesc(VehicleOrderDescUpdateRequest request) {
        return milesApi.updateVehicleOrderDescription(request);
    }

    // Vehicle Order Statüsünün Onaylandı Olarak Güncellenmesi
    public TriggerMWSBulkProcessorResponse triggerMWSBulkProcessorStatu(String ordersId) {
        TriggerMWSBulkProcessorResponse triggerMWSBulkProcessorResponse = new TriggerMWSBulkProcessorResponse();
        if (ordersId != null) {
            triggerMWSBulkProcessorResponse = milesApi.triggerMWSBulkProcessorStatu(ordersId);
        }
        return triggerMWSBulkProcessorResponse;
    }

    public void saveMWSFleetVehicle(String registrationDate, String licensePlate, String fleetVehicleId) {
        milesApi.SaveMWSFleetVehicle(registrationDate, licensePlate, fleetVehicleId);
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

    public SaveLicenseCertificateResponse add(SaveLicenseCertificateRequest request) {
        return milesApi.saveLicenseCertificate(request);
    }

}

package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.entity.*;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.respository.*;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MilesService {

    private final MilesApi milesApi;
    private final ContractDealerAssignmentRepository contractDealerAssignmentRepository;
    private final CustomerContractCache customerContractCache;
    private final MilesContractSyncService milesContractSyncService;
    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final CustomerContractEnrichmentService customerContractEnrichmentService;
    @Value("${miles.customer-contracts.cache-seconds:120}")
    private long customerContractsCacheSeconds;
    @Value("${miles.customer-contracts-response-cache-seconds:3600}")
    private long customerContractsResponseCacheSeconds;
    private final Object customerContractsRefreshLock = new Object();
    private final AtomicBoolean customerContractsRequestInFlight = new AtomicBoolean(false);
    private final AtomicBoolean customerContractsRefreshInFlight = new AtomicBoolean(false);

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
        logHeapUsage("getCustomerContracts:start", null);
        if (!customerContractsRequestInFlight.compareAndSet(false, true)) {
            log.info("getCustomerContracts request already in progress, returning current cache snapshot");
            List<CustomerContractResponse> currentSnapshot = enrichCurrentCustomerContracts();
            logHeapUsage("getCustomerContracts:inflight-cache-return", currentSnapshot.size());
            return currentSnapshot;
        }

        try {
            Duration responseCacheTtl = Duration.ofSeconds(customerContractsResponseCacheSeconds);
            Duration cacheTtl = Duration.ofSeconds(customerContractsCacheSeconds);

            List<CustomerContractResponse> currentSnapshot = getCurrentCustomerContractsSnapshot();
            if (!currentSnapshot.isEmpty() && customerContractCache.isFresh(responseCacheTtl)) {
                if (!customerContractCache.isFresh(cacheTtl)) {
                    triggerAsyncCustomerContractsRefresh(cacheTtl);
                }
                List<CustomerContractResponse> enrichedContracts =
                        customerContractEnrichmentService.enrich(currentSnapshot);
                log.info("getCustomerContracts returning current cache snapshot");
                logHeapUsage("getCustomerContracts:fresh-cache-return", enrichedContracts.size());
                return enrichedContracts;
            }

            if (currentSnapshot.isEmpty()) {
                refreshCustomerContracts(cacheTtl, "API_CALL_EMPTY");
            } else if (!customerContractCache.isFresh(cacheTtl)) {
                triggerAsyncCustomerContractsRefresh(cacheTtl);
            }

            List<CustomerContractResponse> enrichedContracts = enrichCurrentCustomerContracts();
            logHeapUsage("getCustomerContracts:before-return", enrichedContracts.size());
            return enrichedContracts;
        } finally {
            customerContractsRequestInFlight.set(false);
        }
    }

    public Optional<CustomerContractResponse> findCustomerContractById(String contractId) {
        if (contractId == null || contractId.isBlank()) {
            return Optional.empty();
        }

        Optional<CustomerContractResponse> cachedContract = findInCurrentCache(contractId);
        if (cachedContract.isPresent()) {
            return cachedContract;
        }

        forceRefreshCustomerContracts("CONTRACT_LOOKUP_MISS");
        return findInCurrentCache(contractId);
    }

    private List<CustomerContractResponse> getCurrentCustomerContractsSnapshot() {
        List<CustomerContractResponse> snapshot = customerContractCache.snapshot();
        if (snapshot == null || snapshot.isEmpty()) {
            return List.of();
        }
        return snapshot;
    }

    private List<CustomerContractResponse> enrichCurrentCustomerContracts() {
        List<CustomerContractResponse> currentSnapshot = getCurrentCustomerContractsSnapshot();
        if (currentSnapshot.isEmpty()) {
            return currentSnapshot;
        }
        return customerContractEnrichmentService.enrich(currentSnapshot);
    }

    private Optional<CustomerContractResponse> findInCurrentCache(String contractId) {
        return customerContractCache.get().stream()
                .filter(contract -> contractId.equals(contract.getId()))
                .findFirst()
                .map(customerContractEnrichmentService::copyOf);
    }

    private void refreshCustomerContracts(Duration cacheTtl, String trigger) {
        synchronized (customerContractsRefreshLock) {
            if (customerContractCache.isFresh(cacheTtl)) {
                return;
            }
            log.info("Customer contract cache stale/empty, Miles'tan cache refresh ediliyor (trigger={})", trigger);
            logHeapUsage("getCustomerContracts:before-cache-refresh", null);
            milesContractSyncService.refreshCacheFromMiles(trigger);
            logHeapUsage("getCustomerContracts:after-cache-refresh", null);
        }
    }

    private void forceRefreshCustomerContracts(String trigger) {
        synchronized (customerContractsRefreshLock) {
            log.info("Customer contract cache force refresh started (trigger={})", trigger);
            logHeapUsage("getCustomerContracts:before-cache-refresh", null);
            milesContractSyncService.refreshCacheFromMiles(trigger);
            logHeapUsage("getCustomerContracts:after-cache-refresh", null);
        }
    }

    private void triggerAsyncCustomerContractsRefresh(Duration cacheTtl) {
        if (!customerContractsRefreshInFlight.compareAndSet(false, true)) {
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                refreshCustomerContracts(cacheTtl, "API_CALL_ASYNC");
            } catch (Exception e) {
                log.error("Async customer contract refresh failed", e);
            } finally {
                customerContractsRefreshInFlight.set(false);
            }
        });
    }


    public List<StockVehicleContractResponse> getStockVehicleContracts() {
        logHeapUsage("getStockVehicleContracts:start", null);
        List<StockVehicleContractResponse> responses = milesApi.getStockVehicleContracts();
        logHeapUsage("getStockVehicleContracts:after-miles",
                responses == null ? null : responses.size());
        return responses;
    }

    public List<ContractsToBeRegisteredResponse> getContractsRegistered() {
        logHeapUsage("getContractsRegistered:start", null);
        List<ContractsToBeRegisteredResponse> contractsToBeRegisteredResponseList = milesApi.getContractsRegistered();
        logHeapUsage("getContractsRegistered:after-miles", contractsToBeRegisteredResponseList.size());
        List<String> contractIds = contractsToBeRegisteredResponseList.stream()
                .map(ContractsToBeRegisteredResponse::getContractId)
                .filter(java.util.Objects::nonNull)
                .toList();

        if (contractIds.isEmpty()) {
            return contractsToBeRegisteredResponseList;
        }

        logHeapUsage("getContractsRegistered:before-batch-fetch", contractIds.size());
        List<ContractDealerAssignment> dealerAssignments =
                contractDealerAssignmentRepository.findByStatusAndContractIdIn("ACTIVE", contractIds);
        List<VehicleDocumentAssignment> vehicleDocumentAssignments =
                vehicleDocumentRepository.findByStatusAndContractIdIn("ACTIVE", contractIds);
        logHeapUsage("getContractsRegistered:after-batch-fetch", contractIds.size());

        Map<String, ContractDealerAssignment> dealerMap =
                dealerAssignments.stream()
                        .collect(Collectors.toMap(
                                ContractDealerAssignment::getContractId,
                                a -> a
                        ));
        Map<String, VehicleDocumentAssignment> vehicleDocumentMap =
                vehicleDocumentAssignments.stream()
                        .collect(Collectors.toMap(
                                VehicleDocumentAssignment::getContractId,
                                document -> document,
                                (first, second) -> first
                        ));
        logHeapUsage("getContractsRegistered:after-map-build", contractIds.size());
        for (ContractsToBeRegisteredResponse registeredResponse : contractsToBeRegisteredResponseList) {
            ContractDealerAssignment dealer = dealerMap.get(registeredResponse.getContractId());
            if (dealer != null) {
                registeredResponse.setAssignedDealer(dealer.getDealerName());
            }
            VehicleDocumentAssignment vehicleDocumentAssignment = vehicleDocumentMap.get(registeredResponse.getContractId());
            if (vehicleDocumentAssignment != null) {
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
        logHeapUsage("getContractsRegistered:before-return", contractsToBeRegisteredResponseList.size());
        return contractsToBeRegisteredResponseList;
    }

    private void logHeapUsage(String phase, Integer itemCount) {
        Runtime runtime = Runtime.getRuntime();
        long usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long totalMb = runtime.totalMemory() / (1024 * 1024);
        long maxMb = runtime.maxMemory() / (1024 * 1024);

        if (itemCount == null) {
            log.info("HEAP phase={} usedMb={} totalMb={} maxMb={}", phase, usedMb, totalMb, maxMb);
            return;
        }

        log.info("HEAP phase={} items={} usedMb={} totalMb={} maxMb={}", phase, itemCount, usedMb, totalMb, maxMb);
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
        logHeapUsage("getDealerResponseList:start", null);
        List<GetDealerResponse> responses = milesApi.getDealerList();
        logHeapUsage("getDealerResponseList:after-miles",
                responses == null ? null : responses.size());
        return responses;
    }

    public List<ResponsibleDealerResponse> getResponsibleDealerList() {
        return milesApi.getResponsibleDealerList();
    }

    public TriggerMWSBulkProcessorResponse triggerMWSBulkProcessor(TriggerMWSBulkProcessorRequest request) {
        return milesApi.triggerMWSBulkProcessor(request);
    }

    public List<GetLeasingResponse> getLeasingResponseList() {
        logHeapUsage("getLeasingResponseList:start", null);
        List<GetLeasingResponse> responses = milesApi.getLeasingsList();
        logHeapUsage("getLeasingResponseList:after-miles",
                responses == null ? null : responses.size());
        return responses;
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

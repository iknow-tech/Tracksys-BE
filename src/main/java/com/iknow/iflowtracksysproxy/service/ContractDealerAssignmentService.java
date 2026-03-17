package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.dto.DealerContractInfo;
import com.iknow.iflowtracksysproxy.dto.MilesUpdatedDto;
import com.iknow.iflowtracksysproxy.dto.request.AssignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.request.DealerContractUpdateItemRequest;
import com.iknow.iflowtracksysproxy.dto.request.DealerContractUpdateRequest;
import com.iknow.iflowtracksysproxy.dto.request.UnassignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.response.AssignDealerResponse;
import com.iknow.iflowtracksysproxy.dto.response.MilesUpdatedResponse;
import com.iknow.iflowtracksysproxy.entity.*;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.VehicleOrderSupplierUpdateRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.VehicleOrderSupplierUpdateBaseResponse;
import com.iknow.iflowtracksysproxy.respository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractDealerAssignmentService {

    private final ContractDealerAssignmentRepository assignmentRepository;
    private final CustomerContractCache customerContractCache;
    private final MilesContractSyncService milesContractSyncService;
    private final ContractProformaRepository contractProformaRepository;
    private final ContractLeasingAssignmentRepository contractLeasingAssignmentRepository;
    private final DeliveryDocumentRepository deliveryDocumentRepository;
    private final MilesService milesService;
    private final ProformaReviewService proformaReviewService;
    private final MilesUpdateService milesUpdateService;
    private final MilesApi milesApi;
    private final DealerReportRepository dealerReportRepository;

    @Transactional
    @CacheEvict(value = "customerContracts", allEntries = true)
    public AssignDealerResponse assignDealerToContracts(AssignDealerRequest request) throws Exception {

        List<CustomerContractResponse> contracts = request.getContracts();
        List<String> contractIds = contracts.stream()
                .map(CustomerContractResponse::getId)
                .collect(Collectors.toList());

        log.info("Dealer: {} (ID: {}), contract count: {}",
                request.getDealerName(), request.getDealerId(), contracts.size());

        Map<String, ContractDealerAssignment> existingAssignments =
                assignmentRepository.findByContractIdIn(contractIds).stream()
                        .collect(Collectors.toMap(ContractDealerAssignment::getContractId, a -> a));

        Map<String, DealerReport> existingReports =
                dealerReportRepository.findByContractIdIn(contractIds).stream()
                        .collect(Collectors.toMap(DealerReport::getContractId, r -> r));

        int assignedCount = 0;
        int failedCount = 0;
        List<String> failedContractIds = new ArrayList<>();
        List<AssignDealerResponse.AssignedContractInfo> assignedContracts = new ArrayList<>();
        List<Boolean> updatedMilesSupplier = new ArrayList<>();
        List<ContractDealerAssignment> assignmentsToSave = new ArrayList<>();
        List<DealerReport> reportsToSave = new ArrayList<>();

        for (CustomerContractResponse contract : contracts) {
            try {
                if (contract.getOrdersId() == null) {
                    throw new IllegalArgumentException(
                            "OrdersId bulunamadı. Bu kontrat için önce Miles sipariş kaydı oluşmalıdır."
                    );
                }

                ContractDealerAssignment assignment = existingAssignments
                        .getOrDefault(contract.getId(), new ContractDealerAssignment());

                assignment.setContractId(contract.getId());
                assignment.setDealerBusinessPartnerId(request.getDealerId());
                assignment.setDealerContactId(request.getDealerContactId());
                assignment.setDealerName(request.getDealerName());
                assignment.setAssignedBy(request.getAssignedBy());
                assignment.setAssignedDate(LocalDateTime.now());
                assignment.setStatus("ACTIVE");
                assignment.setNotes("Web UI üzerinden atama.");
                assignment.setDealerEmail("");
                assignmentsToSave.add(assignment);

                VehicleOrderSupplierUpdateRequest vehicleOrderSupplierUpdateRequest =
                        new VehicleOrderSupplierUpdateRequest();
                vehicleOrderSupplierUpdateRequest.setOrdersId(contract.getOrdersId());
                vehicleOrderSupplierUpdateRequest.setSupplierId(request.getDealerId());
                vehicleOrderSupplierUpdateRequest.setContactId(request.getDealerContactId());
                VehicleOrderSupplierUpdateBaseResponse response =
                        milesApi.vehicleorderSupplierUpdate(vehicleOrderSupplierUpdateRequest);

                Boolean isMilesUpdateSuccess = response.getData()
                        .getResponseVehicleOrderSupplierUpdate()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Supplier update sonucu yok"))
                        .getResult().equals("1");
                updatedMilesSupplier.add(isMilesUpdateSuccess);

                DealerReport dealerReport = existingReports
                        .getOrDefault(contract.getId(), new DealerReport());
                dealerReport.setContractId(contract.getId());
                dealerReport.setCustomer(contract.getCustomer());
                dealerReport.setVehicleDescription(
                        contract.getMake() + " " + contract.getModel() + " " + contract.getVersion());
                dealerReport.setColor(contract.getColor());
                dealerReport.setDealer(request.getDealerName());
                dealerReport.setDeliveryDealer(contract.getDeliveryPerson());
                dealerReport.setShipmentCityContract(contract.getDeliveryLocation());
                dealerReport.setStatus("ACTIVE");
                reportsToSave.add(dealerReport);

                assignedContracts.add(AssignDealerResponse.AssignedContractInfo.builder()
                        .contractId(contract.getId())
                        .build());

                assignedCount++;
                log.info("Contract {} processed successfully", contract.getId());

            } catch (Exception e) {
                log.error("Failed to assign contract {}: {}", contract.getId(), e.getMessage(), e);
                failedCount++;
                failedContractIds.add(contract.getId());
            }
        }

        assignmentRepository.saveAll(assignmentsToSave);
        dealerReportRepository.saveAll(reportsToSave);

        return AssignDealerResponse.builder()
                .success(failedCount == 0)
                .assignedCount(assignedCount)
                .isMilesUpdateSuccess(updatedMilesSupplier)
                .failedCount(failedCount)
                .failedContractIds(failedContractIds)
                .assignedContracts(assignedContracts)
                .build();
    }

    public List<DealerContractInfo> getDealerContractsWithDetails(String dealerId) {
        log.info("Fetching detailed contracts for dealer: {}", dealerId);

        List<ContractDealerAssignment> assignments =
                assignmentRepository.findLatestAssignmentsPerContract(dealerId, "ACTIVE");

        if (assignments.isEmpty()) {
            log.info("No active assignments found for dealer: {}", dealerId);
            return new ArrayList<>();
        }

        Set<String> contractIds = assignments.stream()
                .map(ContractDealerAssignment::getContractId)
                .collect(Collectors.toSet());

        Map<String, CustomerContractResponse> contractMap =
                milesService.getCustomerContractsByIds(contractIds);

        if (contractMap.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> contractIdList = new ArrayList<>(contractIds);

        Map<String, Boolean> proformaMap = contractProformaRepository
                .findContractIdsByContractIdIn(contractIdList)
                .stream()
                .collect(Collectors.toSet())
                .stream()
                .collect(Collectors.toMap(id -> id, id -> true));

        Map<String, ContractLeasingAssignment> leasingMap =
                contractLeasingAssignmentRepository
                        .findByStatusAndContractIdIn("ACTIVE", contractIdList)
                        .stream()
                        .collect(Collectors.toMap(ContractLeasingAssignment::getContractId, a -> a));

        Map<String, DeliveryDocument> deliveryDocMap =
                deliveryDocumentRepository.findByContractIdIn(contractIdList)
                        .stream()
                        .collect(Collectors.toMap(DeliveryDocument::getContractId, d -> d));

        List<DealerContractInfo> detailedContracts = new ArrayList<>();

        for (ContractDealerAssignment assignment : assignments) {
            try {
                String contractId = assignment.getContractId();
                CustomerContractResponse contract = contractMap.get(contractId);

                if (contract == null) {
                    log.warn("⚠️ Contract {} not found in MilesApi response", contractId);
                    continue;
                }

                boolean hasProforma = proformaMap.getOrDefault(contractId, false);

                ContractLeasingAssignment leasingAssignment =
                        leasingMap.getOrDefault(contractId, new ContractLeasingAssignment());

                DeliveryDocument deliveryDocument = deliveryDocMap.get(contractId);
                String deliveryDocumentId = null;
                String deliveryDocumentName = null;
                if (deliveryDocument != null) {
                    deliveryDocumentId = deliveryDocument.getId() != null ?
                            deliveryDocument.getId().toString() : null;
                    deliveryDocumentName = deliveryDocument.getFileName();
                }

                DealerContractInfo contractInfo = DealerContractInfo.builder()
                        .id(contract.getId())
                        .contractId(contract.getId())
                        .contractApprovedDate(contract.getContractapproveddate())
                        .customer(contract.getCustomer())
                        .make(contract.getMake())
                        .model(contract.getModel())
                        .modelYear(contract.getModelYear())
                        .leasingName(leasingAssignment.getLeasingName())
                        .sysEnumerationId(leasingAssignment.getLeasingEnumId())
                        .version(contract.getVersion())
                        .color(contract.getColor())
                        .deliveryPerson(contract.getDeliveryPerson())
                        .deliveryLocation(contract.getDeliveryLocation())
                        .ordersId(contract.getOrdersId())
                        .ettn(assignment.getEttn())
                        .deliverySupplier(assignment.getDelivery())
                        .shipmentStartDate(assignment.getShipmentBeginDate())
                        .shipmentEndDate(assignment.getShipmentEndDate())
                        .deliveryDate(contract.getDeliveryDate())
                        .netPrice(assignment.getNetPrice())
                        .otv(assignment.getOtv())
                        .chassisNumber(assignment.getChassisNumber())
                        .motorNumber(assignment.getMotorNumber())
                        .options(contract.getOptions())
                        .uttsGpsInstallation(contract.getUttsGpsInstallation())
                        .treasuryApprovalDate(contract.getTreasuryApprovalDate())
                        .deliveryTerms(contract.getDeliveryTerms())
                        .leasingInvoiceDate(assignment.getLeasingInvoiceDate())
                        .deliveryMethod(assignment.getDeliveryMethod())
                        .vehicleOrderItemId(contract.getVehicleOrderItemId())
                        .fleetVehicleId(contract.getFleetVehicleId())
                        .dealerName(assignment.getDealerName())
                        .assignedDate(assignment.getAssignedDate())
                        .assignedBy(assignment.getAssignedBy())
                        .status(assignment.getStatus())
                        .hasProforma(hasProforma)
                        .deliveryDocumentId(deliveryDocumentId)
                        .deliveryDocumentName(deliveryDocumentName)
                        .contractOrderStatus(assignment.getContractOrderStatus())
                        .dealerBusinessPartnerId(assignment.getDealerBusinessPartnerId())
                        .build();

                detailedContracts.add(contractInfo);
                log.debug("Contract {} processed successfully", contractId);

            } catch (Exception e) {
                log.error("Error processing assignment for contract {}: {}",
                        assignment.getContractId(), e.getMessage(), e);
            }
        }

        log.info("Returning {} detailed contracts for dealer {}", detailedContracts.size(), dealerId);
        return detailedContracts;
    }

    @Transactional
    @CacheEvict(value = "customerContracts", allEntries = true)
    public void unassignDealer(UnassignDealerRequest request) {
        ContractDealerAssignment assignment =
                assignmentRepository
                        .findFirstByContractIdAndStatus(request.getContractId(), "ACTIVE")
                        .orElseThrow(() -> new RuntimeException("Aktif bayi ataması bulunamadı"));

        assignment.setStatus("CANCELLED");
        assignment.setCancelledBy(request.getCancelledBy());
        assignment.setCancelledDate(LocalDateTime.now());
        assignmentRepository.save(assignment);

        log.info("Contract {} dealer assignment cancelled", request.getContractId());
    }

    @Transactional
    @CacheEvict(value = "customerContracts", allEntries = true)
    public void updateDealerContracts(DealerContractUpdateRequest request) {
        if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
            return;
        }

        List<String> contractIds = request.getUpdates().stream()
                .map(DealerContractUpdateItemRequest::getContractId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Map<String, CustomerContractResponse> contractMap =
                milesService.getCustomerContractsByIds(new HashSet<>(contractIds));

        Map<String, ContractDealerAssignment> assignmentMap =
                assignmentRepository.findByContractIdIn(contractIds).stream()
                        .filter(a -> "ACTIVE".equals(a.getStatus()))
                        .collect(Collectors.toMap(ContractDealerAssignment::getContractId, a -> a));

        Map<String, DealerReport> reportMap =
                dealerReportRepository.findByContractIdIn(contractIds).stream()
                        .collect(Collectors.toMap(DealerReport::getContractId, r -> r));

        List<ContractDealerAssignment> assignmentsToSave = new ArrayList<>();
        List<DealerReport> reportsToSave = new ArrayList<>();

        try {
            for (DealerContractUpdateItemRequest item : request.getUpdates()) {
                if (item.getContractId() == null) continue;

                CustomerContractResponse contractResponse = contractMap.get(item.getContractId());
                ContractDealerAssignment assignment = assignmentMap.get(item.getContractId());
                if (assignment == null) {
                    throw new IllegalArgumentException("Contract not found: " + item.getContractId());
                }

                MilesUpdatedDto milesUpdatedDto = new MilesUpdatedDto();
                milesUpdatedDto.setContractId(item.getContractId());

                if (item.getNetPrice() != null) {
                    assignment.setNetPrice(item.getNetPrice());
                    milesUpdatedDto.setNetPrice(item.getNetPrice().toString());
                }
                if (item.getOtv() != null) {
                    assignment.setOtv(item.getOtv());
                    milesUpdatedDto.setOtv(item.getOtv().toString());
                }
                if (item.getMotorNumber() != null && !item.getMotorNumber().isBlank()) {
                    assignment.setMotorNumber(item.getMotorNumber());
                    milesUpdatedDto.setMotorNumber(item.getMotorNumber());
                }
                if (item.getChassisNumber() != null && !item.getChassisNumber().isBlank()) {
                    assignment.setChassisNumber(item.getChassisNumber());
                    milesUpdatedDto.setChassisNumber(item.getChassisNumber());
                }
                if (item.getDeliverySupplier() != null && !item.getDeliverySupplier().isBlank()) {
                    assignment.setDelivery(item.getDeliverySupplier());
                    milesUpdatedDto.setDelivery(item.getDeliverySupplier());
                }
                if (item.getShipmentStartDate() != null) {
                    assignment.setShipmentBeginDate(item.getShipmentStartDate());
                    milesUpdatedDto.setShipmentStartDate(LocalDate.parse(item.getShipmentStartDate()));
                    if (contractResponse != null)
                        milesUpdatedDto.setDeliveryConditionId(contractResponse.getDeliveryConditionId());
                }
                if (item.getShipmentEndDate() != null) {
                    assignment.setShipmentEndDate(item.getShipmentEndDate());
                    milesUpdatedDto.setShipmentEndDate(LocalDate.parse(item.getShipmentEndDate()));
                    if (contractResponse != null)
                        milesUpdatedDto.setDeliveryConditionId(contractResponse.getDeliveryConditionId());
                }
                if (item.getEttn() != null && !item.getEttn().isBlank()) {
                    assignment.setEttn(item.getEttn());
                }
                if (item.getDeliveryDate() != null) {
                    assignment.setDeliveryDate(item.getDeliveryDate());
                }
                if (item.getLeasingInvoiceDate() != null) {
                    assignment.setLeasingInvoiceDate(item.getLeasingInvoiceDate());
                }
                if (item.getDeliveryMethod() != null && !item.getDeliveryMethod().isBlank()) {
                    assignment.setDeliveryMethod(item.getDeliveryMethod());
                }

                MilesUpdatedResponse milesUpdatedResponse = milesUpdateService.update(milesUpdatedDto);

                if (milesUpdatedResponse != null && contractResponse != null) {
                    if (milesUpdatedResponse.isDeliverySupplierUpdateSuccess()) {
                        assignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_WILL_START);
                        contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_WILL_START);
                    }
                    if (milesUpdatedResponse.isShipmentStartDateUpdateSuccess()) {
                        assignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_IN_PROGRESS);
                        contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_IN_PROGRESS);
                    }
                    if (milesUpdatedResponse.isShipmentEndDateUpdateSuccess()) {
                        if (contractResponse.getDeliveryTerms() != null &&
                                !contractResponse.getDeliveryTerms().isEmpty()) {
                            assignment.setContractOrderStatus(
                                    ContractOrderStatus.SHIPMENT_DONE_WAITING_DELIVERY_CONDITION);
                            contractResponse.setContractOrderStatus(
                                    ContractOrderStatus.SHIPMENT_DONE_WAITING_DELIVERY_CONDITION);
                        } else {
                            assignment.setContractOrderStatus(
                                    ContractOrderStatus.SHIPMENT_DONE_DELIVERY_TO_BE_PLANNED);
                            contractResponse.setContractOrderStatus(
                                    ContractOrderStatus.SHIPMENT_DONE_DELIVERY_TO_BE_PLANNED);
                        }
                    }
                }

                assignment.setUpdatedBy(null);
                assignment.setUpdatedDate(LocalDateTime.now());
                assignmentsToSave.add(assignment);

                DealerReport dealerReport = reportMap.get(item.getContractId());
                if (dealerReport != null) {
                    if (item.getChassisNumber() != null)
                        dealerReport.setChassisNumber(item.getChassisNumber());
                    if (item.getMotorNumber() != null)
                        dealerReport.setEngineNumber(item.getMotorNumber());
                    if (item.getShipmentStartDate() != null)
                        dealerReport.setShipmentStartDate(LocalDateTime.parse(item.getShipmentStartDate()));
                    if (item.getShipmentEndDate() != null)
                        dealerReport.setShipmentEndDate(LocalDateTime.parse(item.getShipmentEndDate()));
                    if (item.getDeliverySupplier() != null)
                        dealerReport.setDeliveryDealer(item.getDeliverySupplier());
                    if (item.getNetPrice() != null)
                        dealerReport.setProformaTotal(item.getNetPrice());
                    reportsToSave.add(dealerReport);
                }
            }

            assignmentRepository.saveAll(assignmentsToSave);
            dealerReportRepository.saveAll(reportsToSave);

        } catch (Exception e) {
            log.error("Error updating dealer contracts: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating dealer contracts", e);
        }
    }

    @Transactional(readOnly = true)
    public ContractDealerAssignment findByContractId(String contractId) {
        return assignmentRepository
                .findByContractIdAndStatus(contractId, "ACTIVE")
                .orElse(new ContractDealerAssignment());
    }

    @Transactional
    @CacheEvict(value = "customerContracts", allEntries = true)
    public ContractDealerAssignment deliveredContract(String contractId, String completedBy) {
        try {
            Map<String, CustomerContractResponse> contractMap =
                    milesService.getCustomerContractsByIds(Set.of(contractId));

            CustomerContractResponse contractResponse = Optional.ofNullable(contractMap.get(contractId))
                    .orElseThrow(() -> new Exception("İlgili Contract Bulunamadı"));

            ContractDealerAssignment assignment =
                    assignmentRepository.findByContractIdAndStatus(contractId, "ACTIVE")
                            .orElseThrow(() ->
                                    new IllegalStateException("Aktif sipariş bulunamadı: " + contractId));

            DeliveryDocument deliveryDocument =
                    deliveryDocumentRepository.findByContractId(contractId).orElse(null);

            if (!isClosable(assignment, deliveryDocument)) {
                throw new IllegalStateException("Sipariş henüz kapatılmaya hazır değil: " + contractId);
            }

            assignment.setContractStatus(ContractStatus.DELIVERED);
            assignment.setCompletedDate(LocalDateTime.now());
            assignment.setCompletedBy(null);

            ContractDealerAssignment saved = assignmentRepository.save(assignment);

            ProformaReview proformaReview = new ProformaReview();
            proformaReview.setContractId(contractId);
            proformaReview.setDescription(
                    "İlgili Siparişiniz Başarıyla Teslim Edilmiştir. " +
                            "Tamamlanan Siparişinizi 'Sonuçlanan Siparişler' üzerinden görüntüleyebilirsiniz.");
            proformaReview.setTarget(ReviewType.DEALER);
            proformaReviewService.createReview(proformaReview);

            return saved;

        } catch (Exception e) {
            throw new RuntimeException("Error complete order .. ", e);
        }
    }

    private boolean isClosable(ContractDealerAssignment assignment, DeliveryDocument deliveryDocument) {
        return deliveryDocument != null
                && assignment.getDeliveryMethod() != null
                && "ACTIVE".equals(assignment.getStatus());
    }

    public List<DealerContractInfo> getAllDealerContractsWithDetails() {
        log.info("Fetching detailed contracts for all dealers");

        List<ContractDealerAssignment> assignments = assignmentRepository.findByStatus("ACTIVE");
        if (assignments.isEmpty()) {
            log.info("No active assignments found");
            return new ArrayList<>();
        }

        List<CustomerContractResponse> allContracts = milesService.getCustomerContracts();
        if (allContracts == null || allContracts.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, CustomerContractResponse> contractMap = allContracts.stream()
                .collect(Collectors.toMap(
                        CustomerContractResponse::getId,
                        contract -> contract,
                        (existing, replacement) -> existing
                ));

        List<String> contractIds = assignments.stream()
                .map(ContractDealerAssignment::getContractId)
                .collect(Collectors.toList());

        Set<String> proformaIds = new HashSet<>(
                contractProformaRepository.findContractIdsByContractIdIn(contractIds));

        Map<String, ContractLeasingAssignment> leasingMap =
                contractLeasingAssignmentRepository
                        .findByStatusAndContractIdIn("ACTIVE", contractIds)
                        .stream()
                        .collect(Collectors.toMap(ContractLeasingAssignment::getContractId, a -> a));

        Map<String, DeliveryDocument> deliveryDocMap =
                deliveryDocumentRepository.findByContractIdIn(contractIds)
                        .stream()
                        .collect(Collectors.toMap(DeliveryDocument::getContractId, d -> d));

        List<DealerContractInfo> detailedContracts = new ArrayList<>();

        for (ContractDealerAssignment assignment : assignments) {
            try {
                String contractId = assignment.getContractId();
                CustomerContractResponse contract = contractMap.get(contractId);

                if (contract == null) {
                    log.warn("Contract {} not found in MilesApi response", contractId);
                    continue;
                }

                boolean hasProforma = proformaIds.contains(contractId);
                ContractLeasingAssignment leasingAssignment =
                        leasingMap.getOrDefault(contractId, new ContractLeasingAssignment());
                DeliveryDocument doc = deliveryDocMap.get(contractId);
                String deliveryDocumentId = doc != null && doc.getId() != null ?
                        doc.getId().toString() : null;
                String deliveryDocumentName = doc != null ? doc.getFileName() : null;

                DealerContractInfo contractInfo = DealerContractInfo.builder()
                        .id(contract.getId())
                        .contractId(contract.getId())
                        .contractApprovedDate(contract.getContractapproveddate())
                        .customer(contract.getCustomer())
                        .make(contract.getMake())
                        .model(contract.getModel())
                        .modelYear(contract.getModelYear())
                        .leasingName(leasingAssignment.getLeasingName())
                        .sysEnumerationId(leasingAssignment.getLeasingEnumId())
                        .version(contract.getVersion())
                        .color(contract.getColor())
                        .deliveryPerson(contract.getDeliveryPerson())
                        .deliveryLocation(contract.getDeliveryLocation())
                        .ordersId(contract.getOrdersId())
                        .ettn(assignment.getEttn())
                        .deliverySupplier(assignment.getDelivery())
                        .shipmentStartDate(assignment.getShipmentBeginDate())
                        .shipmentEndDate(assignment.getShipmentEndDate())
                        .deliveryDate(contract.getDeliveryDate())
                        .netPrice(assignment.getNetPrice())
                        .otv(assignment.getOtv())
                        .chassisNumber(assignment.getChassisNumber())
                        .motorNumber(assignment.getMotorNumber())
                        .options(contract.getOptions())
                        .uttsGpsInstallation(contract.getUttsGpsInstallation())
                        .treasuryApprovalDate(contract.getTreasuryApprovalDate())
                        .deliveryTerms(contract.getDeliveryTerms())
                        .leasingInvoiceDate(assignment.getLeasingInvoiceDate())
                        .deliveryMethod(assignment.getDeliveryMethod())
                        .dealerName(assignment.getDealerName())
                        .assignedDate(assignment.getAssignedDate())
                        .assignedBy(assignment.getAssignedBy())
                        .status(assignment.getStatus())
                        .hasProforma(hasProforma)
                        .deliveryDocumentId(deliveryDocumentId)
                        .deliveryDocumentName(deliveryDocumentName)
                        .vehicleOrderItemId(contract.getVehicleOrderItemId())
                        .fleetVehicleId(contract.getFleetVehicleId())
                        .contractOrderStatus(assignment.getContractOrderStatus())
                        .dealerBusinessPartnerId(assignment.getDealerBusinessPartnerId())
                        .build();

                detailedContracts.add(contractInfo);

            } catch (Exception e) {
                log.error("Error processing assignment for contract {}: {}",
                        assignment.getContractId(), e.getMessage(), e);
            }
        }

        log.info("Returning {} total detailed contracts", detailedContracts.size());
        return detailedContracts;
    }
}

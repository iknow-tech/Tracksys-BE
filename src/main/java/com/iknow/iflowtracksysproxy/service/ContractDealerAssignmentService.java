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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private final VehicleDocumentService vehicleDocumentService;
    private final VehicleDocumentRepository vehicleDocumentRepository;

    @Transactional
    public AssignDealerResponse assignDealerToContracts(AssignDealerRequest request) throws Exception {

        List<CustomerContractResponse> contracts = request.getContracts();
        int assignedCount = 0;
        int failedCount = 0;
        List<String> failedContractIds = new ArrayList<>();
        List<AssignDealerResponse.AssignedContractInfo> assignedContracts = new ArrayList<>();

        log.info("Dealer: {} (ID: {})", request.getDealerName(), request.getDealerId());
        List<Boolean> updatedMilesSupplier = new ArrayList<>();

        for (CustomerContractResponse contract : contracts) {
            try {
                Optional<ContractDealerAssignment> existingAssignment = assignmentRepository.findByContractId(contract.getId());
                if (existingAssignment.isPresent()) {
                    ContractDealerAssignment contractDealerAssignment = existingAssignment.get();
                    contractDealerAssignment.setContractId(contract.getId());
                    contractDealerAssignment.setDealerBusinessPartnerId(request.getDealerId());
                    contractDealerAssignment.setDealerName(request.getDealerName());
                    contractDealerAssignment.setAssignedBy(request.getAssignedBy());
                    contractDealerAssignment.setAssignedDate(LocalDateTime.now());
                    contractDealerAssignment.setStatus("ACTIVE");
                    contractDealerAssignment.setNotes("Web UI  üzerinden atama.");
                    contractDealerAssignment.setDealerEmail("");
                    assignmentRepository.save(contractDealerAssignment);
                } else {
                    ContractDealerAssignment newAssignment = ContractDealerAssignment.builder()
                            .contractId(contract.getId())
                            .dealerBusinessPartnerId(request.getDealerId())
                            .dealerContactId(request.getDealerContactId())
                            .dealerName(request.getDealerName())
                            .assignedBy(request.getAssignedBy())
                            .assignedDate(LocalDateTime.now())
                            .status("ACTIVE")
                            .notes("Web UI  üzerinden atama.")
                            .build();

                    contract.setAssignedDealer(request.getDealerName());
                    newAssignment = assignmentRepository.save(newAssignment);
                }

                if (contract.getOrdersId() != null) {
                    // Supplier ve Contact Bilgisinin Güncellenmesi
                    VehicleOrderSupplierUpdateRequest vehicleOrderSupplierUpdateRequest = new VehicleOrderSupplierUpdateRequest();
                    vehicleOrderSupplierUpdateRequest.setOrdersId(contract.getOrdersId());
                    vehicleOrderSupplierUpdateRequest.setSupplierId(request.getDealerId());
                    vehicleOrderSupplierUpdateRequest.setContactId(request.getDealerContactId());
                    VehicleOrderSupplierUpdateBaseResponse response = milesApi.vehicleorderSupplierUpdate(vehicleOrderSupplierUpdateRequest);

                    Boolean isMilesUpdateSuccess = response.getData()
                            .getResponseVehicleOrderSupplierUpdate()
                            .stream()
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException("Supplier update sonucu yok"))
                            .getResult().equals("1");

                    updatedMilesSupplier.add(isMilesUpdateSuccess);
                } else {
                    throw new IllegalArgumentException(
                            "OrdersId bulunamadı. Bu kontrat için önce Miles sipariş kaydı oluşmalıdır."
                    );
                }


                // raporlama için eklendi
                DealerReport dealerReport = dealerReportRepository
                        .findByContractId(contract.getId())
                        .orElse(new DealerReport());

                dealerReport.setContractId(contract.getId());
                dealerReport.setCustomer(contract.getCustomer());
                dealerReport.setVehicleDescription(contract.getMake() + " " + contract.getModel() + " " + contract.getVersion());
                dealerReport.setColor(contract.getColor());
                dealerReport.setDealer(request.getDealerName());
                dealerReport.setDeliveryDealer(contract.getDeliveryPerson());
                dealerReport.setShipmentCityContract(contract.getDeliveryLocation());
                dealerReport.setStatus("ACTIVE");

                dealerReportRepository.save(dealerReport);


                assignedCount++;

                log.info(" Contract {} assigned successfully", contract.getId());

            } catch (Exception e) {
                log.error("Failed to assign contract {}: {}", contract.getId(), e.getMessage(), e);
                failedCount++;
                failedContractIds.add(contract.getId());
                throw new Exception("Atama sırasında bir hata oluştu");

            }
        }

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
                assignmentRepository.findLatestAssignmentsPerContract(
                        dealerId,
                        "ACTIVE"
                );

        if (assignments.isEmpty()) {
            log.info("No active assignments found for dealer: {}", dealerId);
            return new ArrayList<>();
        }

        List<CustomerContractResponse> allContracts = customerContractCache.get();

        if (allContracts == null || allContracts.isEmpty()) {
            milesContractSyncService.refreshCacheFromMiles("DEALER_ON_DEMAND");
            allContracts = customerContractCache.get();
        }

        if (allContracts == null || allContracts.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, CustomerContractResponse> contractMap = allContracts.stream()
                .collect(Collectors.toMap(
                        CustomerContractResponse::getId,
                        contract -> contract,
                        (existing, replacement) -> existing
                ));


        List<DealerContractInfo> detailedContracts = new ArrayList<>();


        for (ContractDealerAssignment assignment : assignments) {
            String deliveryDocumentId = null;
            String deliveryDocumentName = null;
            ContractLeasingAssignment leasingAssignment = new ContractLeasingAssignment();
            DeliveryDocument deliveryDocument = new DeliveryDocument();
            try {
                String contractId = assignment.getContractId();
                log.debug("📄 Processing contract: {}", contractId);
                CustomerContractResponse contract = contractMap.get(contractId);

                if (contract == null) {
                    log.warn("⚠️ Contract {} not found in MilesApi response", contractId);
                    continue;
                }

                boolean hasProforma = contractProformaRepository.existsByContractId(contractId);

                Optional<ContractLeasingAssignment> leasingAssignmentOptional = contractLeasingAssignmentRepository.findByContractIdAndStatus(contractId, "ACTIVE");
                if (leasingAssignmentOptional.isPresent()) {
                    leasingAssignment = leasingAssignmentOptional.get();
                }

                Optional<DeliveryDocument> deliveryDocumentOptional = deliveryDocumentRepository.findByContractId(contractId);
                if (deliveryDocumentOptional.isPresent()) {
                    deliveryDocument = deliveryDocumentOptional.get();
                }


                if (deliveryDocument != null) {
                    deliveryDocumentId = deliveryDocument.getId() != null ? deliveryDocument.getId().toString() : null;
                    deliveryDocumentName = deliveryDocument.getFileName();
                }

                if (assignment.getDealerInvoiceMailSentAt() == null && contract.getTreasuryApprovalDate() != null && contract.getOrdersId() != null) {

                }
                VehicleDocumentAssignment vehicleDocumentAssignment = vehicleDocumentRepository.findByContractIdAndStatus(contractId,"ACTIVE").orElse(null);

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
                        .licensePlate(vehicleDocumentAssignment != null ? vehicleDocumentAssignment.getLicensePlate(): null)


                        // Atama Bilgileri (assignment objesinden)
                        .dealerName(assignment.getDealerName())
                        .assignedDate(assignment.getAssignedDate())
                        .assignedBy(assignment.getAssignedBy())
                        .status(assignment.getStatus())
                        .hasProforma(hasProforma)
                        .deliveryDocumentId(deliveryDocumentId)
                        .deliveryDocumentName(deliveryDocumentName)
                        .contractOrderStatus(assignment.getContractOrderStatus())
                        .build();

                detailedContracts.add(contractInfo);

                log.debug(" Contract {} processed successfully", contractId);

            } catch (Exception e) {
                log.error("Error processing assignment for contract {}: {}",
                        assignment.getContractId(), e.getMessage(), e);
            }
        }

        log.info("Returning {} detailed contracts for dealer {}",
                detailedContracts.size(), dealerId);

        return detailedContracts;
    }

    @Transactional
    public void unassignDealer(UnassignDealerRequest request) {
        ContractDealerAssignment assignment =
                assignmentRepository
                        .findFirstByContractIdAndStatus(
                                request.getContractId(), "ACTIVE")
                        .orElseThrow(() ->
                                new RuntimeException("Aktif bayi ataması bulunamadı"));

        assignment.setStatus("CANCELLED");
        assignment.setCancelledBy(request.getCancelledBy());
        assignment.setCancelledDate(LocalDateTime.now());

        assignmentRepository.save(assignment);
        customerContractCache.clear();


        log.info("Contract {} dealer assignment cancelled", request.getContractId());
    }

    @Transactional
    public void updateDealerContracts(DealerContractUpdateRequest request) {
        if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
            return;
        }

        try {
            for (DealerContractUpdateItemRequest item : request.getUpdates()) {
                CustomerContractResponse contractResponse =
                        milesService.findCustomerContractById(item.getContractId())
                                .orElse(new CustomerContractResponse());

                MilesUpdatedDto milesUpdatedDto = new MilesUpdatedDto();
                milesUpdatedDto.setContractId(item.getContractId());
                ContractDealerAssignment contractDealerAssignment = assignmentRepository.findByContractIdAndStatus(item.getContractId(), "ACTIVE")
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Contract not found: " + item.getContractId()
                                )
                        );
                if (item.getNetPrice() != null) {
                    contractDealerAssignment.setNetPrice(item.getNetPrice());
                    milesUpdatedDto.setNetPrice(item.getNetPrice().toString());
                }
                if (item.getOtv() != null) {
                    contractDealerAssignment.setOtv(item.getOtv());
                    milesUpdatedDto.setOtv(item.getOtv().toString());
                }
                if (item.getMotorNumber() != null && !item.getMotorNumber().isBlank()) {
                    contractDealerAssignment.setMotorNumber(item.getMotorNumber());
                    milesUpdatedDto.setMotorNumber(item.getMotorNumber().toString());
                }
                if (item.getChassisNumber() != null && !item.getChassisNumber().isBlank()) {
                    contractDealerAssignment.setChassisNumber(item.getChassisNumber());
                    milesUpdatedDto.setChassisNumber(item.getChassisNumber().toString());

                }

                if (item.getDeliverySupplier() != null && !item.getDeliverySupplier().isBlank()) {
                    contractDealerAssignment.setDelivery(item.getDeliverySupplier());
                    milesUpdatedDto.setDelivery(item.getDeliverySupplier().toString());
                }

                if (item.getShipmentStartDate() != null) {
                    contractDealerAssignment.setShipmentBeginDate(item.getShipmentStartDate());
                    milesUpdatedDto.setShipmentStartDate(LocalDate.parse(item.getShipmentStartDate()));
                    milesUpdatedDto.setDeliveryConditionId(contractResponse.getDeliveryConditionId());
                }
                if (item.getShipmentEndDate() != null) {
                    contractDealerAssignment.setShipmentEndDate(item.getShipmentEndDate());
                    milesUpdatedDto.setShipmentEndDate(LocalDate.parse(item.getShipmentEndDate()));
                    milesUpdatedDto.setDeliveryConditionId(contractResponse.getDeliveryConditionId());
                }
                if (item.getEttn() != null && !item.getEttn().isBlank()) {
                    contractDealerAssignment.setEttn(item.getEttn());
                }
                if (item.getDeliveryDate() != null) {
                    contractDealerAssignment.setDeliveryDate(item.getDeliveryDate());
                }
                if (item.getLeasingInvoiceDate() != null) {
                    contractDealerAssignment.setLeasingInvoiceDate(item.getLeasingInvoiceDate());
                }
                if (item.getDeliveryMethod() != null && !item.getDeliveryMethod().isBlank()) {
                    contractDealerAssignment.setDeliveryMethod(item.getDeliveryMethod());
                }
                MilesUpdatedResponse milesUpdatedResponse = milesUpdateService.update(milesUpdatedDto);

                if (milesUpdatedResponse != null) {
                    if (milesUpdatedResponse.isDeliverySupplierUpdateSuccess()) {
                        contractDealerAssignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_WILL_START);
                        contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_WILL_START);
                    }
                    if (milesUpdatedResponse.isShipmentStartDateUpdateSuccess()) {
                        contractDealerAssignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_IN_PROGRESS);
                        contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_IN_PROGRESS);
                    }
                    if (milesUpdatedResponse.isShipmentEndDateUpdateSuccess()) {
                        if (contractResponse.getDeliveryTerms() != null && !contractResponse.getDeliveryTerms().isEmpty()) {
                            contractDealerAssignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_DONE_WAITING_DELIVERY_CONDITION);
                            contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_DONE_WAITING_DELIVERY_CONDITION);
                        } else {
                            contractDealerAssignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_DONE_DELIVERY_TO_BE_PLANNED);
                            contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_DONE_DELIVERY_TO_BE_PLANNED);
                        }
                    }
// teslimat tarihi
//                    if (milesUpdatedResponse.getShipmentStartDateUpdateSuccess()) {
//                        contractDealerAssignment.setContractOrderStatus(ContractOrderStatus.SHIPMENT_IN_PROGRESS);
//                        contractResponse.setContractOrderStatus(ContractOrderStatus.SHIPMENT_IN_PROGRESS);
//                    }

                }
                contractDealerAssignment.setUpdatedBy(null);
                contractDealerAssignment.setUpdatedDate(LocalDateTime.now());
                assignmentRepository.save(contractDealerAssignment);
                // Dealer report için eklendi
                dealerReportRepository.findByContractId(item.getContractId()).ifPresent(dealerReport -> {
                    if (item.getChassisNumber() != null) {
                        dealerReport.setChassisNumber(item.getChassisNumber());
                    }
                    if (item.getMotorNumber() != null) {
                        dealerReport.setEngineNumber(item.getMotorNumber());
                    }
                    if (item.getShipmentStartDate() != null) {
                        dealerReport.setShipmentStartDate(LocalDate.parse(item.getShipmentStartDate()));
                    }
                    if (item.getShipmentEndDate() != null) {
                        dealerReport.setShipmentEndDate(LocalDate.parse(item.getShipmentEndDate()));
                    }
                    if (item.getDeliverySupplier() != null) {
                        dealerReport.setDeliveryDealer(item.getDeliverySupplier());
                    }
                    if (item.getNetPrice() != null) {
                        dealerReport.setProformaTotal(item.getNetPrice());
                    }

                    dealerReportRepository.save(dealerReport);
                });
            }
        } catch (Exception e) {
            log.error("Error updating dealer contracts: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating dealer contracts", e);
        }
    }

    @Transactional(readOnly = true)
    public ContractDealerAssignment findByContractId(String contractId) {
        ContractDealerAssignment contractDealerAssignment = new ContractDealerAssignment();
        Optional<ContractDealerAssignment> optionalContractDealerAssignment = assignmentRepository.findByContractIdAndStatus(contractId, "ACTIVE");

        if (optionalContractDealerAssignment.isPresent()) {
            contractDealerAssignment = optionalContractDealerAssignment.get();
        }
        return contractDealerAssignment;
    }

    // Sipariş Teslim Edildi - Sipariş statusu 'DELIVERED' yapılacak.
    @Transactional
    public ContractDealerAssignment deliveredContract(String contractId, String completedBy) {
        DeliveryDocument deliveryDocument = new DeliveryDocument();

        try {
            milesService.findCustomerContractById(contractId)
                    .orElseThrow(() -> new Exception("İlgili Contract Bulunamadı"));

            ContractDealerAssignment assignment =
                    assignmentRepository.findByContractIdAndStatus(contractId, "ACTIVE")
                            .orElseThrow(() ->
                                    new IllegalStateException("Aktif sipariş bulunamadı: " + contractId)
                            );
            if (deliveryDocumentRepository.findByContractId(contractId).isPresent()) {
                deliveryDocument = deliveryDocumentRepository.findByContractId(contractId).get();
            }
            Boolean contractClosable = isClosable(assignment, deliveryDocument);

            if (!Boolean.TRUE.equals(contractClosable)) {
                throw new IllegalStateException(
                        "Sipariş henüz kapatılmaya hazır değil: " + contractId
                );
            }

            // TO DO bir siparişin kapatılabilmesi için hangi şartların yerine getirilmesi gerekiyor?
            assignment.setContractStatus(ContractStatus.DELIVERED);
            assignment.setCompletedDate(LocalDateTime.now());
            assignment.setCompletedBy(null);

            ContractDealerAssignment contractDealerAssignment = assignmentRepository.save(assignment);

            // notification to dealer
            ProformaReview proformaReview = new ProformaReview();
            proformaReview.setContractId(contractId);
            proformaReview.setDescription("İlgili Siparişiniz Başarıyla Teslim Edilmiştir.Tamamlanan Siparişinizi 'Sonuçlanan Siparişler' üzerinden görüntüleyebilirsiniz.");
            proformaReview.setTarget(ReviewType.DEALER);
            proformaReviewService.createReview(proformaReview);

            return contractDealerAssignment;
        } catch (Exception e) {
            throw new RuntimeException("Error complete order .. ", e);
        }
    }

    private boolean isClosable(ContractDealerAssignment assignment,
                               DeliveryDocument deliveryDocument) {

        return deliveryDocument != null
                && assignment.getDeliveryMethod() != null
                && "ACTIVE".equals(assignment.getStatus());
    }

    public List<DealerContractInfo> getAllDealerContractsWithDetails() {

        log.info("Fetching detailed contracts for all dealers");

        List<ContractDealerAssignment> assignments =
                assignmentRepository.findByStatus("ACTIVE");

        if (assignments.isEmpty()) {
            log.info("No active assignments found");
            return new ArrayList<>();
        }

        List<CustomerContractResponse> allContracts = customerContractCache.get();

        if (allContracts == null || allContracts.isEmpty()) {
            milesContractSyncService.refreshCacheFromMiles("ADMIN_ON_DEMAND");
            allContracts = customerContractCache.get();
        }

        if (allContracts == null || allContracts.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, CustomerContractResponse> contractMap = allContracts.stream()
                .collect(Collectors.toMap(
                        CustomerContractResponse::getId,
                        contract -> contract,
                        (existing, replacement) -> existing
                ));

        List<DealerContractInfo> detailedContracts = new ArrayList<>();

        for (ContractDealerAssignment assignment : assignments) {
            try {
                String contractId = assignment.getContractId();
                CustomerContractResponse contract = contractMap.get(contractId);

                if (contract == null) {
                    log.warn("Contract {} not found in MilesApi response", contractId);
                    continue;
                }

                boolean hasProforma = contractProformaRepository.existsByContractId(contractId);

                ContractLeasingAssignment leasingAssignment = new ContractLeasingAssignment();
                Optional<ContractLeasingAssignment> leasingOpt =
                        contractLeasingAssignmentRepository.findByContractIdAndStatus(contractId, "ACTIVE");
                if (leasingOpt.isPresent()) {
                    leasingAssignment = leasingOpt.get();
                }

                String deliveryDocumentId = null;
                String deliveryDocumentName = null;
                Optional<DeliveryDocument> deliveryDocOpt =
                        deliveryDocumentRepository.findByContractId(contractId);
                if (deliveryDocOpt.isPresent()) {
                    DeliveryDocument doc = deliveryDocOpt.get();
                    deliveryDocumentId = doc.getId() != null ? doc.getId().toString() : null;
                    deliveryDocumentName = doc.getFileName();
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

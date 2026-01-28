package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.dto.DealerContractInfo;
import com.iknow.iflowtracksysproxy.dto.request.AssignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.request.DealerContractUpdateItemRequest;
import com.iknow.iflowtracksysproxy.dto.request.DealerContractUpdateRequest;
import com.iknow.iflowtracksysproxy.dto.request.UnassignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.response.AssignDealerResponse;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractLeasingAssignment;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.respository.ContractDealerAssignmentRepository;
import com.iknow.iflowtracksysproxy.respository.ContractLeasingAssignmentRepository;
import com.iknow.iflowtracksysproxy.respository.ContractProformaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public AssignDealerResponse assignDealerToContracts(AssignDealerRequest request) {

        List<CustomerContractResponse> contracts = request.getContracts();
        int assignedCount = 0;
        int failedCount = 0;
        List<String> failedContractIds = new ArrayList<>();
        List<AssignDealerResponse.AssignedContractInfo> assignedContracts = new ArrayList<>();

        log.info("Dealer: {} (ID: {})", request.getDealerName(), request.getDealerId());

        for (CustomerContractResponse contract : contracts) {
            try {

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
                assignmentRepository.save(newAssignment);
                assignedCount++;

                log.info(" Contract {} assigned successfully", contract.getId());

            } catch (Exception e) {
                log.error("Failed to assign contract {}: {}", contract.getId(), e.getMessage(), e);
                failedCount++;
                failedContractIds.add(contract.getId());
            }
        }

        return AssignDealerResponse.builder()
                .success(failedCount == 0)
                .assignedCount(assignedCount)
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
            milesContractSyncService.syncFromMiles("DEALER_ON_DEMAND");
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
            ContractLeasingAssignment leasingAssignment =new ContractLeasingAssignment();
            try {
                String contractId = assignment.getContractId();
                log.debug("📄 Processing contract: {}", contractId);
                CustomerContractResponse contract = contractMap.get(contractId);

                if (contract == null) {
                    log.warn("⚠️ Contract {} not found in MilesApi response", contractId);
                    continue;
                }

                boolean hasProforma = contractProformaRepository.existsByContractId(contractId);

                Optional<ContractLeasingAssignment> leasingAssignmentOptional= contractLeasingAssignmentRepository.findByContractIdAndStatus(contractId,"ACTIVE");
                if(leasingAssignmentOptional.isPresent()){
                    leasingAssignment = leasingAssignmentOptional.get();
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
                        .delivery(assignment.getDelivery())
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

                        // Atama Bilgileri (assignment objesinden)
                        .dealerName(assignment.getDealerName())
                        .assignedDate(assignment.getAssignedDate())
                        .assignedBy(assignment.getAssignedBy())
                        .status(assignment.getStatus())
                        .hasProforma(hasProforma)

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

        log.info("Contract {} dealer assignment cancelled", request.getContractId());
    }

    @Transactional
    public void updateDealerContracts(DealerContractUpdateRequest request) {
        if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
            return;
        }

        try{
        for (DealerContractUpdateItemRequest item: request.getUpdates()) {
            ContractDealerAssignment contractDealerAssignment = assignmentRepository.findByContractIdAndStatus(item.getContractId(),"ACTIVE")
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Contract not found: " + item.getContractId()
                                    )
                            );
            if (item.getNetPrice() != null) {
                contractDealerAssignment.setNetPrice(item.getNetPrice());
            }
            if (item.getOtv() != null) {
                contractDealerAssignment.setOtv(item.getOtv());
            }
            if (item.getMotorNumber() != null && !item.getMotorNumber().isBlank()) {
                contractDealerAssignment.setMotorNumber(item.getMotorNumber());
            }
            if (item.getChassisNumber() != null && !item.getChassisNumber().isBlank()) {
                contractDealerAssignment.setChassisNumber(item.getChassisNumber());
            }

            if(item.getDelivery() != null && !item.getDelivery().isBlank()){
                contractDealerAssignment.setDelivery(item.getDelivery());
            }

            if(item.getShipmentBeginDate() != null ){
                contractDealerAssignment.setShipmentBeginDate(item.getShipmentBeginDate());
            }
            if(item.getShipmentEndDate() != null ){
                contractDealerAssignment.setShipmentEndDate(item.getShipmentEndDate());
            }
            if(item.getEttn() != null && !item.getEttn().isBlank()){
                contractDealerAssignment.setEttn(item.getEttn());
            }
            if(item.getDeliveryDate() != null ){
                contractDealerAssignment.setDeliveryDate(item.getDeliveryDate());
            }

            contractDealerAssignment.setUpdatedBy(null);
            contractDealerAssignment.setUpdatedDate(LocalDateTime.now());

            assignmentRepository.save(contractDealerAssignment);
        }}
        catch (Exception e){
            log.error(e.getMessage());
        }
    }

    public ContractDealerAssignment findByContractId(String contractId){
        ContractDealerAssignment contractDealerAssignment = new ContractDealerAssignment();
        Optional<ContractDealerAssignment> optionalContractDealerAssignment= assignmentRepository.findByContractIdAndStatus(contractId,"ACTIVE");

        if(optionalContractDealerAssignment.isPresent()){
            contractDealerAssignment=  optionalContractDealerAssignment.get();
        }
        return contractDealerAssignment;
    }



}

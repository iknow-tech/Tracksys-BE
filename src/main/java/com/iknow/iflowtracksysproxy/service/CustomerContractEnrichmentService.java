package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractLeasingAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractOrderStatus;
import com.iknow.iflowtracksysproxy.entity.ContractStatus;
import com.iknow.iflowtracksysproxy.entity.DeliveryDocument;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.respository.ContractDealerAssignmentRepository;
import com.iknow.iflowtracksysproxy.respository.ContractLeasingAssignmentRepository;
import com.iknow.iflowtracksysproxy.respository.ContractProformaRepository;
import com.iknow.iflowtracksysproxy.respository.DeliveryDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerContractEnrichmentService {

    private final ContractDealerAssignmentRepository contractDealerAssignmentRepository;
    private final ContractLeasingAssignmentRepository contractLeasingAssignmentRepository;
    private final ContractProformaRepository contractProformaRepository;
    private final DeliveryDocumentRepository deliveryDocumentRepository;

    public List<CustomerContractResponse> enrich(List<CustomerContractResponse> contracts) {
        List<CustomerContractResponse> copies = copyContracts(contracts);
        if (copies.isEmpty()) {
            return copies;
        }

        List<String> contractIds = copies.stream()
                .map(CustomerContractResponse::getId)
                .filter(id -> id != null && !id.isBlank())
                .toList();

        if (contractIds.isEmpty()) {
            return copies;
        }

        List<ContractDealerAssignment> dealerAssignments =
                contractDealerAssignmentRepository.findByContractIdIn(contractIds);
        List<ContractLeasingAssignment> leasingAssignments =
                contractLeasingAssignmentRepository.findByStatusAndContractIdIn("ACTIVE", contractIds);
        Map<String, DeliveryDocument> deliveryDocumentMap =
                deliveryDocumentRepository.findByContractIdIn(contractIds)
                        .stream()
                        .collect(Collectors.toMap(
                                DeliveryDocument::getContractId,
                                document -> document,
                                (existing, replacement) -> existing
                        ));
        Set<String> proformaContractIds = new HashSet<>(
                contractProformaRepository.findContractIdsByContractIdIn(contractIds)
        );

        Map<String, ContractDealerAssignment> dealerMap =
                dealerAssignments.stream()
                        .collect(Collectors.toMap(
                                ContractDealerAssignment::getContractId,
                                assignment -> assignment,
                                (existing, replacement) -> existing
                        ));

        Map<String, ContractLeasingAssignment> leasingMap =
                leasingAssignments.stream()
                        .collect(Collectors.toMap(
                                ContractLeasingAssignment::getContractId,
                                assignment -> assignment,
                                (existing, replacement) -> existing
                        ));

        for (CustomerContractResponse contractResponse : copies) {
            String contractId = contractResponse.getId();

            ContractLeasingAssignment leasing = leasingMap.get(contractId);
            DeliveryDocument deliveryDocument = deliveryDocumentMap.get(contractId);
            if (deliveryDocument != null) {
                contractResponse.setDeliveryDocumentId(deliveryDocument.getId().toString());
                contractResponse.setDeliveryDocumentName(deliveryDocument.getFileName());
            }

            if (leasing != null) {
                contractResponse.setAssignedLeasing(leasing.getLeasingName());
                contractResponse.setSysEnumerationId(leasing.getLeasingEnumId());
            } else {
                contractResponse.setAssignedLeasing(null);
                contractResponse.setSysEnumerationId(null);
            }

            ContractDealerAssignment dealer = dealerMap.get(contractId);
            if (dealer != null && "ACTIVE".equals(dealer.getStatus())) {
                contractResponse.setAssignedDealer(dealer.getDealerName());
                contractResponse.setDeliveryMethod(dealer.getDeliveryMethod());
                if (dealer.getLeasingInvoiceDate() != null) {
                    contractResponse.setLeasingInvoiceDate(dealer.getLeasingInvoiceDate());
                }
                if (dealer.getStatus().equals(ContractStatus.DELIVERED.toString())) {
                    contractResponse.setStatus(ContractStatus.DELIVERED);
                    contractResponse.setDeliveredBy(null);
                    contractResponse.setOrderDeliveredDate(LocalDateTime.now());
                }
                contractResponse.setContractOrderStatus(dealer.getContractOrderStatus());
            }

            contractResponse.setHasProforma(proformaContractIds.contains(contractId));
        }

        return copies;
    }

    public CustomerContractResponse copyOf(CustomerContractResponse source) {
        if (source == null) {
            return null;
        }

        CustomerContractResponse copy = new CustomerContractResponse();
        BeanUtils.copyProperties(source, copy);
        return copy;
    }

    private List<CustomerContractResponse> copyContracts(List<CustomerContractResponse> contracts) {
        if (contracts == null || contracts.isEmpty()) {
            return List.of();
        }

        return contracts.stream()
                .map(this::copyOf)
                .toList();
    }
}

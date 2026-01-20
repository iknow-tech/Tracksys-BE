package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.DealerContractInfo;
import com.iknow.iflowtracksysproxy.dto.request.AssignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.request.AssignLeasingRequest;
import com.iknow.iflowtracksysproxy.dto.request.UnassignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.response.AssignDealerResponse;
import com.iknow.iflowtracksysproxy.dto.response.AssignLeasingResponse;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.entity.ContractLeasingAssignment;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.respository.ContractDealerAssignmentRepository;
import com.iknow.iflowtracksysproxy.respository.ContractLeasingAssignmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContracLeasingAssignmentService {

    private final ContractLeasingAssignmentRepository assignmentRepository;

    private final MilesApi milesApi;

    @Transactional
    public AssignLeasingResponse assignLeasingToContracts(AssignLeasingRequest request) {

        List<CustomerContractResponse> contracts = request.getContracts();
        int assignedCount = 0;
        int failedCount = 0;
        List<String> failedContractIds = new ArrayList<>();
        List<AssignLeasingResponse.AssignedContractInfo> assignedContracts = new ArrayList<>();


        for (CustomerContractResponse contract : contracts) {
            try {

                assignmentRepository.passiveAllActiveByContractId(contract.getId());

                ContractLeasingAssignment assignment = new ContractLeasingAssignment();
                assignment.setContractId(contract.getId());
                assignment.setNotes("Web UI üzerinden atama.");

                if (request.getSysEnumerationId() != null) {
                    assignment.setLeasingEnumId(request.getSysEnumerationId());
                    assignment.setLeasingName(request.getDescription());
                    assignment.setAssignedDate(LocalDateTime.now());
                    assignment.setAssignedBy(request.getAssignedBy());
                    assignment.setStatus("ACTIVE");
                    contract.setAssignedLeasing(request.getDescription());
                    contract.setSysEnumerationId(request.getSysEnumerationId());
                    assignmentRepository.save(assignment);

                } else {
                    contract.setAssignedLeasing(null);
                    contract.setSysEnumerationId(null);
                    assignment.setLeasingEnumId(request.getSysEnumerationId());
                    assignment.setLeasingName(request.getDescription());
                    assignment.setAssignedDate(LocalDateTime.now());
                    assignment.setAssignedBy(request.getAssignedBy());
                    assignment.setStatus("CANCELED");
                    assignmentRepository.save(assignment);

                }

                assignedCount++;

                log.info(" Contract {} assigned leasing successfully", contract.getId());

            } catch (Exception e) {
                log.error("Failed to assign contract {}: {}", contract.getId(), e.getMessage(), e);
                failedCount++;
                failedContractIds.add(contract.getId());
            }
        }

        return AssignLeasingResponse.builder()
                .success(failedCount == 0)
                .assignedCount(assignedCount)
                .failedCount(failedCount)
                .failedContractIds(failedContractIds)
                .assignedContracts(assignedContracts)
                .build();

    }




}

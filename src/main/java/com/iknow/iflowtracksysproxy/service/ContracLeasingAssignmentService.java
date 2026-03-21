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
import com.iknow.iflowtracksysproxy.integration.miles.model.request.MulkUpdateRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.PropertyTypeUpdateRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.MulkUpdateResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.PropertyTypeUpdateResponse;
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

                    // ✅ ÖNCE miles update — başarılı olursa kaydet
                    // Vehicle Order Statüsü Güncelleme
                    PropertyTypeUpdateRequest propertyTypeUpdateRequest = new PropertyTypeUpdateRequest("266", "10282", "1005943");
                    PropertyTypeUpdateResponse propertyTypeUpdateResponse = milesApi.updateProperty(propertyTypeUpdateRequest, contract.getOrdersId());
                    boolean hasBusinessError = Boolean.parseBoolean(propertyTypeUpdateResponse.getMetadata().getOperationStatus().getBusinessError());
                    if (hasBusinessError) {
                        throw new RuntimeException("Vehicle order statüsü güncellenemedi: " + contract.getId());
                    }
                    contract.setUpdateVehicleOrderItemStatu(true);

                    // Mülkiyet Türü Güncelleme
                    MulkUpdateRequest mulkiyetUpdateRequest = new MulkUpdateRequest();
                    mulkiyetUpdateRequest.setFleetVehicleId(contract.getFleetVehicleId());
                    mulkiyetUpdateRequest.setFieldId("2942");
                    mulkiyetUpdateRequest.setSroid("68");
                    mulkiyetUpdateRequest.setValue(assignment.getLeasingEnumId());
                    MulkUpdateResponse mulkiyetUpdateResponse = milesApi.updateMulk(mulkiyetUpdateRequest);
                    boolean mulkiyetError = Boolean.parseBoolean(mulkiyetUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError());
                    if (mulkiyetError) {
                        throw new RuntimeException("Mülkiyet türü güncellenemedi: " + contract.getId());
                    }
                    contract.setMulkiyetUpdateSuccess(true);

                    // Mülk Alanı Güncelleme
                    MulkUpdateRequest mulkUpdateRequest = new MulkUpdateRequest();
                    mulkUpdateRequest.setFleetVehicleId(contract.getFleetVehicleId());
                    mulkUpdateRequest.setFieldId("1001733");
                    mulkUpdateRequest.setSroid("68");
                    mulkUpdateRequest.setValue("1006514");
                    MulkUpdateResponse mulkUpdateResponse = milesApi.updateMulk(mulkUpdateRequest);
                    boolean mulkError = Boolean.parseBoolean(mulkUpdateResponse.getResponsemetadata().getOperationStatus().getBusinessError());
                    if (mulkError) {
                        throw new RuntimeException("Mülk alanı güncellenemedi: " + contract.getId());
                    }
                    contract.setMulkUpdateSuccess(true);

                    // ✅ Tüm miles update'ler başarılıysa kaydet
                    assignmentRepository.save(assignment);
                    log.info("Contract {} leasing assigned and miles updated successfully", contract.getId());
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

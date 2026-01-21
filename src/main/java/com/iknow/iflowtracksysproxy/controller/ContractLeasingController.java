package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.dto.DealerContractInfo;
import com.iknow.iflowtracksysproxy.dto.request.AssignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.request.AssignLeasingRequest;
import com.iknow.iflowtracksysproxy.dto.request.UnassignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.response.AssignDealerResponse;
import com.iknow.iflowtracksysproxy.dto.response.AssignLeasingResponse;
import com.iknow.iflowtracksysproxy.service.ContracLeasingAssignmentService;
import com.iknow.iflowtracksysproxy.service.ContractDealerAssignmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ContractLeasingController {

    private final ContracLeasingAssignmentService assignmentService;

    @PostMapping("/assign-leasing")
    public ResponseEntity<AssignLeasingResponse> assignLeasing(@RequestBody AssignLeasingRequest request) {

        log.info("Leasing assignment request received:");

        try {
            AssignLeasingResponse response = assignmentService.assignLeasingToContracts(request);

            log.info("Assignment completed: {} successful, {} failed",
                    response.getAssignedCount(),
                    response.getFailedCount());


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Assignment failed: {}", e.getMessage(), e);

            AssignLeasingResponse errorResponse = AssignLeasingResponse.builder()
                    .success(false)
                    .assignedCount(0)
                    .failedCount(request.getContracts().size())
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }


}

package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.dto.DealerContractInfo;
import com.iknow.iflowtracksysproxy.dto.request.AssignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.request.DealerContractUpdateRequest;
import com.iknow.iflowtracksysproxy.dto.request.UnassignDealerRequest;
import com.iknow.iflowtracksysproxy.dto.response.AssignDealerResponse;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
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
public class ContractDealerController {

    private final ContractDealerAssignmentService assignmentService;

    @PostMapping("/assign-dealer")
    public ResponseEntity<AssignDealerResponse> assignDealer(@RequestBody AssignDealerRequest request) {

        log.info("Dealer assignment request received:");

        try {
            AssignDealerResponse response = assignmentService.assignDealerToContracts(request);

            log.info("Assignment completed: {} successful, {} failed",
                    response.getAssignedCount(),
                    response.getFailedCount());


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Assignment failed: {}", e.getMessage(), e);

            AssignDealerResponse errorResponse = AssignDealerResponse.builder()
                    .success(false)
                    .assignedCount(0)
                    .failedCount(request.getContracts().size())
                    .build();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @GetMapping("/dealer/{dealerId}/contracts/detailed")
    public ResponseEntity<List<DealerContractInfo>> getDealerContractsDetailed(@PathVariable String dealerId) {

        try {
            List<DealerContractInfo> contracts = assignmentService.getDealerContractsWithDetails(dealerId);

            log.info("Returning {} detailed contracts for dealer {}", contracts.size(), dealerId);

            return ResponseEntity.ok(contracts);

        } catch (Exception e) {
            log.error("Error getting detailed dealer contracts for dealer {}: {}", dealerId, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/unassign-dealer")
    public ResponseEntity<?> unassignDealer(@RequestBody UnassignDealerRequest request) {

        assignmentService.unassignDealer(request);

        return ResponseEntity.ok(java.util.Map.of(
                "success", true,
                "message", "Bayi ataması iptal edildi"
        ));
    }

    @PutMapping("/dealer-contract/update")
    public ResponseEntity<Void> updateDealerContracts(@RequestBody DealerContractUpdateRequest request
    ) {
        assignmentService.updateDealerContracts(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dealer-contract/{contractId}")
    public ResponseEntity<ContractDealerAssignment> getByContractId(@PathVariable String contractId) {
       ContractDealerAssignment contractDealerAssignment=  assignmentService.findByContractId(contractId);
       return ResponseEntity.ok(contractDealerAssignment);
    }

    // Sipariş teslim edildi
    @PostMapping("/{contractId}/complete")
    public ResponseEntity<ContractDealerAssignment> deliveredContract(@PathVariable String contractId, @RequestParam(required = false) String completedBy
    ) {
       ContractDealerAssignment contractDealerAssignment=  assignmentService.deliveredContract(contractId, completedBy);
        return ResponseEntity.ok(contractDealerAssignment);
    }


}

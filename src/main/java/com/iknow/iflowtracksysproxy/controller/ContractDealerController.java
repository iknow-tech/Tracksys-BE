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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class ContractDealerController {

    private final ContractDealerAssignmentService assignmentService;

    @PostMapping("/assign-dealer")
    public ResponseEntity<AssignDealerResponse> assignDealer(@RequestBody AssignDealerRequest request) throws Exception {

        AssignDealerResponse response = assignmentService.assignDealerToContracts(request);
        return ResponseEntity.ok(response);
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
    @Transactional
    public ResponseEntity<Void> updateDealerContracts(@RequestBody DealerContractUpdateRequest request
    ) {
        try {
            log.info("updateDealerContracts called with {} updates",
                    request.getUpdates() == null ? 0 : request.getUpdates().size());
            if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
                return null;
            }
            assignmentService.updateDealerContracts(request);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("Error updating dealer contracts", e);
            throw new RuntimeException("Error updating dealer contracts", e);
        }
    }

    @GetMapping("/dealer-contract/{contractId}")
    public ResponseEntity<ContractDealerAssignment> getByContractId(@PathVariable String contractId) {
        ContractDealerAssignment contractDealerAssignment = assignmentService.findByContractId(contractId);
        return ResponseEntity.ok(contractDealerAssignment);
    }

    // Sipariş teslim edildi
    @PostMapping("/{contractId}/complete")
    public ResponseEntity<ContractDealerAssignment> deliveredContract(@PathVariable String contractId, @RequestParam(required = false) String completedBy
    ) {
        ContractDealerAssignment contractDealerAssignment = assignmentService.deliveredContract(contractId, completedBy);
        return ResponseEntity.ok(contractDealerAssignment);
    }

    @GetMapping("/all/contracts/detailed")
    public ResponseEntity<List<DealerContractInfo>> getAllDealerContractsDetailed() {
        try {
            List<DealerContractInfo> contracts = assignmentService.getAllDealerContractsWithDetails();
            log.info("Returning {} detailed contracts for all dealers", contracts.size());
            return ResponseEntity.ok(contracts);
        } catch (Exception e) {
            log.error("Error getting all dealer contracts: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }


}

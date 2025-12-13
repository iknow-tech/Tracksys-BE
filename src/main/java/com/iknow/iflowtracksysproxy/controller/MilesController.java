package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.integration.miles.model.response.ContractsToBeRegisteredResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.StockVehicleContractResponse;
import com.iknow.iflowtracksysproxy.service.MilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/miles")
@RequiredArgsConstructor
public class MilesController {

    private final MilesService milesService;

    /**
     * Get current session information
     */
    @GetMapping("/session")
    public ResponseEntity<MilesService.SessionInfo> getSessionInfo() {
        try {
            log.info("Getting Miles session information");
            MilesService.SessionInfo sessionInfo = milesService.getSessionInfo();
            return ResponseEntity.ok(sessionInfo);
        } catch (Exception e) {
            log.error("Error getting session info", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Customer Contracts (PRJ_SM_CustomerContract)
     */
    @GetMapping("/customer-contracts")
    public ResponseEntity<List<CustomerContractResponse>> getCustomerContracts() {
        try {
            log.info("Getting customer contracts");
            List<CustomerContractResponse> response = milesService.getCustomerContracts();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting customer contracts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Stock Vehicle Contracts (PRJ_SM_StockVehicleContract)
     */
    @GetMapping("/stock-vehicle-contracts")
    public ResponseEntity<List<StockVehicleContractResponse>> getStockVehicleContracts() {
        try {
            log.info("Getting stock vehicle contracts");
            List<StockVehicleContractResponse> response = milesService.getStockVehicleContracts();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting stock vehicle contracts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Contracts to be Registered (PRJ_SM_ContractsToBeRegistered)
     */
    @GetMapping("contracts-registered")
    public ResponseEntity<List<ContractsToBeRegisteredResponse>> getContractsRegistered() {
        try {
            log.info("Getting contracts to be registered");
            List<ContractsToBeRegisteredResponse> response = milesService.getContractsRegistered();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting contracts to be registered", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

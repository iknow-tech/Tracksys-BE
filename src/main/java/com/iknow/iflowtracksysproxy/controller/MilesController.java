package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.service.MilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Update Net Amount (GenericAttributeUpdateService_NetAmountUpdate)
     */
    @GetMapping("/update-net-amount")
    public ResponseEntity<NetAmountUpdateResponse> updateNetAmount(NetAmountUpdateRequest request) {
        try {
            log.info("Updating net amount for vehicleOrderItemId: {}", request.getVehicleOrderItemId());
            NetAmountUpdateResponse response = milesService.updateNetAmount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating net amount", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-tax")
    public ResponseEntity<TaxUpdateResponse> updateTax(@RequestBody TaxUpdateRequest request) {
        try {
            log.info("Updating tax");
            TaxUpdateResponse response = milesService.updateTax(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating tax", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-discount/{vehicleOrderItem}")
    public ResponseEntity<DiscountUpdateResponse> updateDiscount(@RequestBody DiscountUpdateRequest request,
            @PathVariable String vehicleOrderItem) {
        try {
            log.info("Updating Discount for vehicleOrderItemId: {}", vehicleOrderItem);
            DiscountUpdateResponse response = milesService.updateDiscount(request, vehicleOrderItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating Discount", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-chassis-number/{fleetVehicleId}")
    public ResponseEntity<ChassisNumberUpdateResponse> updateChassisNumber(
            @RequestBody ChassisNumberUpdateRequest request, @PathVariable String fleetVehicleId) {
        try {
            log.info("Updating chassisNumber for fleetvehicleId: {}", fleetVehicleId);
            ChassisNumberUpdateResponse response = milesService.updateChassisNumber(request, fleetVehicleId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating chassisNumber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-property-type/{fleetVehicleId}")
    public ResponseEntity<PropertyTypeUpdateResponse> updatePropertyTpe(
            @RequestBody PropertyTypeUpdateRequest request, @PathVariable String fleetVehicleId) {
        try {
            log.info("Updating propertyType for fleetvehicleId: {}", fleetVehicleId);
            PropertyTypeUpdateResponse response = milesService.updatePropertyType(request, fleetVehicleId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating chassisNumber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update-property/{fleetVehicleId}")
    public ResponseEntity<PropertyTypeUpdateResponse> updateProperty(@RequestBody PropertyTypeUpdateRequest request,
            @PathVariable String fleetVehicleId) {
        try {
            log.info("Updating propertyType for fleetvehicleId: {}", fleetVehicleId);
            PropertyTypeUpdateResponse response = milesService.updateProperty(request, fleetVehicleId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating chassisNumber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Sasi No - 1.6.7 FleetVehicle Üzerinde Şasi Numarası Alanının
     * Güncellenmesi
     */
    @PutMapping("/update-sasi-no")
    public ResponseEntity<SasiNoUpdateResponse> updateSasiNo(@RequestBody SasiNoUpdateRequest request) {
        try {
            log.info("Updating sasi no for fleetVehicleId: {}", request.getFleetVehicleId());
            SasiNoUpdateResponse response = milesService.updateSasiNo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating sasi no", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Mulk - 1.6.10 FleetVehicle Üzerinde Mülk Alanının Güncellenmesi
     */
    @PutMapping("/update-mulk")
    public ResponseEntity<MulkUpdateResponse> updateMulk(@RequestBody MulkUpdateRequest request) {
        try {
            log.info("Updating mulk for fleetVehicleId: {}", request.getFleetVehicleId());
            MulkUpdateResponse response = milesService.updateMulk(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating mulk", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Ruhsat Belge No - 1.6.11.2 Ruhsat Belge No Alanının Güncellenmesi
     */
    @PutMapping("/update-ruhsat-belge-no")
    public ResponseEntity<BaseResponse> updateRuhsatBelgeNo(@RequestBody RuhsatBelgeNoUpdateRequest request) {
        try {
            log.info("Updating ruhsat belge no for vehiclePropertyId: {}", request.getVehiclePropertyId());
            BaseResponse response = milesService.updateRuhsatBelgeNo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating ruhsat belge no", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/get-vehicle-inspection")
    public ResponseEntity<VehicleInspectionUpdateResponse> getVehicleInspection(
            VehicleInspectionUpdateRequest request) {
        try {
            log.info("Updating vehicle inspection");
            VehicleInspectionUpdateResponse response = milesService.getVehicleInspection(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error vehicle inspection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Vehicle Documents - 1.6.12.1 PRJ_SM_VehicleDocuments Servisi
     */
    @GetMapping("/get-vehicle-documents")
    public ResponseEntity<BaseResponse> getVehicleDocuments(VehicleDocumentsRequest request) {
        try {
            log.info("Getting vehicle documents for fleetvehicleId: {}", request.getFleetvehicleId());
            BaseResponse response = milesService.getVehicleDocuments(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting vehicle documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Vehicle Inspection Date - 1.6.12.2 Araç Muayene Geçerlilik Sonu
     * Alanının Güncellenmesi
     */
    @PutMapping("/update-vehicle-inspection-date")
    public ResponseEntity<BaseResponse> updateVehicleInspectionDate(
            @RequestBody VehicleInspectionDateUpdateRequest request) {
        try {
            log.info("Updating vehicle inspection date for vehiclePropertyId: {}", request.getVehiclePropertyId());
            BaseResponse response = milesService.updateVehicleInspectionDate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating vehicle inspection date", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Update HGS Etiket No - 1.6.13.2 HGS Etiket No Alanının Güncellenmesi
     */
    @PutMapping("/update-hgs-etiket-no")
    public ResponseEntity<BaseResponse> updateHgsEtiketNo(@RequestBody HgsEtiketNoUpdateRequest request) {
        try {
            log.info("Updating HGS etiket no for vehiclePropertyId: {}", request.getVehiclePropertyId());
            BaseResponse response = milesService.updateHgsEtiketNo(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating HGS etiket no", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update HGS Talep Tarihi - 1.6.13.3 HGS Etiket No Talep Tarihi Alanının Güncellenmesi
     */
    @PutMapping("/update-hgs-talep-tarihi")
    public ResponseEntity<BaseResponse> updateHgsTalepTarihi(@RequestBody HgsTalepTarihiUpdateRequest request) {
        try {
            log.info("Updating HGS talep tarihi for vehiclePropertyId: {}", request.getVehiclePropertyId());
            BaseResponse response = milesService.updateHgsTalepTarihi(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating HGS talep tarihi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

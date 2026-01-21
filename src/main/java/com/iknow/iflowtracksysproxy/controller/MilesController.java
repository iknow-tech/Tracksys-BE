package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.service.MilesContractSyncService;
import com.iknow.iflowtracksysproxy.service.MilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/miles")
@RequiredArgsConstructor
public class MilesController {

    private final MilesService milesService;
    private final CustomerContractCache customerContractCache;
    private final MilesContractSyncService milesContractSyncService;

    /**
     * Get current session information
     */
    @GetMapping("/session")
    public ResponseEntity<MilesService.SessionInfo> getSessionInfo() {
        try {
            System.out.print("Getting Miles session information");
            MilesService.SessionInfo sessionInfo = milesService.getSessionInfo();
            return ResponseEntity.ok(sessionInfo);
        } catch (Exception e) {
            System.out.print("Error getting session info");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Customer Contracts (PRJ_SM_CustomerContract)
     */
    @GetMapping("/customer-contracts")
    public ResponseEntity<List<CustomerContractResponse>> getCustomerContracts() {
        try {
            log.info("Getting customer contracts (merged)");
            return ResponseEntity.ok(milesService.getCustomerContracts());
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
     * Update HGS Talep Tarihi - 1.6.13.3 HGS Etiket No Talep Tarihi Alanının
     * Güncellenmesi
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

    /**
     * Update Plaka Avadanlık Talep Tarihi - 1.6.14.2 Plaka ve Avadanlık Sevki Talep
     * Edildi Tarihi Alanının Güncellenmesi
     */
    @PutMapping("/update-plaka-avadanlik-talep-tarihi")
    public ResponseEntity<BaseResponse> updatePlakaAvadanlikTalepTarihi(
            @RequestBody PlakaAvadanlikTalepTarihiUpdateRequest request) {
        try {
            log.info("Updating Plaka Avadanlik talep tarihi for vehiclePropertyId: {}", request.getVehiclePropertyId());
            BaseResponse response = milesService.updatePlakaAvadanlikTalepTarihi(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating Plaka Avadanlik talep tarihi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Plaka Avadanlık Alındı Tarihi - 1.6.14.3 Plaka ve Avadanlık Sevki
     * Alındı Tarihi Alanının Güncellenmesi
     */
    @PutMapping("/update-plaka-avadanlik-alindi-tarihi")
    public ResponseEntity<BaseResponse> updatePlakaAvadanlikAlindiTarihi(
            @RequestBody PlakaAvadanlikAlindiTarihiUpdateRequest request) {
        try {
            log.info("Updating Plaka Avadanlik alindi tarihi for vehiclePropertyId: {}",
                    request.getVehiclePropertyId());
            BaseResponse response = milesService.updatePlakaAvadanlikAlindiTarihi(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating Plaka Avadanlik alindi tarihi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Trafik Sigortası Talep Tarihi - 1.6.15.2 Trafik Sigortası Talep Tarihi
     * Alanının Güncellenmesi
     */
    @PutMapping("/update-trafik-sigortasi-talep-tarihi")
    public ResponseEntity<BaseResponse> updateTrafikSigortasiTalepTarihi(
            @RequestBody TrafikSigortasiTalepTarihiUpdateRequest request) {
        try {
            log.info("Updating Trafik Sigortasi talep tarihi for vehiclePropertyId: {}",
                    request.getVehiclePropertyId());
            BaseResponse response = milesService.updateTrafikSigortasiTalepTarihi(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating Trafik Sigortasi talep tarihi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }}

    @GetMapping("/traffic-insurance/{fleetVehicleId}")
    public ResponseEntity<TrafficInsuranceGetResponse> getTrafficInsurance(
            @ModelAttribute TrafficInsuranceGetRequest request) {
        try {
            log.info("Getting trafficInsurance for fleetVehicleId: {}",
                    request.getFleetVehicleId());
            TrafficInsuranceGetResponse response = milesService.getTrafficInsurance(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error Getting trafficInsurance", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/traffic-registration-number")
    public ResponseEntity<TrafficRegistrationNumberUpdateResponse> updatePlakaAvadanlikAlindiTarihi(
            @RequestBody TrafficRegistrationNumberUpdaterequest request) {
        try {
            log.info("Updating trafficRegistrationNumber vehiclePropertyId: {}",
                    request.getVehiclePropertyId());
            TrafficRegistrationNumberUpdateResponse response = milesService.updateTrafficRegistrationNumber(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating trafficRegistrationNumber", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update Sevk Bitiş Tarihi - 1.6.18.1 Vehicle Order Üzerinde Sevk Bitiş
     * Tarihinin Güncellenmesi
     */
    @PutMapping("/update-sevk-bitis-tarihi")
    public ResponseEntity<BaseResponse> updateSevkBitisTarihi(@RequestBody SevkBitisTarihiUpdateRequest request) {
        try {
            log.info("Updating Sevk Bitis tarihi for deliveryConditionId: {}", request.getDeliveryConditionId());
            BaseResponse response = milesService.updateSevkBitisTarihi(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating Sevk Bitis tarihi", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        }}

    @PutMapping("/delivery-dealer-area")
    public ResponseEntity<DeliveryDealerAreaUpdateResponse> updateDeliveryDealerArea(
            @RequestBody DeliveryDealerAreaUpdateRequest request) {
        try {
            log.info("Updating deliveryDealerArea contractId: {}",
                    request.getContractId());
            DeliveryDealerAreaUpdateResponse response = milesService.updateDeliveryDealerArea(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating DeliveryDealerArea", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/vehicle-order/credit-approval-date")
    public ResponseEntity<ApprovalDateUpdateBaseResponse> updateCreditApprovalDate(@RequestBody ApprovalDateUpdateRequest request) {
        try {
            log.info("Updating Credit Approval Date for vehicleOrderId: {}, date: {}", request.getOrderId(), request.getApprovalDate());

            if (request.getVehicleOrderItemId() == null || request.getApprovalDate() == null ) {
                return ResponseEntity.badRequest().build();
            }

            ApprovalDateUpdateBaseResponse response = milesService.updateCreditApprovalDate(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating credit approval date", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Dealer List (PRJ_SM_DealerList)
     */
    @GetMapping("/dealer")
    public ResponseEntity<List<GetDealerResponse>> getDealers() {
        try {
            log.info("Getting dealer list");
            List<GetDealerResponse> response = milesService.getDealerResponseList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting dealer list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Responsible Dealer List (PRJ_SM_ResponsibleDealer)
     */
    @GetMapping("/responsible-dealer")
    public ResponseEntity<List<ResponsibleDealerResponse>> getResponsibleDealers() {
        try {
            log.info("Getting responsible dealer list");
            List<ResponsibleDealerResponse> response = milesService.getResponsibleDealerList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting responsible dealer list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/trigger-mws-bulk-processor")
    public ResponseEntity<TriggerMWSBulkProcessorResponse> triggerMWSBulkProcessor(
            @RequestBody TriggerMWSBulkProcessorRequest request) {
        try {
            log.info("Triggering MWS Bulk Processor for guid: {}", request.getGuid());
            TriggerMWSBulkProcessorResponse response = milesService.triggerMWSBulkProcessor(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering MWS Bulk Processor", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get Leasing Company List (PRJ_SM_OwnerShip)
     */
    @GetMapping("/leasing")
    public ResponseEntity<List<GetLeasingResponse>> getLeasings() {
        try {
            log.info("Getting leasing company list");
            List<GetLeasingResponse> response = milesService.getLeasingResponseList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting leasing company list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/approve-contract")
    public ResponseEntity<TriggerMWSBulkProcessor_ApproveContractResponse> approveContract(
            @RequestBody TriggerMWSBulkProcessor_ApproveContractRequest request) {
        try {
            log.info("Triggering approveContract for contractId: {}", request.getContractId());
            TriggerMWSBulkProcessor_ApproveContractResponse response = milesService.triggerMWSBulkProcessor(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering approveContract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/ownership")
    public ResponseEntity<PRJ_SM_OwnerShipResponse> getOwnerShip() {
        try {
            log.info("Triggering getownership");
            PRJ_SM_OwnerShipResponse response = milesService.getOwnerShip();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering approveContract", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value="/save-license-certificate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SaveLicenseCertificateResponse> saveLicenseCertificate(@ModelAttribute SaveLicenseCertificateRequest request) {
        try {
            log.info("Triggering saveLicenseCertificate");
            SaveLicenseCertificateResponse response = milesService.saveLicenseCertificate(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error triggering saveLicenseCertificate", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

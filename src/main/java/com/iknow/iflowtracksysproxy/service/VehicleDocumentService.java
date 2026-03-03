package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.cache.CustomerContractCache;
import com.iknow.iflowtracksysproxy.dto.DealerContractInfo;
import com.iknow.iflowtracksysproxy.dto.MilesUpdatedDto;
import com.iknow.iflowtracksysproxy.dto.request.*;
import com.iknow.iflowtracksysproxy.dto.response.AssignDealerResponse;
import com.iknow.iflowtracksysproxy.entity.*;
import com.iknow.iflowtracksysproxy.integration.miles.MilesApi;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.VehicleOrderSupplierUpdateRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.VehicleInspectionUpdateResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.VehicleOrderSupplierUpdateBaseResponse;
import com.iknow.iflowtracksysproxy.respository.*;
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
public class VehicleDocumentService {

    private final VehicleDocumentRepository vehicleDocumentRepository;
    private final MilesUpdateService milesUpdateService;

    @Transactional
    public VehicleDocumentAssignment updateVehicleDocument(VehicleDocumentUpdateRequest request) {
        VehicleDocumentAssignment vehicleDocumentAssignment = new VehicleDocumentAssignment();
        if (request.getUpdates() == null || request.getUpdates().isEmpty()) {
            return null;
        }
        try {
            for (VehicleDocumentUpdateItemRequest item : request.getUpdates()) {
                MilesUpdatedDto milesUpdatedDto = new MilesUpdatedDto();
                milesUpdatedDto.setContractId(item.getContractId());
                vehicleDocumentAssignment = findByContractId(item.getContractId());

                if (vehicleDocumentAssignment == null) {
                    vehicleDocumentAssignment.setContractId(item.getContractId());
                }
                if (item.getLicenseSerialNumber() != null) {
                    vehicleDocumentAssignment.setLicenseSerialNumber(item.getLicenseSerialNumber());
                    milesUpdatedDto.setLicenseSerialNumber(item.getLicenseSerialNumber());
                }

                if (item.getExpirationDate() != null) {
                    vehicleDocumentAssignment.setExpirationDate(item.getExpirationDate());
                    milesUpdatedDto.setExpirationDate(item.getExpirationDate());
                }

                if (item.getHgsTagNo() != null) {
                    vehicleDocumentAssignment.setHgsTagNo(item.getHgsTagNo());
                    milesUpdatedDto.setHgsTagNo(item.getHgsTagNo());
                }

                if (item.getHgsRequestedDate() != null) {
                    vehicleDocumentAssignment.setHgsRequestedDate(item.getHgsRequestedDate());
                    milesUpdatedDto.setHgsRequestedDate(item.getHgsRequestedDate());

                }
                if (item.getLicensePlateEquipmentRequestDate() != null) {
                    vehicleDocumentAssignment.setLicensePlateEquipmentRequestDate(item.getLicensePlateEquipmentRequestDate());
                    milesUpdatedDto.setLicensePlateEquipmentRequestDate(item.getLicensePlateEquipmentRequestDate());
                }

                if (item.getLicensePlateEquipmentTransferDate() != null) {
                    vehicleDocumentAssignment.setLicensePlateEquipmentTransferDate(item.getLicensePlateEquipmentTransferDate());
                    milesUpdatedDto.setLicensePlateEquipmentTransferDate(item.getLicensePlateEquipmentTransferDate());
                }

                if (item.getTrafficInsuranceDate() != null) {
                    vehicleDocumentAssignment.setTrafficInsuranceDate(item.getTrafficInsuranceDate());
                    milesUpdatedDto.setTrafficInsuranceDate(item.getTrafficInsuranceDate());
                }

                milesUpdatedDto.setFleetVehicleId(item.getFleetVehicleId() != null ? item.getFleetVehicleId() : null);
                vehicleDocumentAssignment.setCreatedAt(LocalDateTime.now());
                milesUpdateService.update(milesUpdatedDto);
                vehicleDocumentRepository.save(vehicleDocumentAssignment);

            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Error updating dealer contracts", e);
        }

        return vehicleDocumentAssignment;
    }

    public VehicleDocumentAssignment findByContractId(String contractId) {
        VehicleDocumentAssignment vehicleDocumentAssignment = new VehicleDocumentAssignment();
        Optional<VehicleDocumentAssignment> optional = vehicleDocumentRepository.findByContractIdAndStatus(contractId, "ACTIVE");

        if (optional.isPresent()) {
            vehicleDocumentAssignment = optional.get();
        }
        return vehicleDocumentAssignment;
    }


}

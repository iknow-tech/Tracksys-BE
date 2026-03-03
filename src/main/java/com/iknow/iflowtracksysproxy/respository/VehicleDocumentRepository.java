package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.VehicleDocumentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleDocumentRepository extends JpaRepository<VehicleDocumentAssignment, Long> {

    Optional<VehicleDocumentAssignment> findByContractIdAndStatus(String contractId, String status);

}

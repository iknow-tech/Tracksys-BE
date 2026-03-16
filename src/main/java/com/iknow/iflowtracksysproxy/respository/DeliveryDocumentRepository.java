package com.iknow.iflowtracksysproxy.respository;

import com.iknow.iflowtracksysproxy.entity.DeliveryDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryDocumentRepository extends JpaRepository<DeliveryDocument, Long> {

    Optional<DeliveryDocument> findByContractId(String contractId);

    List<DeliveryDocument> findByContractIdIn(List<String> contractIds);



}

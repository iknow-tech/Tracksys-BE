package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.dto.response.OrderUpdateResult;
import com.iknow.iflowtracksysproxy.entity.ContractDealerAssignment;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import com.iknow.iflowtracksysproxy.respository.MilesUpdateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MilesUpdateService {

    private final MilesService milesService;

    private final ContractDealerAssignmentService contractDealerAssignmentService;

    private final MilesUpdateRepository milesUpdateRepository;

    @Transactional
    public OrderUpdateResult updateSupplierAndContact(String contractId) throws Exception {

        OrderUpdateResult orderUpdateResult = new OrderUpdateResult();

        CustomerContractResponse contractResponse= milesService.getCustomerContracts().stream().filter(contract -> contract.getId().equals(contractId)).findFirst() .orElse(null);
        ContractDealerAssignment contractDealerAssignment= contractDealerAssignmentService.findByContractId(contractId);

        Long ordersId   = Long.valueOf(contractResponse.getOrdersId());
        Long supplierId = Long.valueOf(contractDealerAssignment.getDealerBusinessPartnerId());
        Long contactId  = Long.valueOf((contractDealerAssignment.getContractId()));

        //  Miles update
        int updatedCount = milesUpdateRepository.updateSupplierAndContact(ordersId, supplierId, contactId);

        if (updatedCount != 1) {
            throw new Exception(
                    "Miles update başarısız. ordersId=" + ordersId
            );
        }
        orderUpdateResult.setOrdersId(ordersId);
        orderUpdateResult.setSupplierId(supplierId);
        orderUpdateResult.setContractId(contractId);
        orderUpdateResult.setContactId(contactId);
        orderUpdateResult.setSuccess(true);

        return orderUpdateResult;

    }




}

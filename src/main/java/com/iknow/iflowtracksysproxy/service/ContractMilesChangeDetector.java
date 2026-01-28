package com.iknow.iflowtracksysproxy.service;

import com.iknow.iflowtracksysproxy.entity.ContractChangeEvent;
import com.iknow.iflowtracksysproxy.entity.ContractFieldKey;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ContractMilesChangeDetector {

    public List<ContractChangeEvent> detectChanges(
            CustomerContractResponse oldC,
            CustomerContractResponse newC,
            String batchId
    ) {

        List<ContractChangeEvent> events = new ArrayList<>();
        String contractId = newC.getId();

        compare(events,
                oldC.getDeliveryLocation(),
                newC.getDeliveryLocation(),
                ContractFieldKey.DELIVERY_LOCATION,
                contractId,
                batchId);

        compare(events,
                oldC.getColor(),
                newC.getColor(),
                ContractFieldKey.COLOR,
                contractId,
                batchId);

        compare(events,
                oldC.getModelYear(),
                newC.getModelYear(),
                ContractFieldKey.MODEL_YEAR,
                contractId,
                batchId);

        compare(events,
                normalize(oldC.getOptions()),
                normalize(newC.getOptions()),
                ContractFieldKey.OPTIONS,
                contractId,
                batchId);

        compare(events,
                normalize(oldC.getUttsGpsInstallation()),
                normalize(newC.getUttsGpsInstallation()),
                ContractFieldKey.UTTS_GPS_INSTALLATION,
                contractId,
                batchId);

        compare(events,
                normalize(oldC.getTreasuryApprovalDate()),
                normalize(newC.getTreasuryApprovalDate()),
                ContractFieldKey.TREASURY_APPROVAL_DATE,
                contractId,
                batchId);

        compare(events,
                oldC.getDeliveryPerson(),
                newC.getDeliveryPerson(),
                ContractFieldKey.DELIVERY_PERSON,
                contractId,
                batchId);

        return events;
    }

    private void compare(
            List<ContractChangeEvent> events,
            String oldVal,
            String newVal,
            ContractFieldKey key,
            String contractId,
            String batchId
    ) {
        if (Objects.equals(oldVal, newVal)) {
            return;
        }

        events.add(
                ContractChangeEvent.builder()
                        .contractId(contractId)
                        .fieldKey(key)
                        .fieldLabel(key.getLabel())
                        .oldValue(oldVal)
                        .newValue(newVal)
                        .changedAt(LocalDateTime.now())
                        .seenByDealer(false)
                        .batchId(batchId)
                        .build()
        );
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}

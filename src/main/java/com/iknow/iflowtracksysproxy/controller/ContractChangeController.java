package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.entity.ContractChangeEvent;
import com.iknow.iflowtracksysproxy.respository.ContractChangeEventRepository;
import com.iknow.iflowtracksysproxy.service.ContractMilesChangeDetector;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dealer/contracts")
@RequiredArgsConstructor
public class ContractChangeController {

    private final ContractMilesChangeDetector contractMilesChangeDetector;
    private final ContractChangeEventRepository contractChangeEventRepository;

    @GetMapping("/changed-fields")
    @Transactional(readOnly = true)
    public Map<String, Set<String>> getChangedFields() {

        List<ContractChangeEvent> events =
                contractChangeEventRepository.findAll();

        return events.stream()
                .filter(e -> e.getContractId() != null && e.getFieldKey() != null)
                .collect(Collectors.groupingBy(
                        ContractChangeEvent::getContractId,
                        Collectors.mapping(
                                e -> e.getFieldKey().name(),
                                Collectors.toSet()
                        )
                ));
    }
}

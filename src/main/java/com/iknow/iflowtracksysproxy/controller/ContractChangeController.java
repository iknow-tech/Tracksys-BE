package com.iknow.iflowtracksysproxy.controller;

import com.iknow.iflowtracksysproxy.entity.ContractFieldKey;
import com.iknow.iflowtracksysproxy.respository.ContractChangeEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/dealer/contracts")
@RequiredArgsConstructor
public class ContractChangeController {

    private final ContractChangeEventRepository contractChangeEventRepository;


    @GetMapping("/changed-fields")
    @Transactional(readOnly = true)
    public Map<String, Set<String>> getChangedFields() {
        return Map.of();
    }

//    public Map<String, Set<String>> getChangedFields() {
//
//        return contractChangeEventRepository.findDistinctUnseenContractFieldKeys().stream()
//                .filter(row -> row.length >= 2 && row[0] != null && row[1] != null)
//                .collect(Collectors.groupingBy(
//                        row -> (String) row[0],
//                        Collectors.mapping(
//                                row -> ((ContractFieldKey) row[1]).name(),
//                                Collectors.toSet()
//                        )
//                ));
//    }
}

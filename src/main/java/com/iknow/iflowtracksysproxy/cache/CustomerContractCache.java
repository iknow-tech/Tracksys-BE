package com.iknow.iflowtracksysproxy.cache;

import com.iknow.iflowtracksysproxy.integration.miles.model.response.CustomerContractResponse;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class CustomerContractCache {

    private final AtomicReference<List<CustomerContractResponse>> cache =
            new AtomicReference<>(Collections.emptyList());

    @Getter
    private volatile LocalDateTime lastUpdatedAt;

    public void update(List<CustomerContractResponse> contracts) {
        cache.set(contracts);
        lastUpdatedAt = LocalDateTime.now();
    }

    public List<CustomerContractResponse> get() {
        return cache.get();
    }

    public boolean isEmpty() {
        return cache.get().isEmpty();
    }

    public List<CustomerContractResponse> snapshot() {
        List<CustomerContractResponse> data=  cache.get();
        return data == null ? new ArrayList<>() : new ArrayList<>(data);
    }

    public void clear() {
        cache.set(Collections.emptyList());
        lastUpdatedAt = null;
    }
}

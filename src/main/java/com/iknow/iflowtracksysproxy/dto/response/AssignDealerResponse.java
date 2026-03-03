package com.iknow.iflowtracksysproxy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignDealerResponse {
    private boolean success;
    private int assignedCount;
    private int failedCount;
    private List<Boolean> isMilesUpdateSuccess;
    private List<String> failedContractIds;

    private List<AssignedContractInfo> assignedContracts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignedContractInfo {
        private String contractId;
        private String customer;
        private String make;
        private String model;
        private String version;
        private String color;
        private String dealerName;
        private LocalDateTime assignedDate;
        private String status;
        private boolean wasReassigned;
        private String previousDealer;
        private Boolean updatedSupplierContact;

    }
}

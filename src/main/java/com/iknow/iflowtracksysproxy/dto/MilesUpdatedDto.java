package com.iknow.iflowtracksysproxy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MilesUpdatedDto {
    private String contractId;
    private String netPrice;
    private String otv;
    private String chassisNumber;
    private String motorNumber;
    private String ettn;
    private LocalDate shipmentStartDate;
    private LocalDate shipmentEndDate;
    private String delivery;
}

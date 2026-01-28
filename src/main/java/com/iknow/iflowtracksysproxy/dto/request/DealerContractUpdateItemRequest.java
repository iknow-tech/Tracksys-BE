package com.iknow.iflowtracksysproxy.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class DealerContractUpdateItemRequest{
    private String contractId;
    private BigDecimal netPrice;
    private BigDecimal otv;
    private String chassisNumber;
    private String motorNumber;
    private String delivery;
    private String shipmentBeginDate;
    private String shipmentEndDate;
    private String ettn;
    private String deliveryDate;

}
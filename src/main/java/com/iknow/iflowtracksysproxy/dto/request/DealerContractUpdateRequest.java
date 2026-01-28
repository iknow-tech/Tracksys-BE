package com.iknow.iflowtracksysproxy.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
public class DealerContractUpdateRequest {

    private List<DealerContractUpdateItemRequest> updates;
}



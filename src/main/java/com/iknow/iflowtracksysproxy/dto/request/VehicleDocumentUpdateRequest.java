package com.iknow.iflowtracksysproxy.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class VehicleDocumentUpdateRequest {

    private List<VehicleDocumentUpdateItemRequest> updates;
}



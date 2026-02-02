package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VehicleOrderSupplierUpdateBaseResponse {
    @JsonProperty("data")
    private DataResponse data;

    @JsonProperty("metadata")
    private BaseResponse.MetadataResponse metadata;

    @Data
    public static class ApprovalDateData {
        @JsonProperty("")
        private MWSBulkAttributeUpdate bulkAttributeUpdate;

        @Data
        public static class MWSBulkAttributeUpdate {
            @JsonProperty("MWSObject")
            private ApprovalDateUpdateResponse mwsObject;
        }
    }
}



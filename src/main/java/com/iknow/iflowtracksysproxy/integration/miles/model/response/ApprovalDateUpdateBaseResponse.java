package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApprovalDateUpdateBaseResponse {
    @JsonProperty("data")
    private ApprovalDateData data;

    @JsonProperty("metadata")
    private BaseResponse.MetadataResponse metadata;

    @Data
    public static class ApprovalDateData {
        @JsonProperty("MWSBulkAttributeUpdate")
        private MWSBulkAttributeUpdate bulkAttributeUpdate;

        @Data
        public static class MWSBulkAttributeUpdate {
            @JsonProperty("MWSObject")
            private ApprovalDateUpdateResponse mwsObject;
        }
    }
}



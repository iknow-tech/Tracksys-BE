package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class ApprovalDateUpdateResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("sroid")
    private String sroId;

    @JsonProperty("Field_Set")
    private FieldSet fieldSet;

    @Data
    public static class FieldSet {
        @JsonProperty("Field")
        private Field field;
    }

    @Data
    public static class Field {

        @JsonProperty("id")
        private String id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("MWSDateValue")
        private MWSDateValue mwsDateValue;
    }

    @Data
    public static class MWSDateValue {

        @JsonProperty("value")
        private String value;
    }


}


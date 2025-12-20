package com.iknow.iflowtracksysproxy.integration.miles.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleDocumentsResponse {

    @JacksonXmlProperty(localName = "licenseddocumentnumber")
    private String licenseddocumentnumber;

    @JacksonXmlProperty(localName = "vehicleinspection")
    private String vehicleinspection;

    @JacksonXmlProperty(localName = "mvtplato")
    private String mvtplato;

    @JacksonXmlProperty(localName = "licensemainrequestdate")
    private String licensemainrequestdate;

    @JacksonXmlProperty(localName = "trafficinsurance")
    private String trafficinsurance;
    @JacksonXmlProperty(localName = "hgstagno")
    private String hgstagno;

    @JacksonXmlProperty(localName = "licenseplateandequipmentrequestdate")
    private String licenseplateandequipmentrequestdate;
}

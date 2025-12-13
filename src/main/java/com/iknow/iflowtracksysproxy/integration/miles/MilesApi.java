package com.iknow.iflowtracksysproxy.integration.miles;

import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.util.ResourceReader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Slf4j
@Service
public class MilesApi {

    private static final String PRJ_SM_CustomerContractRequest = ResourceReader.asString("xml/PRJ_SM_CustomerContract.xml");
    private static final String PRJ_SM_StockVehicleContractRequest = ResourceReader.asString("xml/PRJ_SM_StockVehicleContract.xml");
    private static final String PRJ_SM_ContractsToBeRegisteredRequest = ResourceReader.asString("xml/PRJ_SM_ContractsToBeRegistered.xml");


    private final RestTemplate xmlRestTemplate;

    @Value("${miles.baseUrl}")
    private String baseUrl;
    @Value("${miles.username}")
    private String username;
    @Value("${miles.password}")
    private String password;
    public static String sessionId = null;


    public MilesApi(RestTemplate xmlRestTemplate) {
        this.xmlRestTemplate = xmlRestTemplate;
    }

    @PostConstruct
    public void init() {
        refreshSessionId();
    }

    @Scheduled(cron = "0 0 19 * * ?")
    public void refreshSessionId() {
        sessionId = logon().getSessionId();
    }

    private LogonResponse logon() {
        log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/Logon?identification={}&password={}", baseUrl, username, password);
        return xmlRestTemplate.getForEntity(baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/Session/Logon?identification=" + username + "&password=" + password, BaseResponse.class)
                .getBody()
                .getData()
                .getLoginResponse();
    }

    public List<CustomerContractResponse> getCustomerContracts() {
        log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}", sessionId);
        String body = PRJ_SM_CustomerContractRequest
                .replace("{sessionId}", sessionId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            List<CustomerContractResponse> baseResponseResponseEntity =  xmlRestTemplate.postForEntity(baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch", request, BaseResponse.class)
                    .getBody()
                    .getData()
                    .getCustomerContracts();
            return baseResponseResponseEntity;
            //log.info("response: {}", baseResponseResponseEntity);
        } catch (Exception e) {
            log.error("MilesApi.getCustomerContracts", e.getStackTrace());
            return null;
        }
    }

    public List<StockVehicleContractResponse> getStockVehicleContracts() {
        log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}", sessionId);
        String body = PRJ_SM_StockVehicleContractRequest
                .replace("{sessionId}", sessionId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            List<StockVehicleContractResponse> baseResponseResponseEntity =  xmlRestTemplate.postForEntity(baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch", request, BaseResponse.class)
                    .getBody()
                    .getData()
                    .getStockVehicleContracts();
            return baseResponseResponseEntity;
         } catch (Exception e) {
            log.error("MilesApi.getStockVehicleContracts", e.getStackTrace());
            return null;
        }
    }

    public List<ContractsToBeRegisteredResponse> getContractsRegistered() {
        log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}", sessionId);
        String body = PRJ_SM_ContractsToBeRegisteredRequest
                .replace("{sessionId}", sessionId);
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            HttpEntity<String> request = new HttpEntity<>(body, headers);
            List<ContractsToBeRegisteredResponse> baseResponseResponseEntity =  xmlRestTemplate.postForEntity(baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch", request, BaseResponse.class)
                    .getBody()
                    .getData()
                    .getContractsToBeRegistered();
            return baseResponseResponseEntity;
        } catch (Exception e) {
            log.error("MilesApi.getContractsRegistered", e.getStackTrace());
            return null;
        }
    }


}

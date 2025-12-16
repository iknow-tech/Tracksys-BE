package com.iknow.iflowtracksysproxy.integration.miles;

import com.iknow.iflowtracksysproxy.integration.miles.model.request.DiscountUpdateRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.NetAmountUpdateRequest;
import com.iknow.iflowtracksysproxy.integration.miles.model.request.TaxUpdateRequest;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class MilesApi {

        private static final String PRJ_SM_CustomerContractRequest = ResourceReader
                        .asString("xml/PRJ_SM_CustomerContract.xml");
        private static final String PRJ_SM_StockVehicleContractRequest = ResourceReader
                        .asString("xml/PRJ_SM_StockVehicleContract.xml");
        private static final String PRJ_SM_ContractsToBeRegisteredRequest = ResourceReader
                        .asString("xml/PRJ_SM_ContractsToBeRegistered.xml");
        private static final String GenericAttributeUpdateService_NetAmountUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_NetAmountUpdate.xml");
        private static final String GenericAttributeUpdateService_TaxUpdateRequest = ResourceReader
                    .asString("xml/GenericAttributeUpdateService_TaxUpdate.xml");

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
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/Logon?identification={}&password={}",
                                baseUrl, username, password);
                return xmlRestTemplate.getForEntity(baseUrl
                                + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/Session/Logon?identification="
                                + username + "&password=" + password, BaseResponse.class)
                                .getBody()
                                .getData()
                                .getLoginResponse();
        }

        public List<CustomerContractResponse> getCustomerContracts() {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}",
                                sessionId);
                String body = PRJ_SM_CustomerContractRequest
                                .replace("{sessionId}", sessionId);
                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);
                        HttpEntity<String> request = new HttpEntity<>(body, headers);
                        List<CustomerContractResponse> baseResponseResponseEntity = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        request, BaseResponse.class)
                                        .getBody()
                                        .getData()
                                        .getCustomerContracts();
                        return baseResponseResponseEntity;
                } catch (Exception e) {
                        log.error("MilesApi.getCustomerContracts", e.getStackTrace());
                        return null;
                }
        }

        public List<StockVehicleContractResponse> getStockVehicleContracts() {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}",
                                sessionId);
                String body = PRJ_SM_StockVehicleContractRequest
                                .replace("{sessionId}", sessionId);
                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);
                        HttpEntity<String> request = new HttpEntity<>(body, headers);
                        List<StockVehicleContractResponse> baseResponseResponseEntity = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        request, BaseResponse.class)
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
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}",
                                sessionId);
                String body = PRJ_SM_ContractsToBeRegisteredRequest
                                .replace("{sessionId}", sessionId);
                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);
                        HttpEntity<String> request = new HttpEntity<>(body, headers);
                        List<ContractsToBeRegisteredResponse> baseResponseResponseEntity = xmlRestTemplate
                                        .postForEntity(baseUrl
                                                        + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                                        request, BaseResponse.class)
                                        .getBody()
                                        .getData()
                                        .getContractsToBeRegistered();
                        return baseResponseResponseEntity;
                } catch (Exception e) {
                        log.error("MilesApi.getContractsRegistered", e.getStackTrace());
                        return null;
                }
        }

        public NetAmountUpdateResponse updateNetAmount(NetAmountUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);
                String body = GenericAttributeUpdateService_NetAmountUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehicleOrderItemId}", request.getVehicleOrderItemId())
                                .replace("{refAmount}", request.getRefAmount())
                                .replace("{curAmount}", request.getCurAmount());
                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);
                        HttpEntity<String> request1 = new HttpEntity<>(body, headers);
                        NetAmountUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        request1, NetAmountUpdateResponse.class)
                                        .getBody();
                        return response;
                } catch (Exception e) {
                        log.error("MilesApi.updateNetAmount", e.getStackTrace());
                        return null;
                }
        }

    public TaxUpdateResponse updateTax(TaxUpdateRequest request) {
        // Log bilgisi
        log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                baseUrl, sessionId);

        // XML body template'ini request objesine göre oluştur
        String body = GenericAttributeUpdateService_TaxUpdateRequest
                .replace("{sessionId}", sessionId)
                .replace("{vehicleOrderItemId}", request.getVehicleOrderItemId())
                .replace("{orderId}", request.getOrderId())
                .replace("{id}", request.getFieldId())
                .replace("{refAmount}", request.getRefAmount())
                .replace("{curAmount}", request.getCurAmount())
                .replace("{currencyId}", request.getCurrencyId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            TaxUpdateResponse response = xmlRestTemplate.postForEntity(
                    baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                    httpEntity,
                    TaxUpdateResponse.class
            ).getBody();

            return response;

        } catch (Exception e) {
            log.error("MilesApi.updateTax error: ", e);
            return null;
        }
    }

    public DiscountUpdateResponse updateDiscount(DiscountUpdateRequest request, String vehicleOrderItem) {
        log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                baseUrl, sessionId);

        // XML body template'ini request objesine göre oluştur
        String body = GenericAttributeUpdateService_TaxUpdateRequest
                .replace("{sessionId}", sessionId)
                .replace("{vehicleOrderItemId}", vehicleOrderItem)
                .replace("{orderId}", request.getOrderId())
                .replace("{id}", request.getFieldId())
                .replace("{refAmount}", request.getRefAmount())
                .replace("{curAmount}", request.getCurAmount())
                .replace("{currencyId}", request.getCurrencyId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            DiscountUpdateResponse response = xmlRestTemplate.postForEntity(
                    baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                    httpEntity,
                    DiscountUpdateResponse.class
            ).getBody();

            return response;

        } catch (Exception e) {
            log.error("MilesApi.updateTax error: ", e);
            return null;
        }
    }

}

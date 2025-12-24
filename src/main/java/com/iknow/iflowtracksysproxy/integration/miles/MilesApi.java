package com.iknow.iflowtracksysproxy.integration.miles;

import com.iknow.iflowtracksysproxy.integration.miles.model.request.*;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.*;
import com.iknow.iflowtracksysproxy.util.ResourceReader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
        private static final String GenericAttributeUpdateService_ChassisNumberUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_ChassisNumberUpdate.xml");
        private static final String GenericAttributeUpdateService_PropertyTypeRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_PropertyType.xml");
        private static final String GenericAttributeUpdateService_SasiNoUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_SasiNoUpdate.xml");
        private static final String GenericAttributeUpdateService_MulkUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_MulkUpdate.xml");
        private static final String PRJ_SM_VehicleDocumentsRequest = ResourceReader
                        .asString("xml/PRJ_SM_VehicleDocuments.xml");
        private static final String GenericAttributeUpdateService_RuhsatBelgeNoUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_RuhsatBelgeNoUpdate.xml");
        private static final String GenericAttributeUpdateService_CreditApprovalDateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_CreditApprovalDate.xml");
        private static final String PRJ_SM_DealerListRequest = ResourceReader
                        .asString("xml/PRJ_SM_DealerList.xml");
        private static final String PRJ_SM_ResponsibleDealerRequest = ResourceReader
                        .asString("xml/PRJ_SM_ResponsibleDealerList.xml");
        private static final String GenericAttributeUpdateService_VehicleInspectionUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_VehicleInspectionUpdate.xml");
        private static final String GenericAttributeUpdateService_HgsEtiketNoUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_HgsEtiketNoUpdate.xml");
        private static final String GenericAttributeUpdateService_HgsTalepTarihiUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_HgsTalepTarihiUpdate.xml");
        private static final String GenericAttributeUpdateService_PlakaAvadanlikTalepTarihiUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_PlakaAvadanlikTalepTarihiUpdate.xml");
        private static final String GenericAttributeUpdateService_PlakaAvadanlikAlindiTarihiUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_PlakaAvadanlikAlindiTarihiUpdate.xml");
        private static final String GenericAttributeUpdateService_TrafikSigortasiTalepTarihiUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_TrafikSigortasiTalepTarihiUpdate.xml");
        private static final String GenericAttributeUpdateService_SevkBitisTarihiUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_SevkBitisTarihiUpdate.xml");
        private static final String PRJ_SM_VehicleDocuments_GetTrafficInsuranceRequest = ResourceReader
                        .asString("xml/PRJ_SM_VehicleDocuments_GetTrafficInsurance.xml");
        private static final String GenericAttributeUpdateService_TrafficRegistrationNumberUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_TrafficRegistrationNumberUpdate.xml");
        private static final String GenericAttributeUpdateService_DeliveryDealerAreaUpdateRequest = ResourceReader
                        .asString("xml/GenericAttributeUpdateService_DeliveryDealerAreaUpdate.xml");
        private static final String MWS_TriggerMWSBulkProcessorRequest = ResourceReader
                        .asString("xml/MWS_TriggerMWSBulkProcessor.xml");
        private static final String MWS_TriggerMWSBulkProcessor_ApproveContractRequest = ResourceReader
                .asString("xml/TriggerMWSBulkProcessor_ApproveContract.xml");


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
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{refAmount}", request.getRefAmount())
                                .replace("{curAmount}", request.getCurAmount())
                                .replace("{currencyId}", request.getCurrencyId());
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
                                        TaxUpdateResponse.class).getBody();

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
                                        DiscountUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTax error: ", e);
                        return null;
                }
        }

        public ChassisNumberUpdateResponse updateChassisNumber(ChassisNumberUpdateRequest request,
                        String fleetvehicleId) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                // XML body template'ini request objesine göre oluştur
                String body = GenericAttributeUpdateService_ChassisNumberUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetVehicleId}", fleetvehicleId)
                                .replace("{orderId}", request.getOrderId())
                                .replace("{id}", request.getFieldId())
                                .replace("{value}", request.getValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        ChassisNumberUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        ChassisNumberUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTax error: ", e);
                        return null;
                }
        }

        public PropertyTypeUpdateResponse updatePropertyType(PropertyTypeUpdateRequest request, String fleetvehicleId) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                // XML body template'ini request objesine göre oluştur
                String body = GenericAttributeUpdateService_PropertyTypeRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetVehicleId}", fleetvehicleId)
                                .replace("{orderId}", request.getOrderId())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{id}", request.getFieldId())
                                .replace("{value}", request.getValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        PropertyTypeUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        PropertyTypeUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTax error: ", e);
                        return null;
                }
        }

        public SasiNoUpdateResponse updateSasiNo(SasiNoUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_SasiNoUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetVehicleId}", request.getFleetVehicleId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{sasiNo}", request.getSasiNo());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        SasiNoUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        SasiNoUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateSasiNo error: ", e);
                        return null;
                }
        }

        public PropertyTypeUpdateResponse updateProperty(PropertyTypeUpdateRequest request, String fleetvehicleId) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                // XML body template'ini request objesine göre oluştur
                String body = GenericAttributeUpdateService_PropertyTypeRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetVehicleId}", fleetvehicleId)
                                .replace("{orderId}", request.getOrderId())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{id}", request.getFieldId())
                                .replace("{value}", request.getValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        PropertyTypeUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        PropertyTypeUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTax error: ", e);
                        return null;
                }
        }

        public VehicleInspectionUpdateResponse getVehicleInspection(VehicleInspectionUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                // XML body template'ini request objesine göre oluştur
                String body = PRJ_SM_VehicleDocumentsRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{ordersId}", request.getOrdersId());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        VehicleInspectionUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        httpEntity,
                                        VehicleInspectionUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTax error: ", e);
                        return null;
                }
        }

        public MulkUpdateResponse updateMulk(MulkUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_MulkUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetVehicleId}", request.getFleetVehicleId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{value}", request.getValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        MulkUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        MulkUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateMulk error: ", e);
                        return null;
                }
        }

        public BaseResponse updateRuhsatBelgeNo(RuhsatBelgeNoUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_RuhsatBelgeNoUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{ruhsatBelgeNo}", request.getRuhsatBelgeNo());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateRuhsatBelgeNo error: ", e);
                        return null;
                }
        }

        public BaseResponse getVehicleDocuments(VehicleDocumentsRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}",
                                baseUrl, sessionId);

                String body = PRJ_SM_VehicleDocumentsRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetvehicleId}", request.getFleetvehicleId());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.getVehicleDocuments error: ", e);
                        return null;
                }
        }

        public BaseResponse updateVehicleInspectionDate(VehicleInspectionDateUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_VehicleInspectionUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTimeValue}", request.getDateTimeValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateVehicleInspectionDate error: ", e);
                        return null;
                }
        }

        public BaseResponse updateHgsEtiketNo(HgsEtiketNoUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_HgsEtiketNoUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{hgsEtiketNo}", request.getHgsEtiketNo());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateHgsEtiketNo error: ", e);
                        return null;
                }
        }

        public BaseResponse updateHgsTalepTarihi(HgsTalepTarihiUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_HgsTalepTarihiUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTimeValue}", request.getDateTimeValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateHgsTalepTarihi error: ", e);
                        return null;
                }
        }

        public BaseResponse updatePlakaAvadanlikTalepTarihi(PlakaAvadanlikTalepTarihiUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_PlakaAvadanlikTalepTarihiUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTimeValue}", request.getDateTimeValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updatePlakaAvadanlikTalepTarihi error: ", e);
                        return null;
                }
        }

        public BaseResponse updatePlakaAvadanlikAlindiTarihi(PlakaAvadanlikAlindiTarihiUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_PlakaAvadanlikAlindiTarihiUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTimeValue}", request.getDateTimeValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updatePlakaAvadanlikAlindiTarihi error: ", e);
                        return null;
                }
        }

        public BaseResponse updateTrafikSigortasiTalepTarihi(TrafikSigortasiTalepTarihiUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_TrafikSigortasiTalepTarihiUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTimeValue}", request.getDateTimeValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTrafikSigortasiTalepTarihi error: ", e);
                        return null;
                }
        }

        public BaseResponse updateSevkBitisTarihi(SevkBitisTarihiUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService?sessionId={}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_SevkBitisTarihiUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{deliveryConditionId}", request.getDeliveryConditionId())
                                .replace("{sroid}", request.getSroid())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTimeValue}", request.getDateTimeValue());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        BaseResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        BaseResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateSevkBitisTarihi error: ", e);
                        return null;
                }
        }

        public TrafficInsuranceGetResponse getTrafficInsurance(TrafficInsuranceGetRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch{}",
                                baseUrl, sessionId);

                String body = PRJ_SM_VehicleDocuments_GetTrafficInsuranceRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{fleetVehicleId}", request.getFleetVehicleId());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        TrafficInsuranceGetResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        httpEntity,
                                        TrafficInsuranceGetResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updatePlakaAvadanlikAlindiTarihi error: ", e);
                        return null;
                }
        }

        public TrafficRegistrationNumberUpdateResponse updateTrafficRegistrationNumber(
                        TrafficRegistrationNumberUpdaterequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService/{}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_TrafficRegistrationNumberUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehiclePropertyId}", request.getVehiclePropertyId())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{dateTime}", request.getDateTime())
                                .replace("{orderId}", request.getOrderId());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        TrafficRegistrationNumberUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        TrafficRegistrationNumberUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTrafficRegistrationNumber error: ", e);
                        return null;
                }
        }

        public DeliveryDealerAreaUpdateResponse updateDeliveryDealerArea(DeliveryDealerAreaUpdateRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService/{}",
                                baseUrl, sessionId);

                String body = GenericAttributeUpdateService_DeliveryDealerAreaUpdateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{contractId}", request.getContractId())
                                .replace("{fieldId}", request.getFieldId())
                                .replace("{value}", request.getValue())
                                .replace("{orderId}", request.getOrderId());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        DeliveryDealerAreaUpdateResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        DeliveryDealerAreaUpdateResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.updateTrafficRegistrationNumber error: ", e);
                        return null;
                }
        }

        public ApprovalDateUpdateBaseResponse updateCreditApprovalDate(ApprovalDateUpdateRequest request) {
                log.info("Updating Credit Approval Date for vehicleOrderId: {}, date: {}", request.getOrderId(),
                                request.getApprovalDate());

                // XML body template'ini request objesine göre oluştur
                String body = GenericAttributeUpdateService_CreditApprovalDateRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{vehicleOrderItemId}", request.getVehicleOrderItemId())
                                .replace("{orderId}", request.getOrderId() != null ? request.getOrderId() : "205")
                                .replace("{id}", request.getFieldId() != null ? request.getFieldId() : "1000062")
                                .replace("{approvalDate}", request.getApprovalDate().toString());

                try {

                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        return xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService",
                                        httpEntity,
                                        ApprovalDateUpdateBaseResponse.class).getBody();

                } catch (Exception e) {
                        log.error("MilesApi.updateTax error: ", e);
                        return null;
                }
        }

        public List<GetDealerResponse> getDealerList() {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}",
                                sessionId);
                String body = PRJ_SM_DealerListRequest
                                .replace("{sessionId}", sessionId);
                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);
                        HttpEntity<String> request = new HttpEntity<>(body, headers);
                        List<GetDealerResponse> dealerResponseList = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        request, BaseResponse.class)
                                        .getBody()
                                        .getData()
                                        .getDealerList();
                        return dealerResponseList;
                } catch (Exception e) {
                        log.error("MilesApi.getStockVehicleContracts", e.getStackTrace());
                        return null;
                }
        }

        public List<ResponsibleDealerResponse> getResponsibleDealerList() {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch?sessionId={}",
                                sessionId);
                String body = PRJ_SM_ResponsibleDealerRequest
                                .replace("{sessionId}", sessionId);
                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);
                        HttpEntity<String> request = new HttpEntity<>(body, headers);
                        List<ResponsibleDealerResponse> dealerResponseList = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/NativeSearch",
                                        request, BaseResponse.class)
                                        .getBody()
                                        .getData()
                                        .getResponsibleDealerList();
                        return dealerResponseList;
                } catch (Exception e) {
                        log.error("MilesApi.getStockVehicleContracts", e.getStackTrace());
                        return null;
                }
        }

        public TriggerMWSBulkProcessorResponse triggerMWSBulkProcessor(TriggerMWSBulkProcessorRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/GenericAttributeUpdateService/{}",
                                baseUrl, sessionId);

                String body = MWS_TriggerMWSBulkProcessorRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{guid}", request.getGuid());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        TriggerMWSBulkProcessorResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/TriggerMWSBulkProcessor",
                                        httpEntity,
                                        TriggerMWSBulkProcessorResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.triggerMWSBulkProcessor error: ", e);
                        return null;
                }
        }

        public TriggerMWSBulkProcessor_ApproveContractResponse approveContract(TriggerMWSBulkProcessor_ApproveContractRequest request) {
                log.info("{}/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/TriggerMWSBulkProcessor/{}",
                                baseUrl, sessionId);

                String body = MWS_TriggerMWSBulkProcessor_ApproveContractRequest
                                .replace("{sessionId}", sessionId)
                                .replace("{contractId}", request.getContractId())
                                .replace("{deliveryDate}", request.getDeliveryDate())
                                .replace("{deliveryMileage}", request.getDeliveryMileage())
                                .replace("{receiptByContact}", request.getReceiptByContact())
                                .replace("{isDriver}", request.getIsDriver())
                                .replace("{deliveryLocation}", request.getDeliveryLocation());

                try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_XML);

                        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

                        TriggerMWSBulkProcessor_ApproveContractResponse response = xmlRestTemplate.postForEntity(
                                        baseUrl + "/miles/servlet/be.sofico.basecamp.servlet.tools.CommandServlet/MWS/TriggerMWSBulkProcessor",
                                        httpEntity,
                                TriggerMWSBulkProcessor_ApproveContractResponse.class).getBody();

                        return response;

                } catch (Exception e) {
                        log.error("MilesApi.triggerMWSBulkProcessor_ApproveContract error: ", e);
                        return null;
                }
        }

}

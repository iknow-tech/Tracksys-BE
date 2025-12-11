package com.iknow.iflowtracksysproxy.integration.miles;

import com.iknow.iflowtracksysproxy.integration.miles.model.response.BaseResponse;
import com.iknow.iflowtracksysproxy.integration.miles.model.response.LogonResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
public class MilesApi  {

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



}

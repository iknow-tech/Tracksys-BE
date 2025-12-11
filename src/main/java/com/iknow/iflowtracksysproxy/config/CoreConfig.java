package com.iknow.iflowtracksysproxy.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;

import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;


@Configuration
public class CoreConfig {

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    public RestTemplate xmlRestTemplate(XmlMapper xmlMapper) {

        RestTemplate rt = new RestTemplate();

        StringHttpMessageConverter stringConv =
                new StringHttpMessageConverter(StandardCharsets.UTF_8);

        Jaxb2RootElementHttpMessageConverter jaxbConv =
                new Jaxb2RootElementHttpMessageConverter();

        rt.getMessageConverters().add(0, stringConv);
        rt.getMessageConverters().add(1, jaxbConv);

        return rt;

    }
}


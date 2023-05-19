package br.com.certacon.certabotorganizefiles.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PostRestTemplateNFeService {

    private RestTemplate restTemplate;

    @Value("${config.restBuilderNFe}")
    private String restBuilder;

    public PostRestTemplateNFeService() {
        this.restTemplate = new RestTemplateBuilder().rootUri(restBuilder).build();
    }


}

package com.example.demo.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("naver")
public class NaverLoginController {

    private HttpEntity<MultiValueMap<String, String>> getCodeHttpEntity() {
        System.out.println("get code ");
        String response_type = "code";

        String state = "STATE_STRING";
        String redirect_uri = "http://localhost:8080/naver/auth";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        extracted(response_type, client_id, state, redirect_uri, params);
        return new HttpEntity<>(params, headers);
    }

    private ResponseEntity<String> requestAccessToken(HttpEntity request) {
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                request,
                String.class
        );
    }

    private static void extracted(String response_type, String client_id, String state, String redirect_uri, MultiValueMap<String, String> params) {
        params.add("response_type", response_type);
        params.add("client_id", client_id);
        params.add("state", state);
        params.add("redirect_uri", redirect_uri);
    }

    @GetMapping("auth")
    public ResponseEntity<String> naverAuth(@RequestParam String code) throws JsonProcessingException {


        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", client_id);
        params.add("client_secret", client_secret);
        params.add("code", code);

        HttpEntity http = new HttpEntity(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> exchange = restTemplate.exchange("https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                http,
                String.class
        );
        String body = exchange.getBody();
        System.out.println(body);

        ObjectMapper objectMapper = new ObjectMapper();
        NaverEntity naverEntity = objectMapper.readValue(body, NaverEntity.class);
        System.out.println(naverEntity.getAccess_token());

        HttpEntity<MultiValueMap<String, String>> multiValueMapHttpEntity = generateProfileRequest(naverEntity.getAccess_token());
        restTemplate = new RestTemplate();
        ResponseEntity<String> exchange1 = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                multiValueMapHttpEntity,
                String.class
        );
        return exchange1;
        /*
         * {"resultcode":"00","message":"success","response":{"id":"Uskk1N28hTzkqL5vPqnfUabCfidh7V54okhYeHXUxU0","age":"20-29","email":"leo503801@gmail.com","mobile":"010-9369-0376","mobile_e164":"+821093690376","name":"\uc774\uc0c1\uc6d0"}}
         */
    }

    private HttpEntity<MultiValueMap<String, String>> generateProfileRequest(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        return new HttpEntity<>(headers);
    }
}

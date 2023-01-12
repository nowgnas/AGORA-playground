package com.example.demo.oauth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("naver")
public class NaverLoginController {
    @GetMapping("auth")
    public String naverAuth(@RequestParam String code) {
        return "code : " + code;
    }
}

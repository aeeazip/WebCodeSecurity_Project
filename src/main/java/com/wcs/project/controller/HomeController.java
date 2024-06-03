package com.wcs.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    // 메인 화면
    @GetMapping("/")
    public String home() {
        return "home";
    }
}

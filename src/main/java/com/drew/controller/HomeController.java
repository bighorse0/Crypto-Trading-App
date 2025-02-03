package com.drew.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("")
    public String home() {
        return "trading app";
    }

    @GetMapping("/api")
    public String test() {
        return "test";
    }
}

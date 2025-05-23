package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
public class RootController {

    @GetMapping("/")
    public String root() {
        return "This is root URL!";
    }
}

package com.tutoras.tutoras.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class HelloController {

    @GetMapping("/api/")
    public String greeting() {
        return "Hello, world!";
    }
}

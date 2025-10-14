package com.spectra.control.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable("name") String name) {
        return "Hello " + name + "!";
    }

    @GetMapping("/number/{integer}")
    public String returnInteger(@PathVariable("integer") Integer integer) {
        return String.valueOf(integer);
    }
}

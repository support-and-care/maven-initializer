package com.openelements.maven.initializer.backend.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/hello")
    public Map<String, String> hello() {
        logger.info("Test INFO log message for Loki integration");
        logger.warn("Test WARN log message for Loki integration");
        logger.error("Test ERROR log message for Loki integration");
        logger.debug("Test DEBUG log message for Loki integration");
        return Map.of("message", "Hello from Maven Initializer Backend!");
    }
}

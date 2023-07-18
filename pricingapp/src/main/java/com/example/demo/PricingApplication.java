package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class PricingApplication {
    static Logger logger = LoggerFactory.getLogger(PricingApplication.class);
    private Map<String, Double> map = new HashMap<>();

    public PricingApplication() {
        map.put("Samsung", 345.0);

        map.put("iPhone13", 945.0);
    }

    @GetMapping("/products")
    public Map<String, Double> getProductList() {
        return map;
    }

    @GetMapping("/price/{productName}")
    public Double getProductPrice(@PathVariable String productName) {
        logger.info("Demo log: " + productName);
        return map.getOrDefault(productName, 0.0);
    }

    public static void main(String[] args) {
        logger.info("**** Starting Demo Application");
        SpringApplication.run(PricingApplication.class, args);
    }
}

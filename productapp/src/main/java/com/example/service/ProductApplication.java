package com.example.service;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class ProductApplication {

    static Logger logger = LoggerFactory.getLogger(ProductApplication.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/getProduct/{productName}")
    public ResponseEntity<Product> invokeGreetingService(@PathVariable String productName) {
        logger.info("***** Getting product");
        URI uri = discoveryClient.getInstances("pricing-app").stream()
                .map(si -> si.getUri())
                .findFirst()
                .map(s -> s.resolve("/price/"))
                .orElseThrow(() -> new RuntimeException("Demo application not available"));
                logger.info(uri.toString());

        try {
            logger.info("Service log");
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            String pricingUrl = UriComponentsBuilder.fromUri(uri).path(productName).encode().toUriString();
            ResponseEntity<Double> response = restTemplate.exchange(pricingUrl, HttpMethod.GET, entity, Double.class);
            Double price = response.getBody();
            logger.info("price: " + price);
            // Double price = restTemplate.getForObject(uri, Double.class);
            Product product = new Product(productName, price);
            return ResponseEntity.ok(product);
        } catch (HttpClientErrorException.NotFound ex) {
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/")
    String home(){
        return "Hello user";
    }

    public static void main(String[] args) {
        logger.info("******* Starting Service Application");
        SpringApplication.run(ProductApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Product class
    class Product {
        private String name;
        private double price;

        public Product() {
            // default constructor
        }

        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}

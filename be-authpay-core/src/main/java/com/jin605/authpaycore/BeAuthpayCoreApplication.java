package com.jin605.authpaycore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BeAuthpayCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeAuthpayCoreApplication.class, args);
    }

}

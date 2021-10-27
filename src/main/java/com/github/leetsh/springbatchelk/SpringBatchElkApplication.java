package com.github.leetsh.springbatchelk;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class SpringBatchElkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchElkApplication.class, args);
    }

}

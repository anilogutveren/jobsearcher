package com.aitools.jobsearcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.aitools.jobsearcher.adapter.persistence")
public class JobsearcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobsearcherApplication.class, args);
    }
}

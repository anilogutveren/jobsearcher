package com.aitools.jobsearcher;

import com.aitools.jobsearcher.model.Job;
import com.aitools.jobsearcher.service.JobService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JobsearcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobsearcherApplication.class, args);
    }

    @Bean
    public CommandLineRunner seed(JobService jobService) {
        return args -> {
            jobService.create(new Job(null, "Software Engineer", "Acme Corp", "Remote", "Work on backend services"));
            jobService.create(new Job(null, "Frontend Developer", "Webify", "New York, NY", "React/TypeScript developer"));
            jobService.create(new Job(null, "Data Scientist", "DataWorks", "San Francisco, CA", "ML and data analysis"));
        };
    }

}

package com.aitools.jobsearcher.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class JobEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String jobId;
    private String title;
    private String location;
    private LocalDateTime savedAt = LocalDateTime.now();
}

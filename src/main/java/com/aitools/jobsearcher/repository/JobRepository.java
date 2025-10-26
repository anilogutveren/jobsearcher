package com.aitools.jobsearcher.repository;

import com.aitools.jobsearcher.model.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<JobEntity, Long> {
    List<JobEntity> findByUserId(String userId);
}
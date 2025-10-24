package com.aitools.jobsearcher.service;

import com.aitools.jobsearcher.model.Job;
import com.aitools.jobsearcher.repository.InMemoryJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JobServiceTests {
    private JobService service;

    @BeforeEach
    void setUp() {
        service = new JobService(new InMemoryJobRepository());
    }

    @Test
    void createAndFind() {
        Job job = new Job(null, "DevOps Engineer", "InfraCo", "Austin, TX", "CI/CD and infra");
        Job created = service.create(job);
        assertThat(created.getId()).isNotNull();

        var fetched = service.findById(created.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getTitle()).isEqualTo("DevOps Engineer");
    }

    @Test
    void searchWorks() {
        service.create(new Job(null, "Backend", "A", "Remote", "Java"));
        service.create(new Job(null, "Frontend", "B", "NY", "React"));

        List<com.aitools.jobsearcher.model.Job> results = service.search("java");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Backend");
    }
}


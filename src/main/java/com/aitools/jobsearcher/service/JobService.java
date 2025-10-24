package com.aitools.jobsearcher.service;

import com.aitools.jobsearcher.model.Job;
import com.aitools.jobsearcher.repository.InMemoryJobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {
    private final InMemoryJobRepository repository;

    public JobService(InMemoryJobRepository repository) {
        this.repository = repository;
    }

    public List<Job> listAll() {
        return repository.findAll();
    }

    public Optional<Job> findById(String id) {
        return repository.findById(id);
    }

    public Job create(Job job) {
        return repository.save(job);
    }

    public Optional<Job> update(String id, Job update) {
        return repository.findById(id).map(existing -> {
            existing.setTitle(update.getTitle());
            existing.setCompany(update.getCompany());
            existing.setLocation(update.getLocation());
            existing.setDescription(update.getDescription());
            return repository.save(existing);
        });
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public List<Job> search(String q) {
        return repository.search(q);
    }
}


package com.aitools.jobsearcher.repository;

import com.aitools.jobsearcher.model.Job;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryJobRepository {
    private final Map<String, Job> store = new ConcurrentHashMap<>();

    public List<Job> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Job> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Job save(Job job) {
        if (job.getId() == null) {
            job.setId(UUID.randomUUID().toString());
        }
        store.put(job.getId(), job);
        return job;
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public List<Job> search(String query) {
        if (query == null || query.isBlank()) return findAll();
        String q = query.toLowerCase(Locale.ROOT);
        return store.values().stream()
                .filter(j -> matches(j, q))
                .collect(Collectors.toList());
    }

    private boolean matches(Job j, String q) {
        return (j.getTitle() != null && j.getTitle().toLowerCase(Locale.ROOT).contains(q))
                || (j.getCompany() != null && j.getCompany().toLowerCase(Locale.ROOT).contains(q))
                || (j.getLocation() != null && j.getLocation().toLowerCase(Locale.ROOT).contains(q))
                || (j.getDescription() != null && j.getDescription().toLowerCase(Locale.ROOT).contains(q));
    }
}


package com.aitools.jobsearcher.controller;

import com.aitools.jobsearcher.model.Job;
import com.aitools.jobsearcher.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }

    @GetMapping
    public List<Job> list(@RequestParam(value = "q", required = false) String q) {
        if (q == null || q.isBlank()) return service.listAll();
        return service.search(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> get(@PathVariable String id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Job> create(@RequestBody Job job) {
        Job created = service.create(job);
        return ResponseEntity.created(URI.create("/api/jobs/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Job> update(@PathVariable String id, @RequestBody Job job) {
        return service.update(id, job)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


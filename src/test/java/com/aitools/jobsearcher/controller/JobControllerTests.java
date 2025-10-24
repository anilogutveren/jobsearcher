package com.aitools.jobsearcher.controller;

import com.aitools.jobsearcher.model.Job;
import com.aitools.jobsearcher.service.JobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(JobController.class)
class JobControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobService jobService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
    }

    @Test
    void listReturnsJobs() throws Exception {
        when(jobService.listAll()).thenReturn(List.of(new Job("1","t","c","l","d")));

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("[0].id").value("1"));
    }

    @Test
    void createReturns201() throws Exception {
        Job in = new Job(null, "t","c","l","d");
        Job out = new Job("42", "t","c","l","d");
        when(jobService.create(any(Job.class))).thenReturn(out);

        mockMvc.perform(post("/api/jobs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(in)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/jobs/42"))
                .andExpect(jsonPath(".id").value("42"));
    }
}


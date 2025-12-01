package com.aitools.jobsearcher.adapter.otlm;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OtlmAppService {

    private final RestClient restClient;

    public OtlmAppService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://host.docker.internal:8082").build();
    }

    public void postAllJobsToOtlm(String jobData) {
        restClient.post().uri("/jobs").body(jobData);
    }
}

package com.aitools.jobsearcher.adapter.otlm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class OtlmAppService {

    @Value(
        "${otlm.base-url:http://host.docker.internal:8082}"
    )
    private String otlmBaseUrl;

    private final RestClient restClient;

    public OtlmAppService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl(otlmBaseUrl).build();
    }

    public void postAllJobsToOtlm(String jobData) {
        restClient.post().uri("/jobs").body(jobData);
    }
}

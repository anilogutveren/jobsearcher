package com.aitools.jobsearcher.adapter.otlm.model;

public record Job (
    String title,
    String company,
    String location,
    String description,
    String url
) {
}

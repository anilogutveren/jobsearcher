package com.aitools.jobsearcher.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PromptEntity(
        UUID id,
        String userInput,
        PromptStatus status,
        Instant createdAt
) {
}
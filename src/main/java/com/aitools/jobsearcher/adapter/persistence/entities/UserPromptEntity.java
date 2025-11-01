package com.aitools.jobsearcher.adapter.persistence.entities;

import com.aitools.jobsearcher.domain.model.PromptStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_prompts")
public class UserPromptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String userInput;

    @Enumerated
    private PromptStatus status;

    @Column
    private Instant createdAt;

    public UserPromptEntity(UUID id, String userInput, PromptStatus status, Instant createdAt) {
        this.id = id;
        this.userInput = userInput;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UserPromptEntity() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }

    public PromptStatus getStatus() {
        return status;
    }

    public void setStatus(PromptStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}

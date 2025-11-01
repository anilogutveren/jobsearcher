package com.aitools.jobsearcher.adapter.persistence.repository;

import com.aitools.jobsearcher.adapter.persistence.entities.UserPromptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserPromptRepositoryJpa extends JpaRepository<UserPromptEntity, UUID> {

}

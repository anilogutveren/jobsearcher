package com.aitools.jobsearcher.adapter.persistence;

import com.aitools.jobsearcher.adapter.persistence.entities.UserPromptEntity;
import com.aitools.jobsearcher.adapter.persistence.repository.UserPromptRepositoryJpa;
import com.aitools.jobsearcher.domain.model.PromptEntity;
import com.aitools.jobsearcher.domain.repository.UserPromptRepository;
import org.springframework.stereotype.Repository;

@Repository
public class UserPromptRepositoryAdapter implements UserPromptRepository {

    private final UserPromptRepositoryJpa userPromptRepositoryJpa;

    public UserPromptRepositoryAdapter(final UserPromptRepositoryJpa userPromptRepositoryJpa) {
        this.userPromptRepositoryJpa = userPromptRepositoryJpa;
    }

    @Override
    public PromptEntity saveOrUpdate(PromptEntity promptEntity) {
        UserPromptEntity userPromptEntity = userPromptRepositoryJpa.save(toUserPromptEntity(promptEntity));
        return toPromptEntity(userPromptEntity);
    }

    @Override
    public void deleteAll() {
        userPromptRepositoryJpa.deleteAll();
    }

    private UserPromptEntity toUserPromptEntity(PromptEntity promptEntity) {
        return new UserPromptEntity(
                promptEntity.id(),
                promptEntity.userInput(),
                promptEntity.status(),
                promptEntity.createdAt()
        );
    }

    private PromptEntity toPromptEntity(UserPromptEntity userPromptEntity) {
        return new PromptEntity(
                userPromptEntity.getId(),
                userPromptEntity.getUserInput(),
                userPromptEntity.getStatus(),
                userPromptEntity.getCreatedAt()
        );
    }
}

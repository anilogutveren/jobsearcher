package com.aitools.jobsearcher.domain.repository;

import com.aitools.jobsearcher.domain.model.PromptEntity;

public interface UserPromptRepository {
    public PromptEntity saveOrUpdate(PromptEntity promptEntity);

    public void deleteAll();
}

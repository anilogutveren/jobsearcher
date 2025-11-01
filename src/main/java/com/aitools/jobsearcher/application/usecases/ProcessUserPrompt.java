package com.aitools.jobsearcher.application.usecases;

import com.aitools.jobsearcher.adapter.persistence.UserPromptRepositoryAdapter;
import com.aitools.jobsearcher.application.commands.ProcessUserPromptCommand;
import com.aitools.jobsearcher.application.tools.JobSearchTools;
import com.aitools.jobsearcher.domain.model.PromptEntity;
import com.aitools.jobsearcher.domain.model.PromptStatus;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ProcessUserPrompt {

    private final UserPromptRepositoryAdapter userPromptRepositoryAdapter;

    private final ChatClient chatClient;

    private final JobSearchTools jobSearchTools;

    public ProcessUserPrompt(ChatClient.Builder builder,
                             VectorStore vectorStore,
                             JobSearchTools jobSearchTools,
                             UserPromptRepositoryAdapter userPromptRepositoryAdapter) {
        this.userPromptRepositoryAdapter = userPromptRepositoryAdapter;
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
        this.jobSearchTools = jobSearchTools;
    }

    public String execute(ProcessUserPromptCommand command) {

        PromptEntity promptEntity = new PromptEntity(
                null,
                command.userPrompt(),
                PromptStatus.CREATED,
                Instant.now());

        promptEntity = userPromptRepositoryAdapter.saveOrUpdate(promptEntity);
        String reply =  chatClient.prompt()
                .user(command.userPrompt())
                .tools(jobSearchTools)
                .call()
                .content();

        userPromptRepositoryAdapter.saveOrUpdate(new PromptEntity(
                promptEntity.id(),
                promptEntity.userInput(),
                PromptStatus.PROCESSED,
                promptEntity.createdAt()
        ));
        return reply;
    }
}

package com.aitools.jobsearcher.application.usecases;

import com.aitools.jobsearcher.adapter.persistence.UserPromptRepositoryAdapter;
import com.aitools.jobsearcher.application.commands.ProcessUserPromptCommand;
import com.aitools.jobsearcher.application.tools.JobSearchTools;
import com.aitools.jobsearcher.domain.model.PromptEntity;
import com.aitools.jobsearcher.domain.model.PromptStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
public class ProcessUserPrompt {

    private static final Logger logger = LoggerFactory.getLogger(ProcessUserPrompt.class);

    private final UserPromptRepositoryAdapter userPromptRepositoryAdapter;

    private final ChatClient chatClient;

    private final JobSearchTools jobSearchTools;

    public ProcessUserPrompt(ChatClient.Builder builder,
                             VectorStore vectorStore,
                             JobSearchTools jobSearchTools,
                             UserPromptRepositoryAdapter userPromptRepositoryAdapter) {

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .build();

        this.userPromptRepositoryAdapter = userPromptRepositoryAdapter;
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .defaultOptions(chatOptions)
                .build();
        this.jobSearchTools = jobSearchTools;
    }

    public String execute(ProcessUserPromptCommand command) {
        logger.info("Processing user prompt");
        PromptEntity promptEntity = new PromptEntity(
                null,
                command.userPrompt(),
                PromptStatus.CREATED,
                Instant.now());
        promptEntity = userPromptRepositoryAdapter.saveOrUpdate(promptEntity);


        ClassPathResource resource = new ClassPathResource("prompts/job-assistant-system-prompt.txt");
        String systemPrompt = null;
        try {
            systemPrompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String reply = chatClient.prompt()
                .system(systemPrompt)
                .user(command.userPrompt())
                .tools(jobSearchTools)
                .call()
                .content();

        logger.info("Processed user prompt");
        userPromptRepositoryAdapter.saveOrUpdate(new PromptEntity(
                promptEntity.id(),
                promptEntity.userInput(),
                PromptStatus.PROCESSED,
                promptEntity.createdAt()
        ));
        logger.debug("user prompt saved with PROCESSED status");
        return reply;
    }
}

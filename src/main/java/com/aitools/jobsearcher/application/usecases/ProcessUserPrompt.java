package com.aitools.jobsearcher.application.usecases;

import com.aitools.jobsearcher.adapter.otlm.model.Job;
import com.aitools.jobsearcher.adapter.persistence.UserPromptRepositoryAdapter;
import com.aitools.jobsearcher.application.commands.ProcessUserPromptCommand;
import com.aitools.jobsearcher.application.tools.JobSearchTools;
import com.aitools.jobsearcher.domain.model.PromptEntity;
import com.aitools.jobsearcher.domain.model.PromptStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

@Component
public class ProcessUserPrompt {

    private static final Logger logger = LoggerFactory.getLogger(ProcessUserPrompt.class);

    private final UserPromptRepositoryAdapter userPromptRepositoryAdapter;

    private final ChatClient chatClient;

    private final JobSearchTools jobSearchTools;

    private final SimpleLoggerAdvisor simpleLoggerAdvisor;


    public ProcessUserPrompt(ChatClient.Builder builder,
                             VectorStore vectorStore,
                             JobSearchTools jobSearchTools,
                             UserPromptRepositoryAdapter userPromptRepositoryAdapter,
                             SimpleLoggerAdvisor simpleLoggerAdvisor) {

        OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .build();

        this.userPromptRepositoryAdapter = userPromptRepositoryAdapter;
        this.chatClient = builder
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .defaultOptions(chatOptions)
                .build();
        this.jobSearchTools = jobSearchTools;
        this.simpleLoggerAdvisor = simpleLoggerAdvisor;
    }

    public String execute(ProcessUserPromptCommand command) {

        // Setup the converter for List<Job>
        // We use BeanOutputConverter because 'Job' is a complex structure (Record), not a simple String.
        var outputConverter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<Job>>() {
        });

        logger.info("Processing user prompt");
        PromptEntity promptEntity = new PromptEntity(
                null,
                command.userPrompt(),
                PromptStatus.CREATED,
                Instant.now());
        promptEntity = userPromptRepositoryAdapter.saveOrUpdate(promptEntity);

        // Add the JSON schema format instructions to the System Prompt
        // This tells the AI: "Output valid JSON where every item has title, company, etc."
        String systemPrompt = getSystemPrompt() + System.lineSeparator() + outputConverter.getFormat();

        List<Job> jobs = chatClient.prompt()
                .system(systemPrompt)
                .user(command.userPrompt())
                .advisors(simpleLoggerAdvisor)
                .tools(jobSearchTools)
                .call()
                .entity(outputConverter);

        logger.info("Extracted {} jobs from AI response", jobs.size());

        // Logic to save status
        userPromptRepositoryAdapter.saveOrUpdate(new PromptEntity(
                promptEntity.id(),
                promptEntity.userInput(),
                PromptStatus.PROCESSED,
                promptEntity.createdAt()
        ));

        return jobs.toString();
    }

    private static String getSystemPrompt() {
        ClassPathResource resource = new ClassPathResource("prompts/job-assistant-system-prompt.txt");
        String systemPrompt;
        try {
            systemPrompt = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return systemPrompt;
    }
}

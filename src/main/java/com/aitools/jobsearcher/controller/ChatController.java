package com.aitools.jobsearcher.controller;

import com.aitools.jobsearcher.tools.JobSearchTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    private final JobSearchTools jobSearchTools;

    public ChatController(ChatClient.Builder builder,
                          VectorStore vectorStore,
                          JobSearchTools jobSearchTools) {
        this.chatClient = builder
                .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
                .build();
        this.jobSearchTools = jobSearchTools;
    }

    @PostMapping("/")
    public String chat(
            @RequestBody String userInput
    ) {
        return chatClient.prompt()
                .user(userInput)
                .tools(jobSearchTools)
                .call()
                .content();
    }
}

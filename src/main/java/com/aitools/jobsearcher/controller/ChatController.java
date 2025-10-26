package com.aitools.jobsearcher.controller;

import com.aitools.jobsearcher.tools.JobSearchTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
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

    @PostMapping("/processUserPrompt")
    public String handleChat(@RequestParam("userInput") String userInput, Model model) {
        String reply = chatClient.prompt()
                .user(userInput)
                .tools(jobSearchTools)
                .call()
                .content();
        model.addAttribute("userInput", userInput);
        model.addAttribute("reply", reply);
        return "chat"; // Return the Thymeleaf view name
    }

}

package com.aitools.jobsearcher.adapter.controller;

import com.aitools.jobsearcher.adapter.otlm.OtlmAppService;
import com.aitools.jobsearcher.application.commands.ProcessUserPromptCommand;
import com.aitools.jobsearcher.application.usecases.ProcessUserPrompt;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

    private final ProcessUserPrompt processUserPrompt;

    private final OtlmAppService otlmAppService;

    public ChatController(ProcessUserPrompt processUserPrompt, OtlmAppService otlmAppService) {
        this.processUserPrompt = processUserPrompt;
        this.otlmAppService = otlmAppService;
    }

    @PostMapping("/processUserPrompt")
    public String handleChat(@RequestParam("userInput") String userInput, Model model) {
        userInput = userInput + "\nProvide the response in JSON format suitable for job listings.";

        String reply = processUserPrompt.execute(new ProcessUserPromptCommand(userInput));
        otlmAppService.postAllJobsToOtlm(reply);
        model.addAttribute("userInput", userInput);
        model.addAttribute("reply", reply);

        return "chat"; // Return the Thymeleaf view name
    }

    @DeleteMapping
    public String clearChat(Model model) {

        model.addAttribute("reply", "All Chat History Cleared.");
        return "chat"; // Return the Thymeleaf view name
    }
}

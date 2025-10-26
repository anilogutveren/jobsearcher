package com.aitools.jobsearcher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/chat")
    public String handleChat(Model model) {
        model.addAttribute("message", "Hello from Anils Searcher");
        return "chat";
    }
}

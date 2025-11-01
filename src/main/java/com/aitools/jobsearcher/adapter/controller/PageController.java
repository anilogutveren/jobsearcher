package com.aitools.jobsearcher.adapter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String mainPage(Model model) {
        return "index";
    }

    @GetMapping("/chat")
    public String handleChat(Model model) {
        model.addAttribute("message", "Hello from Anils Job Searcher");
        return "chat";
    }
}

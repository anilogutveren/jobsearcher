package com.aitools.jobsearcher.domain.port;

interface OpenAIPort {
    void sendPromptToOpenAI(String prompt);
}

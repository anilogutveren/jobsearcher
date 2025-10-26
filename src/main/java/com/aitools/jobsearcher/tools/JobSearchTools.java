package com.aitools.jobsearcher.tools;

import com.aitools.jobsearcher.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
public class JobSearchTools {

    private static final Logger log = LoggerFactory.getLogger(JobSearchTools.class);

    private final IngestionService ingestionService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("classpath:/docs/CoverLetter.pdf")
    private Resource coverLetterPdf;

    @Autowired
    public JobSearchTools(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "Ingest a cover letter PDF to the vector store.")
    public void ingestCoverLetterInVectorStore() {
        ingestionService.ingest(coverLetterPdf);
        log.info("Ingested cover letter from classpath resource.");
    }

    @Tool(description = "Search the web for jobs in real time for the user's query.")
    public String webSearch(String query) {
        try {
            String apiUrl = "https://api.duckduckgo.com/?q="
                    + URLEncoder.encode(query, StandardCharsets.UTF_8)
                    + "&format=json";
            String response = restTemplate.getForObject(apiUrl, String.class);
            log.info("Searched web for query: {}", query);
            return response;
        } catch (Exception e) {
            log.error("Error performing web search: {}", e.getMessage());
            return "Failed to fetch search results.";
        }
    }
}
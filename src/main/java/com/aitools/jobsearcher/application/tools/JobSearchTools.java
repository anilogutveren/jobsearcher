package com.aitools.jobsearcher.application.tools;

import com.aitools.jobsearcher.application.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class JobSearchTools {

    private static final Logger log = LoggerFactory.getLogger(JobSearchTools.class);

    @Value("${tavily.api-key}")
    private String apiKey;

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

    @Tool(description = "Performs a real-time web search and returns summarized results with URLs")
    public String webSearch(String query) {
        try {
            String url = "https://api.tavily.com/search";
            Map<String, Object> body = Map.of(
                    "query", query,
                    "max_results", 5,
                    "include_answer", true
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return formatResults(response.getBody());
            } else {
                return "No web search results found.";
            }
        } catch (Exception e) {
            return "Error during Tavily search: " + e.getMessage();
        }
    }

    private String formatResults(Map<String, Object> response) {
        StringBuilder sb = new StringBuilder("### 🌐 Web Search Results:\n\n");

        if (response.containsKey("answer")) {
            sb.append("**Summary:** ").append(response.get("answer")).append("\n\n");
        }

        if (response.containsKey("results")) {
            List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");
            for (Map<String, Object> r : results) {
                sb.append("**").append(r.get("title")).append("**\n")
                        .append(r.get("url")).append("\n\n");
            }
        }
        return sb.toString();
    }
}
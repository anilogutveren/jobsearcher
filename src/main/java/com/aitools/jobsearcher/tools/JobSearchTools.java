package com.aitools.jobsearcher.tools;

import com.aitools.jobsearcher.model.JobEntity;
import com.aitools.jobsearcher.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;

@Component
public class JobSearchTools {

    private static final Logger log = LoggerFactory.getLogger(JobSearchTools.class);

    private final IngestionService ingestionService;

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

    /*
    @Tool(name = "save_job", description = "Save a job for a user")
    public String saveJobForUser(@Param("userId") String userId, @Param("jobId") String jobId) {
        JobEntity job = new JobEntity();
        job.setUserId(userId);
        job.setJobId(jobId);
        repository.save(job);
        return "Job saved successfully!";
    } */
}
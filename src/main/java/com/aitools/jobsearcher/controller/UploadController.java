package com.aitools.jobsearcher.controller;

import com.aitools.jobsearcher.service.IngestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Controller
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);
    private final IngestionService ingestionService;

    public UploadController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String handleUpload(MultipartFile cv, MultipartFile coverLetter, Model model) {
        StringBuilder message = new StringBuilder();

        if ((cv == null || cv.isEmpty()) && (coverLetter == null || coverLetter.isEmpty())) {
            model.addAttribute("message", "Please select at least one file (CV or Cover Letter) to upload.");
            return "upload";
        }

        if (cv != null && !cv.isEmpty()) {
            try {
                Path tempCv = Files.createTempFile("uploaded-cv-", ".pdf");
                cv.transferTo(tempCv.toFile());
                ingestionService.ingest(new FileSystemResource(tempCv.toFile()));
                message.append("CV uploaded and ingested. ");
                log.info("Uploaded CV saved to {} and ingested.", tempCv);
            } catch (IOException e) {
                log.error("Failed to process uploaded CV", e);
                message.append("Failed to ingest CV. ");
            }
        }

        if (coverLetter != null && !coverLetter.isEmpty()) {
            try {
                Path tempCover = Files.createTempFile("uploaded-cover-", ".pdf");
                coverLetter.transferTo(tempCover.toFile());
                ingestionService.ingest(new FileSystemResource(tempCover.toFile()));
                message.append("Cover letter uploaded and ingested. ");
                log.info("Uploaded cover letter saved to {} and ingested.", tempCover);
            } catch (IOException e) {
                log.error("Failed to process uploaded cover letter", e);
                message.append("Failed to ingest cover letter. ");
            }
        }

        model.addAttribute("message", message.toString().trim());
        return "result";
    }
}

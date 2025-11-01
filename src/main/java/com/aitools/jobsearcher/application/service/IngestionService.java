package com.aitools.jobsearcher.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;

    @Value("classpath:/docs/CV_Anil_Ogutveren_New.pdf")
    private Resource cvPDF;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... arg) {
        // keep startup behavior using the configured cvPDF
        ingest(cvPDF);
    }

    // public method so callers can pass any Resource (classpath or filesystem)
    public void ingest(Resource pdf) {
        TextSplitter textSplitter = new TokenTextSplitter();
        List<Document> docs = null;
        Exception lastException = null;

        try {
            docs = readParagraph(pdf);
        } catch (Exception e) {
            lastException = e;
            log.warn("ParagraphPdfDocumentReader failed: {}. Falling back to page reader.", e.toString());
        }

        if (docs == null) {
            try {
                docs = readPage(pdf);
            } catch (Exception e) {
                lastException = e;
                log.warn("PagePdfDocumentReader failed: {}. Falling back to Tika reader.", e.toString());
            }
        }

        if (docs == null) {
            try {
                docs = readTika(pdf);
            } catch (Exception e) {
                lastException = e;
                log.error("TikaDocumentReader failed: {}.", e.toString());
            }
        }

        if (docs == null || docs.isEmpty()) {
            throw new IllegalStateException("Document outline/content is null or empty. Ensure the PDF has a TOC or use a reader that extracts pages/text (PagePdfDocumentReader or TikaDocumentReader).",
                    lastException);
        }

        vectorStore.accept(textSplitter.apply(docs));
        log.info("VectorStore loaded with data!");
    }

    private List<Document> readParagraph(Resource pdf) {
        var paragraphReader = new ParagraphPdfDocumentReader(pdf);
        var read = paragraphReader.get();
        if (read != null && !read.isEmpty()) {
            log.info("Loaded document using ParagraphPdfDocumentReader (TOC-aware).");
            return read;
        } else {
            log.warn("ParagraphPdfDocumentReader returned no content (TOC or paragraphs may be missing). Falling back.");
            return null;
        }
    }

    private List<Document> readPage(Resource pdf) {
        var pageReader = new PagePdfDocumentReader(pdf);
        var read = pageReader.get();
        if (read != null && !read.isEmpty()) {
            log.info("Loaded document using PagePdfDocumentReader (page-by-page).");
            return read;
        } else {
            log.warn("PagePdfDocumentReader returned no content. Falling back to Tika reader.");
            return null;
        }
    }

    private List<Document> readTika(Resource pdf) {
        var tikaReader = new TikaDocumentReader(pdf);
        var read = tikaReader.get();
        if (read != null && !read.isEmpty()) {
            log.info("Loaded document using TikaDocumentReader (text extraction).");
            return read;
        } else {
            log.warn("TikaDocumentReader returned no content.");
            return null;
        }
    }
}
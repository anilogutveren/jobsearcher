# Getting Started

## Introduction
This is a Spring Boot application built with Gradle. It provides AI-powered features for job searching and Q&A over your documents:
- RAG: Ingest your CV into a PGVector database and answer questions based on its content.
- Tool Calling: Ingest your Cover Letter and answer questions based on it.

## Prerequisites
Ensure the Docker Compose run configuration includes the required environment variables (see the image below):
![Env Variables for Docker Compose.png](src/main/resources/docs/Env%20Variables%20for%20Docker%20Compose.png)

## Guides
After starting the app, visit:
- http://localhost:8080/chat — Web UI for chat and job search.

You can:
- Upload your CV and Cover Letter using the form.
- Ask questions related to the content of these documents.
- Use the job search chat to find jobs in your preferred location.

## Local Development
Note: The `local` profile does not currently work due to Ollama model limitations (text embedding is not supported). Use Docker Desktop Model Runner to run the Llama 3.2 model locally instead.

To run locally with Docker Desktop Model Runner:
1. Start the Llama 3.2 model on the Docker Desktop model runner.
2. Open the TCP port for the model runner.
3. Enable the model runner:

```powershell
docker desktop enable model-runner --tcp=12434
```

4. Test the endpoint:

```powershell
curl http://localhost:12434/engines/llama.cpp/v1/chat/completions `
  -H "Content-Type: application/json" `
  -d '{
    "model": "ai/llama3.2:latest",
    "messages": [
      {"role": "user", "content": "Tell me about Docker Model Runner."}
    ]
  }'
```

For Ollama embeddings support and a future `ollama` profile, see:
- https://docs.spring.io/spring-ai/reference/api/embeddings/ollama-embeddings.html

## Reference Documentation
- Official Gradle: https://docs.gradle.org
- Spring Boot Gradle Plugin: https://docs.spring.io/spring-boot/3.5.6/gradle-plugin
- Create an OCI image: https://docs.spring.io/spring-boot/3.5.6/gradle-plugin/packaging-oci-image.html
- Tika Document Reader: https://docs.spring.io/spring-ai/reference/api/etl-pipeline.html#_tika_docx_pptx_html
- OpenAI Chat: https://docs.spring.io/spring-ai/reference/api/chat/openai-chat.html
- Spring Web (Servlet): https://docs.spring.io/spring-boot/3.5.6/reference/web/servlet.html
- PGVector Vector Database: https://docs.spring.io/spring-ai/reference/api/vectordbs/pgvector.html

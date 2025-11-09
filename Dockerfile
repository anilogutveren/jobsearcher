# Dockerfile
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle wrapper + build scripts
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle* settings.gradle* ./

COPY src src

RUN chmod +x gradlew && ./gradlew clean bootJar -x test

FROM eclipse-temurin:21-jre AS runtime
# If eclipse-temurin:21-jre not available in your environment, replace with eclipse-temurin:21-jdk
WORKDIR /app
ENV JAVA_OPTS="-Xms256m -Xmx512m"
EXPOSE 8085
COPY --from=build /app/build/libs/jobsearcher-*-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
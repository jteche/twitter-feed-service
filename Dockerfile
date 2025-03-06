FROM bellsoft/liberica-openjdk-alpine:17
ARG PROJECT_JAR
WORKDIR /app
COPY $PROJECT_JAR /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

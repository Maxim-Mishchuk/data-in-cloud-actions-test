FROM openjdk:17-alpine
LABEL authors="laurenci"
COPY app.jar project.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "project.jar"]

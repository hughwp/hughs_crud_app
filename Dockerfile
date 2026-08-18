FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/hughs_crud_app-1.0-SNAPSHOT.jar app.jar
COPY build/libs/*.jar libs/

EXPOSE 8080

ENTRYPOINT ["java", "-cp", "app.jar:libs/*", "-jar", "app.jar"]
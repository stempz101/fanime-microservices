FROM openjdk:17-alpine
WORKDIR /app
COPY target/anime-service-*.jar anime-service.jar
EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "anime-service.jar"]

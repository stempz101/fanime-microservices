FROM openjdk:17-alpine
WORKDIR /app
COPY target/anime-service-*.jar anime-service.jar
EXPOSE 8081:8081
ENTRYPOINT ["java", "-jar", "anime-service.jar"]

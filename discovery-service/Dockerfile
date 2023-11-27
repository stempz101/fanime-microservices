FROM openjdk:17-alpine
WORKDIR /app
COPY target/discovery-service-*.jar discovery-service.jar
EXPOSE 8761:8761
ENTRYPOINT ["java", "-jar", "discovery-service.jar"]

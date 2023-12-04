FROM openjdk:17-alpine
WORKDIR /app
COPY target/user-service-*.jar user-service.jar
EXPOSE 8082:8082
ENTRYPOINT ["java", "-jar", "user-service.jar"]

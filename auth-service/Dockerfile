FROM openjdk:17-alpine
WORKDIR /app
COPY target/auth-service-*.jar auth-service.jar
EXPOSE 8080:8080
ENTRYPOINT ["java", "-jar", "auth-service.jar"]

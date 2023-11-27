FROM openjdk:17-alpine
WORKDIR /app
COPY target/api-gateway-service-*.jar api-gateway-service.jar
EXPOSE 8765:8765
ENTRYPOINT ["java", "-jar", "api-gateway-service.jar"]

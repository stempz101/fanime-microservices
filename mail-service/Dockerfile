FROM openjdk:17-alpine
WORKDIR /app
COPY target/mail-service-*.jar mail-service.jar
EXPOSE 8888:8888
ENTRYPOINT ["java", "-jar", "mail-service.jar"]

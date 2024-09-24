FROM openjdk:17-jdk-alpine

COPY target/exchange-rate-api-0.0.1-SNAPSHOT.jar /app/exchange-rate-api.jar

ENTRYPOINT ["java", "-jar", "/app/exchange-rate-api.jar"]

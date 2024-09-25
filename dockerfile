FROM eclipse-temurin:21-jdk

COPY target/exchange-rate-api-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]

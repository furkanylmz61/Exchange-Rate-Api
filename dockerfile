FROM eclipse-temurin:21-jdk

ADD ./exchange-rate-api-0.0.1-SNAPSHOT.jar exchange-rate-api-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "./target/exchange-rate-api-0.0.1-SNAPSHOT.jar"]

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN chmod +x ./mvnw

RUN ./mvnw clean package -DskipTests

CMD ["java", "-jar", "./target/exchange-rate-api-0.0.1-SNAPSHOT.jar"]

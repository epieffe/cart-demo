FROM maven:3.9-eclipse-temurin-21-alpine AS base
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e dependency:go-offline
COPY src ./src
RUN mvn -q -e compile -DskipTests

FROM base AS runtime
RUN mvn -q -e package -DskipTests
ENV SPRING_PROFILES_ACTIVE=docker
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/cart-demo.jar"]

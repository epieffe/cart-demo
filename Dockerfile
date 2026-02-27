# This is the base image, used to run tests in scripts/tests.sh
FROM maven:3.9-eclipse-temurin-21-alpine AS base
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e dependency:go-offline
COPY src ./src
RUN mvn -q -e compile -DskipTests


# Build and extract the jar file using an efficient layout for layering
FROM base as build
RUN mvn -q -e package -DskipTests
RUN java -Djarmode=tools -jar target/cart-demo.jar extract --layers --destination /extracted


# Analyze app dependencies and create a custom JRE containing only the required modules
FROM maven:3.9-eclipse-temurin-21-alpine AS jlink
COPY --from=build /app/target/cart-demo.jar cart-demo.jar
RUN jar xf cart-demo.jar
RUN jdeps -q --ignore-missing-deps --recursive --multi-release 21 \
  --print-module-deps --class-path 'BOOT-INF/lib/*' \
  cart-demo.jar > deps.info
RUN jlink --add-modules $(cat deps.info) --no-header-files --no-man-pages --output /custom_jre


# Final runtime image
FROM alpine:latest
EXPOSE 8080
WORKDIR /app
RUN adduser -S -D -H user
USER user
ENV SPRING_PROFILES_ACTIVE=docker
COPY --from=jlink /custom_jre /jre
COPY --from=build /extracted/dependencies/ ./
COPY --from=build /extracted/spring-boot-loader/ ./
COPY --from=build /extracted/snapshot-dependencies/ ./
COPY --from=build /extracted/application/ ./
ENTRYPOINT ["/jre/bin/java", "-jar", "cart-demo.jar"]

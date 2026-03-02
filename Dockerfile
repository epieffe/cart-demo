# This is the base image, used to run tests in scripts/tests.sh
FROM maven:3.9-eclipse-temurin-21-alpine AS base
WORKDIR /app
COPY pom.xml .
RUN mvn -q -e dependency:go-offline
COPY src ./src
RUN mvn -q -e compile test


FROM base as build
RUN mvn -q -e package -DskipTests
# Analyze app dependencies
RUN jar xf target/cart-demo.jar && jdeps -q -R -cp 'BOOT-INF/lib/*' \
    --ignore-missing-deps --multi-release 21 --print-module-deps \
    target/cart-demo.jar > deps.info
# Extract the jar file using an efficient layout for layering
RUN java -Djarmode=tools -jar target/cart-demo.jar extract --layers --destination /layers


# Create a custom JRE containing only the required modules
FROM maven:3.9-eclipse-temurin-21-alpine AS jlink
COPY --from=build /app/deps.info .
RUN jlink --add-modules $(cat deps.info) --no-header-files --no-man-pages --output /custom_jre


# Final runtime image
FROM alpine:latest
EXPOSE 8080
WORKDIR /app
RUN adduser -S -D -H user
USER user
ENV SPRING_PROFILES_ACTIVE=docker
COPY --from=jlink /custom_jre /jre
COPY --from=build /layers/dependencies/ ./
COPY --from=build /layers/spring-boot-loader/ ./
COPY --from=build /layers/snapshot-dependencies/ ./
COPY --from=build /layers/application/ ./
ENTRYPOINT ["/jre/bin/java", "-jar", "cart-demo.jar"]

# Build stage

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY mvnw pom.xml ./
COPY .mvn .mvn
ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}
LABEL app.version=${APP_VERSION}

RUN ./mvnw -q -e dependency:resolve dependency:resolve-plugins
COPY src src
RUN ./mvnw -q -e -DskipTests package

# Final stage

FROM eclipse-temurin:17-jdk
ARG APP_VERSION=dev
ENV APP_VERSION=${APP_VERSION}
ENV SPRING_PROFILES_ACTIVE=demo

WORKDIR /app
COPY --from=build /app/target/pixelmate-${APP_VERSION}.jar pixelmate.jar
COPY jvm-options.txt jvm-options.txt

# Healthcheck
HEALTHCHECK --interval=1m --timeout=3s --start-period=15s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

EXPOSE 8085
ENTRYPOINT ["sh", "-c", "java @jvm-options.txt -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar pixelmate.jar"]
# Stage 1 - build
FROM eclipse-temurin:21-jdk-alpine-3.20 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradlew /app/
COPY gradle ./gradle
RUN ./gradlew build --no-daemon

COPY src ./src
RUN ./gradlew bootJar --no-daemon 

# Stage 2 - runtime
FROM eclipse-temurin:21-jre-alpine-3.21 AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/service.jar /app/service.jar
RUN mkdir -p /app/uploads
VOLUME /app/uploads
EXPOSE 8081
CMD ["java", "-jar", "service.jar"]
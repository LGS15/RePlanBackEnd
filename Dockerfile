# Build stage
FROM gradle:8.13-jdk17 AS build
WORKDIR /app
COPY APIcred .
RUN gradle build -x test --no-daemon

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
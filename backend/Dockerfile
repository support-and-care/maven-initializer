# Build stage
FROM eclipse-temurin:21.0.5_11-jre as builder
WORKDIR /application

# Copy prebuilt JAR
COPY target/*.jar backend-1.0.0-SNAPSHOT.jar

# Extract the Spring Boot layers
RUN java -Djarmode=layertools -jar backend-1.0.0-SNAPSHOT.jar extract

# Production stage (use minimal distroless runtime)
FROM gcr.io/distroless/java21-debian12
WORKDIR /application
EXPOSE 8080

# Copy extracted layers from the builder stage
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./

# Run with Spring Boot's JarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

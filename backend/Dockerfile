# Build stage
FROM amazoncorretto:25-alpine as builder
WORKDIR /application

# Copy prebuilt JAR
COPY target/*.jar application.jar

# Extract the Spring Boot layers
RUN java -Djarmode=layertools -jar application.jar extract

# Production stage (use minimal distroless runtime)
FROM amazoncorretto:25-alpine
WORKDIR /application
EXPOSE 8080

# Copy extracted layers from the builder stage
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./

# Run with Spring Boot's JarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

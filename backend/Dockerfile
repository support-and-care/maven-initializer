# Maven Build stage

FROM eclipse-temurin:25 AS maven-build

WORKDIR /mavenbuild

COPY . /mavenbuild

RUN ./mvnw package -DskipTests

# Preparation stage
FROM eclipse-temurin:25 AS builder
WORKDIR /application

# Copy prebuilt JAR
COPY --from=maven-build /mavenbuild/target/*.jar application.jar

# Extract the Spring Boot layers
RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:25-alpine
WORKDIR /application
EXPOSE 8080

# Install Maven for Maven Wrapper generation feature
RUN apk add --no-cache maven

# Copy extracted layers from the builder stage
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application/ ./

# Run with Spring Boot's JarLauncher
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]

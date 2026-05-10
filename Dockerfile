FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN apt-get update && apt-get install -y maven && \
    mvn dependency:go-offline

# Copy source code
COPY src src/

# Build application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run application
ENTRYPOINT ["java", "-jar", "target/tax-gap-detection-1.0.0.jar"]

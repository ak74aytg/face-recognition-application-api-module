# Stage 1: Build the application
FROM maven:3.8.3-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application
FROM openjdk:17.0.1-jdk-slim
WORKDIR /app
# Copy the jar file from the build stage
COPY --from=build /app/target/face-recognition-0.0.1-SNAPSHOT.jar face-recognition.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "face-recognition.jar"]

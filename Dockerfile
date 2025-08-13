# Step 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first (for better build caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Step 2: Run stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy only the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

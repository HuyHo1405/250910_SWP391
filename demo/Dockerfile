# Build stage
FROM eclipse-temurin:24-jdk-jammy AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline || true
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:24-jre-jammy
WORKDIR /app
EXPOSE 8080
COPY --from=builder /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

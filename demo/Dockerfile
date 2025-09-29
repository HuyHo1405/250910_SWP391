# Base image dùng Java 24
FROM eclipse-temurin:24-jdk-jammy

# Lấy file jar từ folder target
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port 8080
EXPOSE 8080

# Lệnh chạy jar khi container start
ENTRYPOINT ["java","-jar","/app.jar"]

# STAGE 1: BUILD STAGE
# Sử dụng một image có JDK và Maven/Gradle để build ứng dụng
FROM eclipse-temurin:24-jdk-jammy AS builder
LABEL maintainer="Your Name"

# Đặt thư mục làm việc
WORKDIR /app

# Copy các file cấu hình build
# Railway cần những file này để tải dependencies (pom.xml, build.gradle, settings.xml)
COPY pom.xml .
# Nếu bạn dùng Gradle, thay pom.xml bằng build.gradle và settings.gradle

# Tải dependencies (tận dụng Docker cache)
RUN mvn dependency:go-offline || true

# Copy toàn bộ mã nguồn
COPY . .

# Chạy lệnh package để tạo ra file JAR
RUN mvn clean package -DskipTests

# STAGE 2: RUNNING STAGE
# Sử dụng một image JRE nhẹ hơn để giảm kích thước image cuối cùng
FROM eclipse-temurin:24-jre-jammy AS runner

# Đặt thư mục làm việc
WORKDIR /app

# Expose port
EXPOSE 8080

# Lấy file JAR duy nhất từ stage builder (ví dụ: target/demo-0.0.1-SNAPSHOT.jar)
# và đổi tên thành app.jar trong image cuối cùng.
# Ký tự đại diện (*) được sử dụng để bắt tên file động.
COPY --from=builder /app/target/*.jar app.jar

# Lệnh chạy ứng dụng, sử dụng tên app.jar đã đổi tên
ENTRYPOINT ["java", "-jar", "/app.jar"]

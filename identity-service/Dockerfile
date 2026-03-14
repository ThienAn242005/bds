# Bước 1: Sử dụng JDK nhẹ để chạy ứng dụng
FROM eclipse-temurin:21-jdk-alpine

# Bước 2: Tạo thư mục làm việc trong container
WORKDIR /app

# Bước 3: Copy file .jar đã build từ thư mục target vào container
# Lưu ý: identity-service-1.0.0.jar là tên file tạo ra sau khi mvn install
COPY target/identity-service-1.0.0.jar app.jar

# Bước 4: Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]
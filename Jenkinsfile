pipeline {
    agent any

    // Định nghĩa các công cụ cần dùng
    tools {
        maven 'maven-3.9.14' // Tên này phải TRÙNG với tên bạn đặt trong Jenkins Global Tool Configuration
    }

    stages {
        stage('1. Build Artifact') {
            steps {
                echo 'Đang thực hiện Build Maven cho toàn bộ dự án HomeVerse...'
                // Chạy lệnh build của Windows (dùng bat thay vì sh nếu Jenkins chạy trên Windows trực tiếp)
                // Nhưng vì mình chạy Jenkins trong Docker (Linux), ta dùng sh
                sh 'mvn clean install -DskipTests'
            }
        }

        stage('2. Build & Tag Docker Image') {
            steps {
                echo 'Đang đóng gói Identity Service thành Docker Image...'
                // Build image từ Dockerfile trong module identity-service
                sh 'docker build -t homeverse-identity:latest ./identity-service'
            }
        }

        stage('3. Run System') {
            steps {
                echo 'Đang khởi chạy hệ thống bằng Docker Compose...'
                // Chạy lệnh docker-compose để bật các service lên
                sh 'docker-compose up -d'
            }
        }
    }

    post {
        success {
            echo 'Chúc mừng! Pipeline đã chạy thành công rực rỡ.'
        }
        failure {
            echo 'Ôi hỏng rồi! Kiểm tra lại log để sửa lỗi nhé.'
        }
    }
}
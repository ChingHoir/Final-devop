# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage with NGINX + Spring Boot + PHP + SSH
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/id-card-management-1.0.0.jar app.jar

# Install NGINX, PHP, and SSH server, plus mysql client for health checks
RUN apt-get update && apt-get install -y \
    nginx \
    php \
    openssh-server \
    default-mysql-client \
    && rm -rf /var/lib/apt/lists/*

# Copy NGINX configuration
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# Setup SSH
RUN mkdir -p /var/run/sshd && \
    echo "root:Hello@123" | chpasswd && \
    sed -i 's/#Port 22/Port 22/' /etc/ssh/sshd_config || true && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config || true

# Expose ports: 8080 (NGINX), 8081 (Spring Boot), 22 (SSH)
EXPOSE 8080 8081 22

# Start SSH, NGINX, then wait for MySQL on 127.0.0.1:3306, then start Spring Boot
CMD service ssh start && service nginx start && \
    echo "Waiting for MySQL on 127.0.0.1:3306..." && \
    while ! mysqladmin ping -h"127.0.0.1" -P"3306" -u"root" -p"Hello@123" --silent 2>/dev/null; do \
      sleep 2; \
    done && \
    echo "MySQL is ready!" && \
    java -jar app.jar
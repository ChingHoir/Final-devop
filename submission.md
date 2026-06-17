# DevOps Exam Submission - ID Card Management System (Kubernetes)

## Student Information
- **Name:** HUY Chanchhinghoir
- **ID:** 20221469
- **Group:** B
- **Date:** June 17, 2026

## Repository Information
- **GitHub URL:** https://github.com/ChingHoir/Final-devop.git
- **Branch:** main
- **Latest Commit:** `6145e98`

---

## Task 1: Kubernetes Configuration (CHOICE_B - Kubernetes Pod)

### YAML File Created
**File:** `id-card-management/kubernetes/minikube-deployment.yaml`

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: e20221469-pod
  labels:
    app: e20221469-id-card
spec:
  containers:
  # Container 1: Web Server (JDK 21 + NGINX + Spring Boot + PHP + SSH)
  - name: web-server
    image: id-card-app:latest
    imagePullPolicy: Never
    ports:
    - containerPort: 8080
      name: nginx
    - containerPort: 8081
      name: springboot
    - containerPort: 22
      name: ssh
    env:
    - name: SERVER_PORT
      value: "8081"
    - name: SPRING_DATASOURCE_URL
      value: "jdbc:mysql://127.0.0.1:3306/B-Huy_Chanchhinghoir-db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    - name: SPRING_DATASOURCE_USERNAME
      value: "root"
    - name: SPRING_DATASOURCE_PASSWORD
      value: "Hello@123"
    - name: SPRING_JPA_HIBERNATE_DDL_AUTO
      value: "update"
    - name: SPRING_JPA_SHOW_SQL
      value: "true"

  # Container 2: MySQL Database
  - name: mysql
    image: mysql:8.0
    ports:
    - containerPort: 3306
    env:
    - name: MYSQL_ROOT_PASSWORD
      value: "Hello@123"
    - name: MYSQL_DATABASE
      value: "B-Huy_Chanchhinghoir-db"
    - name: MYSQL_CHARACTER_SET_SERVER
      value: "utf8mb4"
    - name: MYSQL_COLLATION_SERVER
      value: "utf8mb4_unicode_ci"
---
apiVersion: v1
kind: Service
metadata:
  name: e20221469-service
spec:
  selector:
    app: e20221469-id-card
  ports:
  - name: website
    port: 8443
    targetPort: 8080
    nodePort: 30843
  - name: ssh
    port: 2222
    targetPort: 22
    nodePort: 30222
  type: NodePort
```

### Pod Configuration Summary
| Item | Value |
|------|-------|
| **Pod Name** | `e20221469-pod` |
| **Web Container** | `web-server` (JDK 21 + NGINX + Spring Boot + PHP + SSH) |
| **DB Container** | `mysql` (MySQL 8.0) |
| **Database Name** | `B-Huy_Chanchhinghoir-db` |
| **DB User/Password** | `root` / `Hello@123` |
| **NGINX Port** | 8080 (exposed as 8443 via Service) |
| **SSH Port** | 22 (exposed as 2222 via Service) |
| **Website NodePort** | 30843 |
| **SSH NodePort** | 30222 |

### Verification Commands & Output
```bash
# Check running pods
kubectl get pods
NAME            READY   STATUS    RESTARTS   AGE
e20221469-pod   2/2     Running   0          2m26s

# Check services
kubectl get svc
NAME                        TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                         AGE
e20221469-service           NodePort    10.108.173.248   <none>        8443:30843/TCP,2222:30222/TCP   2m26s
kubernetes                  ClusterIP   10.96.0.1        <none>        443/TCP                         49m
```

---

## Task 2: Web Container - PHP Modules Output

### Command Executed
```bash
kubectl exec e20221469-pod -c web-server -- php -m
```

### Output (saved to `php-modules.txt`)
```
[PHP Modules]
calendar
Core
ctype
date
exif
FFI
fileinfo
filter
ftp
gettext
hash
iconv
json
lexbor
libxml
openssl
pcntl
pcre
PDO
Phar
posix
random
readline
Reflection
session
shmop
sockets
sodium
SPL
standard
sysvmsg
sysvsem
sysvshm
tokenizer
uri
Zend OPcache
zlib

[Zend Modules]
Zend OPcache
```

**File location:** `php-modules.txt` (repository root)

---

## Task 3: MySQL Container - Show Tables Output

### Command Executed
```bash
kubectl exec e20221469-pod -c mysql -- mysql -u root -pHello@123 B-Huy_Chanchhinghoir-db -e "show tables;"
```

### Output (saved to `mysql-tables.txt`)
```
Tables_in_B-Huy_Chanchhinghoir-db
profiles
templates
```

**File location:** `mysql-tables.txt` (repository root)

---

## Task 4: Git Commands

```bash
# Add Kubernetes YAML file
git add id-card-management/kubernetes/minikube-deployment.yaml

# Add PHP modules output
git add php-modules.txt

# Add MySQL tables output
git add mysql-tables.txt

# Add Dockerfile and nginx config
git add id-card-management/Dockerfile id-card-management/nginx/nginx.conf

# Commit all files
git commit -m "add Kubernetes deployment, PHP modules, MySQL tables, and configuration files"

# Push to GitHub
git push origin main
```

### Commit Details
- **Commit Hash:** `6145e98`
- **Commit Message:** "add Kubernetes deployment, PHP modules, MySQL tables, and configuration files"

---

## Dockerfile Configuration

**File:** `id-card-management/Dockerfile`

The Dockerfile uses a multi-stage build:
1. **Build stage**: Maven 3.9 + JDK 21 to compile the Spring Boot application
2. **Runtime stage**: JDK 21 JRE with:
   - NGINX (web server, port 8080, proxies to Spring Boot on 8081)
   - PHP (for `php -m` command)
   - OpenSSH Server (port 22, root user with password `Hello@123`)
   - MySQL client (for waiting until MySQL is ready)

### Docker Build Command
```bash
cd id-card-management
minikube -p minikube docker-env --shell powershell | Invoke-Expression
docker build -t id-card-app:latest .
```

---

## NGINX Configuration

**File:** `id-card-management/nginx/nginx.conf`

NGINX listens on port 8080 and proxies all requests to Spring Boot running on port 8081:
```nginx
upstream springboot {
    server 127.0.0.1:8081;
}

server {
    listen 8080;
    server_name localhost;

    location / {
        proxy_pass http://springboot;
        ...
    }
}
```

---

## Submission Checklist

- [x] Kubernetes Pod YAML file created (`minikube-deployment.yaml`)
- [x] Kubernetes Service YAML file created (same file)
- [x] YAML file committed to GitHub
- [x] PHP modules saved to `php-modules.txt` at repo root
- [x] `php-modules.txt` committed to GitHub
- [x] MySQL tables saved to `mysql-tables.txt` at repo root
- [x] `mysql-tables.txt` committed to GitHub
- [x] Docker image built successfully
- [x] Pod is running with 2/2 containers
- [x] All files pushed to GitHub repository
- [x] Repository URL: https://github.com/ChingHoir/Final-devop.git

---

## Application Access

| Service | URL/Command |
|---------|-------------|
| **Website** | http://localhost:30843 (via NodePort) |
| **SSH Access** | `ssh root@localhost -p 30222` (password: Hello@123) |
| **Database** | `B-Huy_Chanchhinghoir-db` on 127.0.0.1:3306 |

---

**Date:** June 17, 2026  
**Submitted by:** HUY Chanchhinghoir (ID: 20221469 - Group B)
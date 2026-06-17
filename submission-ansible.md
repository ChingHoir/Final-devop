# DevOps Exam Submission - Ansible Automation (Kubernetes Pod Stage)

## Student Information
- **Name:** HUY Chanchhinghoir
- **ID:** 20221469
- **Group:** B
- **Date:** June 18, 2026

## Repository Information
- **GitHub URL:** https://github.com/ChingHoir/Final-devop.git
- **Branch:** main
- **Latest Commit:** `8996539`

---

## Infrastructure Overview

### Kubernetes Pod: `e20221469-pod` (2/2 Running)

**Container 1: `web-server`** (id-card-app:latest)
- JDK 21 + NGINX (port 8080) + Spring Boot (port 8081) + SSH (port 22) + PHP + MySQL client + Maven + Git

**Container 2: `mysql`** (mysql:8.0)
- Database: `B-Huy_Chanchhinghoir-db`
- User: `root` / Password: `Hello@123`

---

## Task 1: Access Web Server & Git Pull

### Method: Ansible Playbook (SSH via port-forward)

**Command:**
```bash
kubectl port-forward --address 0.0.0.0 pod/e20221469-pod 2222:22
ansible-playbook -i inventory ansible-playbook.yml
```

**Result:**
```
cd /root/project && git pull
Already up to date.
```

---

## Task 2: Build with Maven

**Command executed inside web container:**
```bash
cd /root/project/id-card-management && mvn clean package -DskipTests -q
```

**Result:**
```
[INFO] BUILD SUCCESS
```
**JAR:** `target/id-card-management-1.0.0.jar` (70MB)

---

## Task 3: Run Tests with SQLite (Test DB) + MySQL (Prod DB)

### Test Profile Configuration (`application-test.properties`):
```properties
spring.datasource.url=jdbc:sqlite::memory:?cache=shared
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

### Production Configuration (`application.properties`):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/B-Huy_Chanchhinghoir-db
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Command:
```bash
unset SPRING_DATASOURCE_URL && mvn test -Dspring.profiles.active=test
```

### Result:
```
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```
**Test DB:** ✅ SQLite (in-memory)  
**Prod DB:** ✅ MySQL (`B-Huy_Chanchhinghoir-db`)

---

## Task 4: Backup MySQL Database

### Command:
```bash
mysqldump -h 127.0.0.1 -u root -pHello@123 B-Huy_Chanchhinghoir-db > mysql-backup.sql
```

### Result:
- **File:** `mysql-backup.sql` (4.2KB)
- **Content:** Full MySQL dump of `B-Huy_Chanchhinghoir-db`
- **Tables:** `profiles`, `templates`

---

## Ansible Playbook (`ansible-playbook.yml`)

```yaml
- name: Login to Web Server and Perform Tasks
  hosts: webserver
  tasks:
    - name: Git pull (stash local changes first)
      shell: |
        cd /root/project && git stash --include-untracked && git pull
      
    - name: Maven build
      shell: |
        cd /root/project/id-card-management && mvn clean package -DskipTests -q
    
    - name: Run tests with SQLite
      shell: |
        cd /root/project/id-card-management && unset SPRING_DATASOURCE_URL && mvn test
      environment:
        SPRING_PROFILES_ACTIVE: test
    
    - name: Backup MySQL database
      shell: |
        mysqldump -h 127.0.0.1 -u root -pHello@123 B-Huy_Chanchhinghoir-db > mysql-backup.sql
```

## Inventory (`inventory`)

```
[webserver]
localhost ansible_port=2222 ansible_user=root ansible_password=Hello@123 ansible_connection=ssh
```

---

## Application Access

| Service | Method | URL |
|---------|--------|-----|
| **Website** | Port-forward | `kubectl port-forward --address 0.0.0.0 pod/e20221469-pod 8081:8081` → http://127.0.0.1:8081/ or http://192.168.2.211:8081/ (same WiFi) |
| **SSH** | Port-forward | `kubectl port-forward --address 0.0.0.0 pod/e20221469-pod 2222:22` → `ssh root@127.0.0.1 -p 2222` (password: Hello@123) |
| **Database** | Internal | `B-Huy_Chanchhinghoir-db` on 127.0.0.1:3306 (within pod) |

---

## Files in Repository (Root Level)

| File | Description |
|------|-------------|
| `ansible-playbook.yml` | Ansible playbook for automation |
| `inventory` | SSH inventory for web server |
| `mysql-backup.sql` | Database backup of `B-Huy_Chanchhinghoir-db` |
| `php-modules.txt` | PHP modules from web container |
| `mysql-tables.txt` | MySQL tables list |
| `submission.md` | Kubernetes pod submission |
| `submission-ansible.md` | This submission file |
| `id-card-management/kubernetes/minikube-deployment.yaml` | Kubernetes pod + service YAML |
| `id-card-management/src/test/resources/application-test.properties` | SQLite test configuration |

---

## Submission Checklist

- [x] Kubernetes Pod created with 2 containers (web-server + mysql)
- [x] Database name: `B-Huy_Chanchhinghoir-db` ✓
- [x] Git pull executed inside web container
- [x] Maven build successful
- [x] Tests run with SQLite (test DB) — BUILD SUCCESS
- [x] Production uses MySQL (prod DB)
- [x] MySQL database backed up to `mysql-backup.sql`
- [x] Ansible playbook created & committed
- [x] All files pushed to GitHub

---

**Date:** June 18, 2026  
**Submitted by:** HUY Chanchhinghoir (ID: 20221469 - Group B)  
**Repository:** https://github.com/ChingHoir/Final-devop.git
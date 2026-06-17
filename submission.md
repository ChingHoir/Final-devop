# DevOps Exam Submission - ID Card Management System (Kubernetes Pod)

## Student Information
- **Name:** HUY Chanchhinghoir
- **ID:** 20221469
- **Group:** B
- **Date:** June 18, 2026

## Repository Information
- **GitHub URL:** https://github.com/ChingHoir/Final-devop.git
- **Branch:** main
- **Latest Commit:** `2813f76`

---

## Task 1: Kubernetes Pod with 2 Containers (CHOICE_B)

### YAML File Created
**File:** `id-card-management/kubernetes/minikube-deployment.yaml`

### Pod: `e20221469-pod`

#### Container 1: `web-server` (image: id-card-app:latest)
| Component | Technology |
|-----------|-----------|
| Runtime | JDK 21+ (Eclipse Temurin) |
| Web Server | NGINX (port 8080 → proxy to Spring Boot on 8081) |
| Application | Spring Boot (port 8081) |
| SSH Server | OpenSSH (port 22, root:Hello@123) |

#### Container 2: `mysql` (image: mysql:8.0)
| Setting | Value |
|---------|-------|
| Database Name | `B-Huy_Chanchhinghoir-db` |
| User | `root` |
| Password | `Hello@123` |

### Service: `e20221469-service` (NodePort)
| Port | NodePort | Purpose |
|------|----------|---------|
| 8443 | 30843 | Website (NGINX) |
| 8081 | 30881 | Spring Boot direct |
| 2222 | 30222 | SSH |
| 3306 | 30306 | MySQL |

### Verification
```bash
kubectl get pod
NAME            READY   STATUS    RESTARTS   AGE
e20221469-pod   2/2     Running   2          5h

kubectl get svc
NAME                TYPE       CLUSTER-IP      PORT(S)
e20221469-service   NodePort   10.108.67.143   8443:30843,8081:30881,2222:30222,3306:30306
```

---

## Task 2: Web Container - PHP Modules

```bash
kubectl exec e20221469-pod -c web-server -- php -m
```

**Output saved to:** `php-modules.txt` (repository root)

Modules: calendar, Core, ctype, date, exif, FFI, fileinfo, filter, ftp, gettext, hash, iconv, json, lexbor, libxml, openssl, pcntl, pcre, PDO, Phar, posix, random, readline, Reflection, session, shmop, sockets, sodium, SPL, standard, sysvmsg, sysvsem, sysvshm, tokenizer, uri, Zend OPcache, zlib

---

## Task 3: MySQL Container - Show Tables

```bash
kubectl exec e20221469-pod -c mysql -- sh -c "echo 'show tables;' | mysql -u root -pHello@123 B-Huy_Chanchhinghoir-db"
```

**Output saved to:** `mysql-tables.txt` (repository root)
```
Tables_in_B-Huy_Chanchhinghoir-db
profiles
templates
```

---

## Task 4: Git Commands

```bash
# Add Kubernetes YAML file
git add id-card-management/kubernetes/minikube-deployment.yaml

# Add PHP modules output
git add php-modules.txt

# Add MySQL tables output
git add mysql-tables.txt

# Commit all files
git commit -m "Add Kubernetes pod YAML, PHP modules list, and MySQL tables list"

# Push to GitHub
git push origin HEAD
```

- **Commit Hash:** `2813f76`
- **Commit Message:** "Add Kubernetes pod YAML, PHP modules list, and MySQL tables list"

---

## Application Access

| Service | URL/Command |
|---------|-------------|
| **Website (via port-forward)** | `kubectl port-forward pod/e20221469-pod 8081:8081` → http://127.0.0.1:8081/ |
| **SSH Access** | `ssh root@localhost -p 30222` (password: Hello@123) |
| **Database** | `B-Huy_Chanchhinghoir-db` on port 3306 |

---

## Submission Checklist

- [x] Kubernetes Pod YAML file created (`minikube-deployment.yaml`)
- [x] Kubernetes Service YAML file created (same file)
- [x] Pod has 2 containers: web-server + mysql
- [x] Database name: `B-Huy_Chanchhinghoir-db` (B-Group + Huy_Chanchhinghoir)
- [x] YAML file committed to GitHub
- [x] PHP modules saved to `php-modules.txt` at repo root
- [x] MySQL tables saved to `mysql-tables.txt` at repo root
- [x] All files committed and pushed to GitHub
- [x] Repository URL: https://github.com/ChingHoir/Final-devop.git

---

**Date:** June 18, 2026  
**Submitted by:** HUY Chanchhinghoir (ID: 20221469 - Group B)
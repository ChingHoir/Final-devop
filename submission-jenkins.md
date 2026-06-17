# DevOps Exam Submission - Jenkins CI/CD Pipeline

## Student Information
- **Name:** HUY Chanchhinghoir
- **ID:** 20221469
- **Group:** B
- **Date:** June 18, 2026

## Repository Information
- **GitHub URL:** https://github.com/ChingHoir/Final-devop.git
- **Branch:** main
- **Latest Commit:** `7bca6db`

---

## Jenkins Pipeline Overview

**File:** `Jenkinsfile` (project root)

### Pipeline Triggers
- **Poll SCM:** Every 5 minutes (`H/5 * * * *`)

### Pipeline Stages

```
┌─────────────────────────────┐
│  1. Checkout (SCM)          │ ← From GitHub
├─────────────────────────────┤
│  2. Build with Maven        │ ← mvn clean package -DskipTests
│     └─ On Failure → Email   │   → srengty@gmail.com + developer
├─────────────────────────────┤
│  3. Run Tests (SQLite)      │ ← mvn test -Dspring.profiles.active=test
│     └─ On Failure → Email   │   → srengty@gmail.com + developer
├─────────────────────────────┤
│  4. Deploy via Ansible      │ ← ansible-playbook + kubectl port-forward
│     └─ On Failure → Email   │   → srengty@gmail.com
├─────────────────────────────┤
│  Post: On Failure → Email   │ → srengty@gmail.com + developer who committed
└─────────────────────────────┘
```

---

## Stage 1: Checkout
```groovy
stage('Checkout') {
    steps {
        checkout scm
    }
}
```
- Polls GitHub every 5 minutes
- Auto-detects new commits

---

## Stage 2: Build with Maven

```groovy
stage('Build with Maven') {
    steps {
        dir("${PROJECT_DIR}") {
            sh 'mvn clean package -DskipTests -q'
        }
    }
    post {
        failure {
            script {
                def devEmail = sh(script: 'git log -1 --format="%ae"', returnStdout: true).trim()
                mail(
                    to: "${NOTIFY_EMAIL}, ${devEmail}",
                    subject: "Jenkins Build FAILED - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "..."
                )
            }
        }
    }
}
```

**On Failure:** Emails **srengty@gmail.com** AND the developer who made the commit (`git log -1 --format="%ae"`)

---

## Stage 3: Run Tests with SQLite

```groovy
stage('Run Tests (SQLite)') {
    steps {
        dir("${PROJECT_DIR}") {
            sh 'mvn test -Dspring.profiles.active=test'
        }
    }
    post {
        failure {
            // Emails srengty@gmail.com + developer
        }
    }
}
```

- Tests use **SQLite in-memory** database
- Production uses **MySQL** (`B-Huy_Chanchhinghoir-db`)

---

## Stage 4: Deploy via Ansible

```groovy
stage('Setup Port-Forward & Deploy via Ansible') {
    steps {
        script {
            sh '''
                kubectl port-forward --address 0.0.0.0 pod/e20221469-pod 2222:22 &
                sleep 3
                ansible-playbook -i "${ANSIBLE_INVENTORY}" "${ANSIBLE_PLAYBOOK}"
            '''
        }
    }
    post {
        failure {
            mail(
                to: "${NOTIFY_EMAIL}",
                subject: "Jenkins Ansible Deploy FAILED - ...",
                body: "..."
            )
        }
    }
}
```

Deploy steps:
1. Start `kubectl port-forward` to SSH into web container
2. Execute Ansible playbook which:
   - ✅ Git pull latest code
   - ✅ Build with Maven
   - ✅ Run tests with SQLite
   - ✅ Backup MySQL database

---

## Email Notification System

| Trigger | Recipients |
|---------|------------|
| Build failed | `srengty@gmail.com` + developer who committed |
| Tests failed | `srengty@gmail.com` + developer who committed |
| Ansible deploy failed | `srengty@gmail.com` |
| Any stage failed | `srengty@gmail.com` + developer who committed |

Developer email is dynamically retrieved using: `git log -1 --format="%ae"`

---

## Jenkins Setup Instructions

### 1. Prerequisites on Jenkins Machine
- Jenkins with: Git plugin, Pipeline plugin, Email Extension plugin
- Tools: JDK 21+, Maven, Ansible, kubectl (with minikube context)
- Kubernetes pod `e20221469-pod` must be running: `kubectl get pod`

### 2. Configure Jenkins Job
1. **New Item** → **Pipeline**
2. **GitHub project**: https://github.com/ChingHoir/Final-devop.git
3. **Poll SCM**: `H/5 * * * *`
4. **Pipeline Definition**: Pipeline script from SCM
5. **SCM**: Git → Repository URL: https://github.com/ChingHoir/Final-devop.git
6. **Script Path**: `Jenkinsfile`

### 3. Configure Email in Jenkins
- **System Admin email**: your-email@example.com
- **SMTP server**: (configure your SMTP)
- Pipeline uses: `mail(to: ...)` step

### 4. Required Environment Variables
| Variable | Value |
|----------|-------|
| `NOTIFY_EMAIL` | srengty@gmail.com |
| `DB_NAME` | B-Huy_Chanchhinghoir-db |
| `DB_USER` | root |
| `DB_PASSWORD` | Hello@123 |

---

## Files in Repository

| File | Description |
|------|-------------|
| `Jenkinsfile` | ✅ Jenkins CI/CD pipeline definition |
| `ansible-playbook.yml` | ✅ Ansible playbook for deployment |
| `inventory` | ✅ SSH inventory for web server |
| `id-card-management/kubernetes/minikube-deployment.yaml` | ✅ Kubernetes pod YAML |
| `mysql-backup.sql` | ✅ Database backup |
| `php-modules.txt` | ✅ PHP modules |
| `mysql-tables.txt` | ✅ MySQL tables list |
| `submission.md` | ✅ Kubernetes submission |
| `submission-ansible.md` | ✅ Ansible submission |
| `submission-jenkins.md` | ✅ This submission file |

---

## Submission Checklist

- [x] Jenkinsfile created at project root
- [x] Poll SCM every 5 minutes: `H/5 * * * *`
- [x] Auto build with Maven on code change
- [x] Run tests with SQLite (test DB)
- [x] Production uses MySQL (prod DB)
- [x] Email notification on build error → `srengty@gmail.com`
- [x] Email notification to developer who committed the error
- [x] After build+test success → deploy via Ansible
- [x] Jenkinsfile committed and pushed to GitHub

---

**Date:** June 18, 2026  
**Submitted by:** HUY Chanchhinghoir (ID: 20221469 - Group B)  
**Repository:** https://github.com/ChingHoir/Final-devop.git
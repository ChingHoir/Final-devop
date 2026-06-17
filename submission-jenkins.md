# DevOps Exam Submission - Jenkins CI/CD Pipeline

## Student Information
- **Name:** HUY Chanchhinghoir
- **ID:** 20221469
- **Group:** B
- **Date:** June 18, 2026

## Repository Information
- **GitHub URL:** https://github.com/ChingHoir/Final-devop.git
- **Branch:** main
- **Latest Commit:** `b55b4db`

---

## 1. Jenkins Pipeline Script (Jenkinsfile)

**File location:** `Jenkinsfile` (project root)

```groovy
pipeline {
    agent any

    triggers {
        pollSCM('H/5 * * * *')
    }

    environment {
        PROJECT_DIR = "${WORKSPACE}/id-card-management"
        ANSIBLE_INVENTORY = "${WORKSPACE}/inventory"
        ANSIBLE_PLAYBOOK = "${WORKSPACE}/ansible-playbook.yml"
        DB_NAME = "B-Huy_Chanchhinghoir-db"
        DB_USER = "root"
        DB_PASSWORD = "Hello@123"
        NOTIFY_EMAIL = "srengty@gmail.com"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build with Maven') {
            steps {
                dir("${PROJECT_DIR}") {
                    sh 'mvn clean package -DskipTests -q'
                }
            }
            post {
                failure {
                    script {
                        def devEmail = sh(
                            script: 'git log -1 --format="%ae"',
                            returnStdout: true
                        ).trim()
                        mail(
                            to: "${NOTIFY_EMAIL}, ${devEmail}",
                            subject: "Jenkins Build FAILED - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                            body: "The Maven build has failed.\n\nProject: ${env.JOB_NAME}\nBuild #: ${env.BUILD_NUMBER}\nURL: ${env.BUILD_URL}\n\nPlease check the build log for details."
                        )
                    }
                }
            }
        }

        stage('Run Tests (SQLite)') {
            steps {
                dir("${PROJECT_DIR}") {
                    sh 'mvn test -Dspring.profiles.active=test'
                }
            }
            post {
                failure {
                    script {
                        def devEmail = sh(
                            script: 'git log -1 --format="%ae"',
                            returnStdout: true
                        ).trim()
                        mail(
                            to: "${NOTIFY_EMAIL}, ${devEmail}",
                            subject: "Jenkins Tests FAILED - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                            body: "The test execution has failed.\n\nProject: ${env.JOB_NAME}\nBuild #: ${env.BUILD_NUMBER}\nURL: ${env.BUILD_URL}\n\nFailed tests detected. Please check the test report."
                        )
                    }
                }
            }
        }

        stage('Setup Port-Forward & Deploy via Ansible') {
            when {
                allOf {
                    expression { return fileExists(ANSIBLE_PLAYBOOK) }
                    expression { return fileExists(ANSIBLE_INVENTORY) }
                }
            }
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
                    script {
                        mail(
                            to: "${NOTIFY_EMAIL}",
                            subject: "Jenkins Ansible Deploy FAILED - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                            body: "The Ansible deployment has failed.\n\nProject: ${env.JOB_NAME}\nBuild #: ${env.BUILD_NUMBER}\nURL: ${env.BUILD_URL}\n\nPlease check the deployment logs."
                        )
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Build, tests, and deployment completed successfully!"
        }
        failure {
            script {
                def devEmail = sh(
                    script: 'git log -1 --format="%ae"',
                    returnStdout: true
                ).trim()
                mail(
                    to: "${NOTIFY_EMAIL}, ${devEmail}",
                    subject: "Jenkins Pipeline FAILED - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body: "The pipeline has failed.\n\nProject: ${env.JOB_NAME}\nBuild #: ${env.BUILD_NUMBER}\nURL: ${env.BUILD_URL}\n\nPlease check the build log for details."
                )
            }
        }
    }
}
```

---

## 2. Build Output (Maven Build Result)

**Command executed:**
```bash
mvn clean package -DskipTests
```

**Output:**
```
[INFO] Scanning for projects...
[INFO] -------------------< com.example:id-card-management >-------------------
[INFO] Building ID Card Management System 1.0.0
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- resources:3.3.1:resources (default-resources) @ id-card-management ---
[INFO] Copying 2 resources from src/main/resources to target/classes
[INFO] Copying 6 resources from src/main/resources to target/classes
[INFO] 
[INFO] --- compiler:3.11.0:compile (default-compile) @ id-card-management ---
[INFO] Nothing to compile - all classes are up to date
[INFO] 
[INFO] --- resources:3.3.1:testResources (default-testResources) @ id-card-management ---
[INFO] Copying 2 resources from src/test/resources to target/test-classes
[INFO] 
[INFO] --- compiler:3.11.0:testCompile (default-testCompile) @ id-card-management ---
[INFO] Changes detected - recompiling the module! :dependency
[INFO] Compiling 1 source file with javac [debug release 17] to target/test-classes
[INFO] 
[INFO] --- surefire:3.0.0:test (default-test) @ id-card-management ---
[INFO] Tests are skipped.
[INFO] 
[INFO] --- jar:3.3.0:jar (default-jar) @ id-card-management ---
[INFO] Building jar: /root/project/id-card-management/target/id-card-management-1.0.0.jar
[INFO] 
[INFO] --- spring-boot:3.1.5:repackage (repackage) @ id-card-management ---
[INFO] Replacing main artifact ... with repackaged archive, adding nested dependencies
[INFO] The original artifact has been renamed to ...jar.original
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.346 s
[INFO] Finished at: 2026-06-17T18:49:00Z
[INFO] ------------------------------------------------------------------------
```

---

## 3. Test Output (SQLite Tests)

**Command executed:**
```bash
mvn test -Dspring.profiles.active=test
```

**Output:**
```
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**Verified connection:**
- HikariPool-1 - Added connection **org.sqlite.jdbc4.JDBC4Connection** ✅ (SQLite)
- Active profile: **"test"** ✅

---

## 4. Pipeline Architecture

```
                    ┌─────────────────────────────────┐
                    │  GitHub Repository               │
                    │  https://github.com/             │
                    │  ChingHoir/Final-devop.git       │
                    └────────────┬────────────────────┘
                                 │ Poll SCM every 5 min
                                 ▼
                    ┌─────────────────────────────────┐
                    │  Stage 1: Checkout (SCM)        │
                    └────────────┬────────────────────┘
                                 ▼
                    ┌─────────────────────────────────┐
                    │  Stage 2: Build with Maven      │
                    │  mvn clean package -DskipTests  │
                    ├─────────────────────────────────┤
                    │  ON FAILURE → Email:            │
                    │  srengty@gmail.com + developer  │
                    └────────────┬────────────────────┘
                                 ▼
                    ┌─────────────────────────────────┐
                    │  Stage 3: Run Tests (SQLite)    │
                    │  mvn test -Dspring.profiles     │
                    │         .active=test            │
                    ├─────────────────────────────────┤
                    │  ON FAILURE → Email:            │
                    │  srengty@gmail.com + developer  │
                    └────────────┬────────────────────┘
                                 ▼
                    ┌─────────────────────────────────┐
                    │  Stage 4: Deploy via Ansible    │
                    │  kubectl port-forward +          │
                    │  ansible-playbook               │
                    ├─────────────────────────────────┤
                    │  ON FAILURE → Email:            │
                    │  srengty@gmail.com              │
                    └────────────┬────────────────────┘
                                 ▼
                    ┌─────────────────────────────────┐
                    │  POST: SUCCESS → echo complete  │
                    │  POST: FAILURE → Email:         │
                    │  srengty@gmail.com + developer  │
                    └─────────────────────────────────┘
```

---

## 5. Email Notification System

| Trigger | Recipients | Method |
|---------|------------|--------|
| Build failed | `srengty@gmail.com` + developer | `git log -1 --format="%ae"` |
| Tests failed | `srengty@gmail.com` + developer | `git log -1 --format="%ae"` |
| Ansible deploy failed | `srengty@gmail.com` | - |
| Any stage failed | `srengty@gmail.com` + developer | `git log -1 --format="%ae"` |

---

## 6. Jenkins Setup Instructions

### Prerequisites on Jenkins Machine:
- Jenkins with: **Git plugin**, **Pipeline plugin**, **Email Extension plugin**
- **JDK 21+** installed and configured
- **Maven** installed
- **Ansible** installed (`pip install ansible`)
- **kubectl** installed with minikube context
- Kubernetes pod running: `kubectl get pod e20221469-pod`

### Configure Jenkins Job:
1. **Dashboard** → **New Item** → **Pipeline**
2. Name: `id-card-management-pipeline`
3. **Poll SCM**: `H/5 * * * *`
4. **Pipeline** → **Pipeline script from SCM**
5. **SCM**: Git
6. **Repository URL**: `https://github.com/ChingHoir/Final-devop.git`
7. **Script Path**: `Jenkinsfile`

### Configure Email:
1. **Manage Jenkins** → **Configure System**
2. **E-mail Notification** → SMTP server (e.g., `smtp.gmail.com:587`)
3. **Extended E-mail Notification** → configure credentials

---

## Submission Checklist

- [x] Jenkinsfile created at project root
- [x] Poll SCM every 5 minutes: `H/5 * * * *`
- [x] Auto build with Maven on code change → **BUILD SUCCESS**
- [x] Run tests with SQLite (test DB) → **BUILD SUCCESS** (1 test, 0 failures)
- [x] Production uses MySQL (prod DB)
- [x] Email notification on build error → `srengty@gmail.com`
- [x] Email notification to developer who committed the error
- [x] After build+test success → deploy via Ansible
- [x] Build output saved and included in report (file: `build-output.txt`)
- [x] Jenkins pipeline script included in report
- [x] Jenkinsfile committed and pushed to GitHub (commit: `b55b4db`)

---

**Date:** June 18, 2026  
**Submitted by:** HUY Chanchhinghoir (ID: 20221469 - Group B)  
**Repository:** https://github.com/ChingHoir/Final-devop.git
# DevOps Exam Submission - GitHub Actions CI/CD Pipeline

## Student Information
- **Name:** HUY Chanchhinghoir
- **ID:** 20221469
- **Group:** B
- **Date:** June 18, 2026

## Repository Information
- **GitHub URL:** https://github.com/ChingHoir/Final-devop.git
- **Branch:** main
- **Latest Commit:** `52cfc71`

---

## GitHub Actions Workflow

### Workflow File
**File:** `.github/workflows/ci.yml`

---

## Workflow Explanation

### Trigger: Every Commit
```yaml
on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]
```
- Runs automatically on **every push/commit** to `main` or `master` branches
- Also runs on pull requests targeting these branches

---

## Jobs Breakdown

### Job 1: `build-and-test`
| Step | Action | Description |
|------|--------|-------------|
| Checkout | `actions/checkout@v4` | Pulls latest code with git history (depth=2) |
| Setup JDK 21 | `actions/setup-java@v4` | Installs JDK 21 (Temurin) with Maven cache |
| Build | `./mvnw clean package -DskipTests -q` | Compiles and packages JAR (skips tests for speed) |
| Test | `./mvnw test -Dspring.profiles.active=test` | Runs tests using **SQLite** in-memory database |
| Upload Artifact | `actions/upload-artifact@v4` | Saves the JAR as a downloadable artifact |

### Job 2: `notify-build-failure` (runs only if build-and-test fails)
| Step | Action | Description |
|------|--------|-------------|
| Checkout | `actions/checkout@v4` | Pulls code with full history |
| Get Author | `git log -1 --format="%ae"` | Extracts the commit author's email |
| Send Email | `dawidd6/action-send-mail@v3` | Sends failure notification — **TO: srengty@gmail.com, CC: developer** |

### Job 3: `deploy` (runs only if build-and-test succeeds)
| Step | Action | Description |
|------|--------|-------------|
| Checkout | `actions/checkout@v4` | Pulls code again |
| Install Ansible | `sudo apt-get install -y ansible` | Installs Ansible on runner |
| Run Playbook | `ansible-playbook -i inventory ansible-playbook.yml` | Executes the Ansible playbook to deploy to web server |

### Job 4: `notify-deploy-failure` (runs only if deploy fails)
| Step | Action | Description |
|------|--------|-------------|
| Checkout | `actions/checkout@v4` | Pulls code with full history |
| Get Author | `git log -1 --format="%ae"` | Extracts the commit author's email |
| Send Email | `dawidd6/action-send-mail@v3` | Sends deploy failure notification — **TO: srengty@gmail.com, CC: developer** |

---

## Database Configuration

| Environment | Database | Driver |
|-------------|----------|--------|
| **Test** (GitHub Actions) | SQLite (in-memory) | `org.sqlite.JDBC` |
| **Production** (Ansible deploy) | MySQL | `com.mysql.cj.jdbc.Driver` |

### Test DB Config (`src/test/resources/application.properties`)
```properties
spring.datasource.url=jdbc:sqlite::memory:
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.properties.hibernate.dialect=org.hibernate.community.dialect.SQLiteDialect
```

### Production DB Config (`src/main/resources/application.properties`)
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/B-Huy_Chanchhinghoir-db
spring.datasource.username=root
spring.datasource.password=Hello@123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

---

## Email Notification Details

| Scenario | Recipients (To) | Recipients (CC) | Sent By |
|----------|----------------|------------------|---------|
| Build failure | `srengty@gmail.com` | Commit author email | GitHub Actions |
| Test failure | `srengty@gmail.com` | Commit author email | GitHub Actions |
| Deploy failure | `srengty@gmail.com` | Commit author email | GitHub Actions |

- The developer's email is extracted automatically: `git log -1 --format="%ae"`
- **Requires setup:** GitHub Secrets must be configured:
  - `MAIL_USERNAME` — Gmail address used to send email
  - `MAIL_PASSWORD` — Gmail App Password (not regular password)

---

## Pipeline Flow Diagram

```
[Git Push / Commit]
         │
         ▼
┌─ build-and-test ────────────────────────────────────────┐
│  1. Checkout code                                       │
│  2. Setup JDK 21                                        │
│  3. Build with Maven (./mvnw clean package -DskipTests)  │
│  4. Run Tests with SQLite (./mvnw test)                  │
│  5. Upload JAR artifact                                  │
└──────────────────────────┬──────────────────────────────┘
          │                            │
     SUCCESS                       FAILURE
          │                            │
          ▼                            ▼
┌─ deploy ──────────────┐   ┌─ notify-build-failure ─────────┐
│ 1. Install Ansible    │   │ 1. Get commit author email     │
│ 2. Run ansible-       │   │ 2. Send email:                 │
│    playbook.yml       │   │    TO: srengty@gmail.com       │
└────────┬──────────────┘   │    CC: commit author           │
    FAILURE                 └────────────────────────────────┘
         │
         ▼
┌─ notify-deploy-failure ───────────────────────────────────┐
│ 1. Get commit author email                                │
│ 2. Send email:                                            │
│    TO: srengty@gmail.com                                  │
│    CC: commit author                                      │
└───────────────────────────────────────────────────────────┘
```

---

## How to View GitHub Actions Results

1. Go to your repository on GitHub: https://github.com/ChingHoir/Final-devop.git
2. Click on the **Actions** tab
3. You will see the workflow runs listed — click on any run for details
4. Each run shows:
   - ✅ Green checkmark = Success (build → test → deploy)
   - ❌ Red X = Failure (email sent to srengty@gmail.com + developer)

---

## Required GitHub Secrets Setup

For email notifications to work, add these **GitHub Secrets** in:
`Settings → Secrets and variables → Actions → New repository secret`

| Secret | Value |
|--------|-------|
| `MAIL_USERNAME` | Your Gmail address (e.g., `yourname@gmail.com`) |
| `MAIL_PASSWORD` | Gmail App Password (not your regular password) |

**Note:** If email notifications are not configured, the pipeline will still run build, test, and deploy successfully — only the notification steps will be skipped.

---

## Files Created/Modified

| File | Description |
|------|-------------|
| `.github/workflows/ci.yml` | GitHub Actions workflow file (4 jobs: build, notify-build-failure, deploy, notify-deploy-failure) |
| `inventory` | Ansible inventory file (webserver on localhost) |
| `submission-github-actions.md` | This submission document |

---

## Full Workflow YAML

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

env:
  MAVEN_OPTS: -Dmaven.repo.local=${{ github.workspace }}/.m2/repository
  NOTIFY_EMAIL: srengty@gmail.com

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 2

      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        id: build
        run: |
          cd id-card-management
          ./mvnw clean package -DskipTests -q
        continue-on-error: true

      - name: Check build status
        if: steps.build.outcome == 'failure'
        run: |
          echo "Build failed!"
          exit 1

      - name: Run Tests (SQLite)
        id: test
        run: |
          cd id-card-management
          ./mvnw test -Dspring.profiles.active=test
        continue-on-error: true

      - name: Check test status
        if: steps.test.outcome == 'failure'
        run: |
          echo "Tests failed!"
          exit 1

      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: id-card-management-jar
          path: id-card-management/target/*.jar

  notify-build-failure:
    needs: build-and-test
    if: failure()
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Get commit author email
        id: get_author
        run: |
          AUTHOR_EMAIL=$(git log -1 --format="%ae")
          echo "author_email=$AUTHOR_EMAIL" >> $GITHUB_OUTPUT

      - name: Send email notification
        uses: dawidd6/action-send-mail@v3
        with:
          server_address: smtp.gmail.com
          server_port: 587
          username: ${{ secrets.MAIL_USERNAME }}
          password: ${{ secrets.MAIL_PASSWORD }}
          subject: "❌ BUILD/TEST FAILED - ${{ github.repository }} #${{ github.run_number }}"
          to: ${{ env.NOTIFY_EMAIL }}
          cc: ${{ steps.get_author.outputs.author_email }}
          from: GitHub Actions CI/CD
          body: |
            The Build & Test stage has FAILED.
            
            Repository: ${{ github.repository }}
            Run #: ${{ github.run_number }}
            Commit: ${{ github.sha }}
            Author: ${{ github.actor }}
            Branch: ${{ github.ref_name }}
            URL: ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}
            
            Please check the build log for details.

  deploy:
    needs: build-and-test
    if: success()
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Install Ansible
        run: |
          sudo apt-get update
          sudo apt-get install -y ansible

      - name: Run Ansible Playbook
        id: deploy_step
        run: |
          ansible-playbook -i inventory ansible-playbook.yml
        env:
          ANSIBLE_HOST_KEY_CHECKING: "false"
        continue-on-error: true

      - name: Check deploy status
        if: steps.deploy_step.outcome == 'failure'
        run: |
          echo "Deploy failed!"
          exit 1

  notify-deploy-failure:
    needs: deploy
    if: failure()
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Get commit author email
        id: get_author
        run: |
          AUTHOR_EMAIL=$(git log -1 --format="%ae")
          echo "author_email=$AUTHOR_EMAIL" >> $GITHUB_OUTPUT

      - name: Send email notification
        uses: dawidd6/action-send-mail@v3
        with:
          server_address: smtp.gmail.com
          server_port: 587
          username: ${{ secrets.MAIL_USERNAME }}
          password: ${{ secrets.MAIL_PASSWORD }}
          subject: "❌ DEPLOY FAILED - ${{ github.repository }} #${{ github.run_number }}"
          to: ${{ env.NOTIFY_EMAIL }}
          cc: ${{ steps.get_author.outputs.author_email }}
          from: GitHub Actions CI/CD
          body: |
            The Ansible Deploy stage has FAILED.
            
            Repository: ${{ github.repository }}
            Run #: ${{ github.run_number }}
            Commit: ${{ github.sha }}
            Author: ${{ github.actor }}
            URL: ${{ github.server_url }}/${{ github.repository }}/actions/runs/${{ github.run_id }}
            
            Build and tests passed, but deployment to the web server failed.
            Please check the deployment logs.
```

---

## Submission Checklist

- [x] GitHub Actions workflow file created (`.github/workflows/ci.yml`)
- [x] Trigger: Runs on every commit/push to main/master branch
- [x] Build stage: `./mvnw clean package -DskipTests -q`
- [x] Test stage: `./mvnw test -Dspring.profiles.active=test` (SQLite in-memory)
- [x] Deploy stage: `ansible-playbook` (only if build + tests succeed)
- [x] Email notification on build/test failure — **TO: srengty@gmail.com + CC: developer**
- [x] Email notification on deploy failure — **TO: srengty@gmail.com + CC: developer**
- [x] JAR build output uploaded as artifact
- [x] Ansible inventory file created (`inventory`)
- [x] All files committed and pushed to GitHub repository (commit: `52cfc71`)
- [x] Separate `notify-build-failure` job for build/test failures
- [x] Separate `notify-deploy-failure` job for deploy failures

---

**Date:** June 18, 2026
**Submitted by:** HUY Chanchhinghoir (ID: 20221469 - Group B)
**Repository:** https://github.com/ChingHoir/Final-devop.git
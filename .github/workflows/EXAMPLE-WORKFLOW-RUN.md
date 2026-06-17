# Example: GitHub Actions Workflow Run

This document shows **what happens** when you push a commit to GitHub.

---

## Scenario 1: SUCCESSFUL Build → Test → Deploy

### 1. Developer pushes a commit
```bash
git add .
git commit -m "Fix profile type validation"
git push origin main
```

### 2. GitHub Actions automatically triggers
Go to: https://github.com/ChingHoir/Final-devop.git → **Actions** tab

You will see:

### 3. Job: `build-and-test` (Running)

```
Checkout code          ✅ Pulled from GitHub (fetch-depth: 2)
Set up JDK 21          ✅ JDK 21 Temurin installed (cached Maven dependencies)
Build with Maven       ✅ [INFO] BUILD SUCCESS
Run Tests (SQLite)     ✅ Tests run: 1, Failures: 0, Errors: 0
Upload JAR artifact    ✅ id-card-management-1.0.0.jar uploaded
```

**Console output of build:**
```
[INFO] Scanning for projects...
[INFO] Building ID Card Management System 1.0.0
[INFO] --- jar:3.3.0:jar (default-jar) @ id-card-management ---
[INFO] Building jar: .../target/id-card-management-1.0.0.jar
[INFO] BUILD SUCCESS
```

**Console output of tests:**
```
The following 1 profile is active: "test"
HikariPool-1 - Added connection org.sqlite.jdbc4.JDBC4Connection
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 4. Job: `deploy` (Runs because build-and-test succeeded)

```
Install Ansible        ✅ ansible installed
Run Ansible Playbook   ✅ PLAY RECAP: ok=4 changed=0 failed=0
```

### 5. ✅ Pipeline Result: **ALL GREEN**

```
All jobs completed successfully!
✓ build-and-test   → PASSED
✓ deploy           → PASSED
```

**No email sent** — everything worked fine.

---

## Scenario 2: BUILD FAILED → Email Sent

### 1. Developer pushes code with a compile error
```bash
git commit -m "Oops, broke the build"
git push origin main
```

### 2. Job: `build-and-test` (Failing)

```
Checkout code          ✅ Pulled from GitHub
Set up JDK 21          ✅ JDK 21 installed
Build with Maven       ❌ COMPILATION ERROR
```

**Console output of failure:**
```
[ERROR] Failed to execute goal ... compiler:3.11.0:compile
[ERROR] .../ProfileService.java:[32,20] cannot find symbol
[ERROR]   symbol:   method syncProfileType(Profile)
[ERROR] BUILD FAILURE
```

### 3. Job: `notify-build-failure` (Runs because build failed)

```
Checkout code          ✅ Pulled from GitHub
Get commit author      ✅ Extracted: chanchhinghoir@example.com
Send email             ✅ Sent!
```

### 4. ❌ Pipeline Result: **RED**

```
✗ build-and-test   → FAILED (compilation error)
  notify-build-failure → SENT EMAIL
```

### 5. Email received by both recipients:

```
TO:       srengty@gmail.com
CC:       chanchhinghoir@example.com
SUBJECT:  ❌ BUILD/TEST FAILED - ChingHoir/Final-devop #42

The Build & Test stage has FAILED.

Repository: ChingHoir/Final-devop
Run #: 42
Commit: a1b2c3d4e5f6...
Author: HUY Chanchhinghoir
Branch: main
URL: https://github.com/ChingHoir/Final-devop/actions/runs/42

Please check the build log for details.
```

---

## Scenario 3: DEPLOY FAILED → Email Sent

### 1. Build and tests pass, but Ansible fails

```
build-and-test → ✅ SUCCESS
deploy         → ❌ FAILED (SSH connection refused)
```

### 2. Job: `notify-deploy-failure` (Runs because deploy failed)

```
Checkout code          ✅ Pulled from GitHub
Get commit author      ✅ Extracted: chanchhinghoir@example.com
Send email             ✅ Sent!
```

### 3. Email received:

```
TO:       srengty@gmail.com
CC:       chanchhinghoir@example.com
SUBJECT:  ❌ DEPLOY FAILED - ChingHoir/Final-devop #42

The Ansible Deploy stage has FAILED.

Repository: ChingHoir/Final-devop
Run #: 42
Commit: a1b2c3d4e5f6...
Author: HUY Chanchhinghoir
URL: https://github.com/ChingHoir/Final-devop/actions/runs/42

Build and tests passed, but deployment to the web server failed.
Please check the deployment logs.
```

---

## How to Trigger the Workflow Yourself

1. **Make any change** to any file in the repository
2. **Commit and push:**
   ```bash
   git add .
   git commit -m "Test GitHub Actions workflow"
   git push origin main
   ```
3. **Go to GitHub** → Repository → **Actions** tab
4. **Watch the pipeline run** in real-time

### To test a FAILURE scenario (for email demo):
Create a temporary compilation error:
```bash
echo "broken code" > id-card-management/src/main/java/com/example/idcard/service/ProfileService.java
git add .
git commit -m "Testing failure email notification"
git push origin main
```
Then fix it after seeing the email:
```bash
git checkout -- id-card-management/src/main/java/com/example/idcard/service/ProfileService.java
git add .
git commit -m "Fixed broken code"
git push origin main
```

---

## Viewing Workflow Results

1. Go to: https://github.com/ChingHoir/Final-devop.git
2. Click **Actions** tab
3. Click any workflow run to see:
   - ✅ Green = All jobs passed
   - ❌ Red = Some jobs failed (email sent)
   - 🟡 Yellow = Workflow running

---

## Required GitHub Secrets Setup (for email to work)

Go to: `Settings → Secrets and variables → Actions → New repository secret`

| Secret | Value |
|--------|-------|
| `MAIL_USERNAME` | `chinghoir11@gmail.com` (your Gmail) |
| `MAIL_PASSWORD` | `xxxx xxxx xxxx xxxx` (Gmail App Password) |

**Without these secrets:** The pipeline still runs build → test → deploy, but email notifications are skipped.

---

## ⚡ LIVE DEMO: Push to Trigger the Workflow Now

Try it yourself — this example file was created to show you. Push to GitHub and watch the pipeline run:

```bash
git add .github/workflows/EXAMPLE-WORKFLOW-RUN.md
git commit -m "Add workflow example guide"
git push origin main
```

Then go to: **https://github.com/ChingHoir/Final-devop/actions** → You'll see the workflow running!

You'll see:
1. `build-and-test` — turns 🟡 yellow, then ✅ green (if success)
2. `deploy` — starts after build passes, runs Ansible
3. All jobs ✅ green = success

If you want to test **email notification**, intentionally break a file and push. But remember to fix it after! 😄

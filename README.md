# Student Registration Form - DevOps Project

A clean, exam-ready Java and Maven DevOps web application demonstrating a complete CI/CD pipeline and containerization workflow.

---

## 🌟 Key Features
- **Pure Java & Maven**: Built using standard Java SE HTTP Server (`com.sun.net.httpserver.HttpServer`).
- **No Heavy Frameworks or Databases**: Fast startup (< 1 sec), minimal memory usage, zero external database configuration.
- **Single-page Registration Form**:
  - Student Name
  - Roll Number
  - Email
  - Course selection
  - Submit button & instant confirmation view
- **Automated CI/CD**:
  - **GitHub Actions**: Automated build and test on push/PR (`.github/workflows/build.yml`)
  - **Jenkins Pipeline**: Simple declarative pipeline with Checkout, Build, and Test stages (`Jenkinsfile`)
  - **Docker Containerization**: Multi-stage build and lightweight JRE runtime (`Dockerfile`)
- **Port Conflict Protection**: Default port is `8080`, but automatically falls back to `8081` if port 8080 is in use (e.g. by a local Jenkins server).

---

## 📁 Project Structure

```text
Student Registration Form/
│
├── .github/
│   └── workflows/
│       └── build.yml              # GitHub Actions CI workflow
│
├── src/
│   ├── main/
│   │   ├── java/com/devops/
│   │   │   └── App.java           # Java HTTP Server & Request Handlers
│   │   └── resources/
│   │       └── index.html         # Student Registration Form HTML & CSS
│   └── test/
│       └── java/com/devops/
│           └── AppTest.java       # JUnit 5 Automated Unit Tests
│
├── pom.xml                        # Maven configuration & build plugins
├── Dockerfile                     # Multi-stage Docker image definition
├── Jenkinsfile                    # Jenkins CI pipeline script
├── .gitignore                     # Git ignore rules for Maven & IDEs
└── README.md                      # Documentation & Execution Guide
```

---

## 🚀 Step-by-Step PowerShell Guide for Windows

Open **Windows PowerShell** in this project directory (`d:\Sutdent Registration Form`) and follow the steps below.

### Step 1: Ensure Maven and Java are in PATH
If `mvn` is not recognized directly in your terminal, add the installed Maven directory to your current PowerShell session:

```powershell
$env:PATH = "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.2.6.1\plugins\maven\lib\maven3\bin;$env:PATH"
```

Verify your installation:
```powershell
java -version
mvn -version
```

---

### Step 2: Compile and Run Unit Tests
To compile the source code and run the automated tests:

```powershell
mvn test
```

You should see:
```text
[INFO] Running com.devops.AppTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

### Step 3: Package the Application into a JAR
Build the standalone executable JAR file:

```powershell
mvn clean package
```

The compiled JAR file will be created inside the `target/` directory:
- `target/student-registration-form-1.0.0.jar`

---

### Step 4: Run the Application Locally
Run the executable JAR with Java:

```powershell
java -jar target\student-registration-form-1.0.0.jar
```

You will see:
```text
=================================================
 Student Registration Web App is running!
 URL: http://localhost:8080/
 Health Check: http://localhost:8080/health
 Press Ctrl+C to stop the server.
=================================================
```
*(Note: If Jenkins is running on port 8080, the application automatically switches to `http://localhost:8081/`)*

Open your web browser and navigate to:
```text
http://localhost:8080/
```
(or `http://localhost:8081/` if port 8080 was in use).

Fill out the form with sample student details and click **Register Student** to view the confirmation screen.

To stop the server, press `Ctrl + C` in the PowerShell window.

---

## 🐳 Docker Execution

### Step 1: Build the Docker Image
Ensure Docker Desktop is running, then execute:

```powershell
docker build -t student-registration-form .
```

### Step 2: Run the Docker Container
Map port `8080` (or `8081` if 8080 is used by Jenkins):

```powershell
docker run -d -p 8080:8080 --name student-app student-registration-form
```

Open in browser:
```text
http://localhost:8080/
```

### Step 3: Stop and Remove Container
```powershell
docker stop student-app
docker rm student-app
```

---

## 🔄 CI/CD Pipelines

### 1. GitHub Actions (`.github/workflows/build.yml`)
- Triggers automatically whenever you `git push` to `main` or `master`.
- Spins up an Ubuntu runner, installs Java 17, and runs `mvn -B package`.
- To push to GitHub:
  ```powershell
  git init
  git add .
  git commit -m "Initial commit of Student Registration Form DevOps project"
  git branch -M main
  git remote add origin https://github.com/<your-username>/<your-repo-name>.git
  git push -u origin main
  ```

### 2. Jenkins Pipeline (`Jenkinsfile`)
- Stages:
  1. **Checkout**: Pulls code from Git SCM repository.
  2. **Build**: Executes `mvn clean compile`.
  3. **Test**: Executes `mvn test`.
- In Jenkins: Create a new **Pipeline** job, select **Pipeline script from SCM**, choose **Git**, and provide your repository URL.

---

## 🎓 Viva / Practical Exam Explanation Tips

1. **Why use Java Built-in HTTP Server?**
   - It is part of standard Java (`com.sun.net.httpserver.HttpServer`).
   - Requires zero third-party dependencies or heavy application servers like Tomcat.
   - Ideal for demonstration because it is lightweight, starts instantly, and runs with a single `java -jar` command.
2. **What does `mvn clean package` do?**
   - `clean`: Deletes previously built files in `target/`.
   - `compile`: Compiles Java source files to `.class` bytecode.
   - `test`: Executes JUnit 5 test cases.
   - `package`: Bundles compiled classes and `index.html` into an executable `.jar` file.
3. **What is a Multi-Stage Dockerfile?**
   - First stage uses `maven:3.9-eclipse-temurin-17` to compile and package the app.
   - Second stage uses a lightweight `eclipse-temurin:17-jre` runtime image and only copies the resulting `.jar`.
   - Result: Much smaller image size, faster download, and higher security since build tools are not present in the runtime container.

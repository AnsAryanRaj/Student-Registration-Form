pipeline {
    agent any

    tools {
        // Ensure Maven and JDK tools are configured in Jenkins Global Tool Configuration
        maven 'Maven 3'
        jdk 'Java 17'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code from repository...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Compiling the application with Maven...'
                bat 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                echo 'Running unit tests with Maven...'
                bat 'mvn test'
            }
        }
    }

    post {
        always {
            echo 'Pipeline execution completed.'
        }
        success {
            echo 'Build and Test succeeded!'
        }
        failure {
            echo 'Pipeline failed. Check test results and logs.'
        }
    }
}

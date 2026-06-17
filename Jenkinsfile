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
                    // Start port-forward to SSH into web container
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
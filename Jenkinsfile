pipeline {
    agent any

    environment {
        JENKINS = 'true'
    }

    tools {
        jdk 'jdk-17'
    }

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        skipStagesAfterUnstable()
        buildDiscarder(logRotator(numToKeepStr: '30'))
    }

    stages {

        stage('Clean') {
            // Only clean when the last build failed
            when {
                expression {
                    currentBuild.previousBuild?.currentResult == 'FAILURE'
                }
            }
            steps {
                sh "./gradlew clean"
            }
        }

        stage('Info') {
            steps {
                sh './gradlew -v' // Output gradle version for verification checks
                sh './grailsw -v' // Output grails version for verification checks
            }
        }

        stage('Test cleanup & Compile') {
            steps {
                sh "./gradlew jenkinsClean"
                sh './gradlew compile'
            }
        }

        stage('License Header Check') {
            steps {
                warnError('Missing License Headers') {
                    sh './gradlew --build-cache license'
                }
            }
        }
//
//        stage('Unit Test') {
//
//            steps {
//                sh "./grailsw test-app -unit"
//            }
//            post {
//                always {
//                    junit allowEmptyResults: true, testResults: 'build/test-results/test/*.xml'
//                    publishHTML([
//                        allowMissing         : false,
//                        alwaysLinkToLastBuild: true,
//                        keepAll              : true,
//                        reportDir            : 'build/reports/tests',
//                        reportFiles          : 'index.html',
//                        reportName           : 'Test Report',
//                        reportTitles         : 'Test'
//                    ])
//                }
//            }
//        }
//
//        stage('Integration Test') {
//
//            steps {
//                sh "./grailsw test-app -integration"
//            }
//            post {
//                always {
//                    junit allowEmptyResults: true, testResults: 'build/test-results/integrationTest/*.xml'
//                    publishHTML([
//                        allowMissing         : false,
//                        alwaysLinkToLastBuild: true,
//                        keepAll              : true,
//                        reportDir            : 'build/reports/tests',
//                        reportFiles          : 'index.html',
//                        reportName           : 'Test Report',
//                        reportTitles         : 'Test'
//                    ])
//                }
//            }
//        }

        stage('Deploy to Artifactory') {
            when {
                allOf {
                    anyOf {
                       branch 'main'
                        branch 'develop'
                    }
                    expression {
                        currentBuild.currentResult == 'SUCCESS'
                    }
                }

            }
            steps {
                script {
                    sh "./gradlew publish"
                }
            }
        }
    }

    post {
        always {
//            outputTestResults()
//            jacoco execPattern: '**/build/jacoco/*.exec'
//            archiveArtifacts allowEmptyArchive: true, artifacts: '**/*.log'
            zulipNotification(topic: 'dita-groovy-dsl')
        }
    }
}
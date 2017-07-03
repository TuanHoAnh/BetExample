#!groovy​

node {
    def mvnHome
    stage('Preparation') {
        git 'git@git.kms-technology.com:KMS-Next/next-01-ngaythobet.git'
        mvnHome = tool 'maven-3.3.9'
    }
    stage('Build') {
        sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
    }
    stage('Results') {
        junit '**/target/surefire-reports/TEST-*.xml'
        archive 'target/*.jar'
    }
}

pipeline {
  agent any
  stages {
      stage('拉取Git代码') {
          steps {
              checkout([$class: 'GitSCM', branches: [[name: '${tag}']],
              extensions: [], userRemoteConfigs: [[url:
              'https://github.com/jiangchengyu998/demo.git']]])
          }
      }
      stage('构建代码') {
          steps {
              sh '/var/jenkins_home/maven/bin/mvn clean package -DskipTests'
          }
      }
      stage('检测代码质量') {
          steps {
              sh '/var/jenkins_home/sonar-scanner/bin/sonar-scanner -Dsonar.source=./ -Dsonar.projectName=${JOB_NAME} -Dsonar.projectKey=${JOB_NAME} -Dsonar.java.binaries=./target -Dsonar.exclusions=**/*.java -Dsonar.test.exclusions=**/*.java -Dsonar.coverage.exclusions=**/*.java -Dsonar.login=6da0d36ca3a51f8fa2fcad8cff37fd474f2d1a77'
          }
      }

  }
}

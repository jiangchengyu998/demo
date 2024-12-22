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
  }
}

def call(Map params) {

    def componentArray = [];
    def message = "";
    pipeline {
        agent any
        environment {
          TAG = sh(script: "echo `date +%Y-%m%d-%H%M`", returnStdout: true).trim()
          Registry = "pgtgs/"
          dockerhub = credentials('dockerhub')
          Image = "${env.JOB_NAME.replace('/', '-').substring(0, 20).toLowerCase()}"
          Api_Service_Branch="develop"
          kube_ns = getKubeNamespace(env.GIT_BRANCH)
          k8s_config = getKubeConfig(env.GIT_BRANCH)
          
          commit_url = "${env.GIT_URL.replace('.git', '/-/commit/')}${env.GIT_COMMIT}"
        }
    
    stages {                
        stage('Prepare Env') {
            steps {
                checkout scm
                sh "echo 'Branch: ${env.GIT_BRANCH}'"

                script {
                    echo "Api_Service_Branch=$Api_Service_Branch"
                    configureGitModules(Api_Service_Branch)
                    TAG = env.kube_ns + '-' + TAG
                }

                configFileProvider([configFile(fileId: "tgs.${kube_ns}", variable: 'tgsconfig')]) {
                    script {
                        def envItems = readProperties file: "$tgsconfig"
                        envItems.each { key, value -> 
                            env."tgs_${key}" = value;
                        }
                        def component=params.components
                        componentArray = "${env."tgs_${component}"}".split(",")
                        
                        echo "components: ${componentArray.join(', ')}"
                        message = createMessage(componentArray)
                    }
                }
                sh "echo 'env: $env.kube_ns'"
                sh "echo 'tag: ${TAG}'"
            }
        }   
        stage('Docker Build') {   
            steps {                 
                sh "docker build -t ${Registry}${Image}:${TAG} ."
            }
        }           
        stage('Docker Push') {   
            steps {   
                sh "docker push ${Registry}${Image}:${TAG}"
            }
        }
        stage('Deploy') {
            when {
                expression {
                    return env.kube_ns == 'prod';
                }
            }

            steps {
                script {
                    input message: 'Ready to go?', ok: 'yes';
                }
                
                sh "echo start deploy"
            }
        }
        stage('Deployment') {   
            steps {   
                script {            
                    withKubeConfig([credentialsId: env.k8s_config]) {
                        for (i = 0;i < componentArray.length; i++) {
                            sh "kubectl set image deployment/${componentArray[i]} ${componentArray[i]}=${Registry}${Image}:${TAG} --record -n $env.kube_ns"
                        }
                    }
                }
            }
        }   
    }
    post {
        success {
             sendTelegram("Pipeline: ${currentBuild.fullDisplayName} OK! ${message} \n${currentBuild.getBuildCauses()[0].shortDescription}\n")
        }
        failure {
            sendTelegram("<b>Failed Pipeline:</b> ${currentBuild.fullDisplayName} Something is wrong with ${env.BUILD_URL}/console\n${currentBuild.getBuildCauses()[0].shortDescription}\n")
        }
    }
  }
}
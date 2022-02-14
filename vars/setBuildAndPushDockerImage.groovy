def call(Map pipelineParams) {
	stage('Build Docker Image') {
		steps {
			sh "dotnet build ${pipelineParams.buildProjectName} -c $BuildConfiguration -t:ContainerBuild -p:DockerfileTag=${pipelineParams.imageName}:${TAG} --no-restore"
		}
	}

	stage('Push Docker Image') {
		steps {
			sh "sudo docker push ${pipelineParams.imageName}:${TAG}"
		}
	}
}
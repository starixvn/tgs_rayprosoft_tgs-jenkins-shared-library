def call(Map pipelineParams) {
    steps {
		script {

			def resources = getResources(env.k8s_config, pipelineParams.tgsenv, pipelineParams.deploymentName)
			println resources

			withKubeConfig([credentialsId: env.k8s_config]) {
				withCredentials([string(credentialsId: "tgs-${pipelineParams.tgsenv}-connectionstring", variable: "tgs_tgs")]) {
					pipelineParams.helmPrintExec(resources)
					pipelineParams.helmRunExec(resources)
				}
			}
		}
	}
}
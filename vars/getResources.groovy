def call(kubeconfig, tgsenv, deployment) {
    withKubeConfig([credentialsId: kubeconfig]) {
		resource = sh (script: 'kubectl get cm resources-setting -o=jsonpath="{.data.'+ deployment +'}" -n' + tgsenv, returnStdout: true)
		return resource
	}
}
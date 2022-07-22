def call(kubeconfig, tgsenv, deployment) {
    withKubeConfig([credentialsId: kubeconfig]) {
		resource = sh (script: 'kubectl get cm autoscaling-setting -o=jsonpath="{.data.'+ deployment +'}" -n' + tgsenv, returnStdout: true)
		return resource
	}
}
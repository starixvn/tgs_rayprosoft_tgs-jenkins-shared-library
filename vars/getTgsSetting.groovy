def call(kubeconfig, tgsenv, key) {
    withKubeConfig([credentialsId: kubeconfig]) {
		resource = sh (script: 'kubectl get cm tgs-setting -o=jsonpath="{.data.'+ key +'}" -n' + tgsenv, returnStdout: true)
		return resource
	}
}
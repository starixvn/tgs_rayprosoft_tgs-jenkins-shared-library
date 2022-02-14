def call() {
	stage('Prepare'){
		steps {
			sh "echo 'Branch: $GIT_BRANCH'";
			sh "printenv";
			script {
				switch(env.GIT_BRANCH)
				{
					case ['main', 'origin/main']:
						env.kube_ns = "prod";
						env.k8s_config = 'stag-k8s-config';
						env.node_selector = '{ "dedicated": "prod" }';
						env.dedicated = '{"effect":"NoSchedule","key": "dedicated", "operator": "Equal", "value": "prod"}';
						break;
					case ['release', 'origin/release']:
						env.kube_ns = "staging";
						env.k8s_config = 'stag-k8s-config';
						env.node_selector = '{ "dedicated": "nonprod" }';
						env.dedicated = '{"effect":"NoSchedule","key": "dedicated", "operator": "Equal", "value": "noprod"}';
						break;
					default:
						env.kube_ns = "dev";
						env.k8s_config = 'k8s-kube-config';
						env.node_selector = '{}';
						env.dedicated = '';
						break;
				}
			}
			configFileProvider([
				configFile(
					fileId: 'tgs.' + env.kube_ns , 
					variable: 'tgsconfig')]) {
				script {
					def envItems = readProperties file: "$tgsconfig";

					envItems.each { key, value -> 
						env."tgs_${key}" = value;
					}

					env.TAG = sh(script: "echo `date +%Y%m%d%H%M`", returnStdout: true).trim() + '-' + env.kube_ns
					message += "\nBranch: ${env.GIT_BRANCH}" 
					message += "\nTag: ${env.TAG}"
					message += "\nEnv: ${env.kube_ns}"
					env.commit_url = "${env.GIT_URL}".replace("git@gitlab.com:","https://gitlab.com/")
						.replace(".git","/-/commit/") + "${env.GIT_COMMIT}"
					message += "\n Commit: ${env.commit_url}"
				}
			}
			sh "echo tag: $TAG"
		}
	}
}
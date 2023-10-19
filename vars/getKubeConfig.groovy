def call(branch) {
    switch (branch) {
        case ['master', 'origin/master']:
            Api_Service_Branch = "master"
            return 'prod-k8s-config'
        case ['release/current', 'origin/release/current']:
            return 'stag-k8s-config'
        default:
            return 'k8s-kube-config'
    }
}
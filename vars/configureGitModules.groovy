def call(branch) {
    sh "git config --file=.gitmodules submodule.src/api-services.branch ${branch}"
    sh "git submodule sync"
    sh "git submodule update --remote"
}
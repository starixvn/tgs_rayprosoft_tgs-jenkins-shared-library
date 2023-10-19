def call(componentArray) {
    def createMessage(componentArray) {
    def message = """   
Branch: ${env.GIT_BRANCH}
Image: ${Image}
Tag: ${TAG}
Env: ${env.kube_ns}
Commit: ${env.commit_url}
Component:
"""
    for (i = 0; i < componentArray.length; i++) {
        message += "    ${componentArray[i]}\n"
    }

    return message
}
}
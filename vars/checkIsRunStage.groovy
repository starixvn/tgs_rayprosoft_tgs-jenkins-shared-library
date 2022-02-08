def call(currentName, selectedNames) {
    def arr = selectedNames.split(",")
    def isRun = false

    if(arr.length > 0)
        for(i = 0; i < arr.length; i++) {
            isRun = isRun || (arr[i] == currentName)
        }

    return isRun
}
props = [:]

void add(String key, def value) {
    if (value) {
        props.put(key, value)
    }
}

void readFromUrl(String url) {
    if (url) {
        tempFile = sh(returnStdout: true, script: 'mktemp').trim()
        sh "wget ${url} -O ${tempFile}"
        readFromFile(tempFile)
    }
}

void readFromFile(String file) {
    props = readProperties file: file
    echo props.collect { entry ->  "${entry.key}=${entry.value}" }.join('\n')
}

void writeToFile(String file) {
    def propertiesStr = props.collect { entry ->  "${entry.key}=${entry.value}" }.join('\n')
    writeFile(text: propertiesStr, file: file)
}

boolean contains(String key) {
    return props[key]
}

String retrieve(String key) {
    return props[key]
}

return this

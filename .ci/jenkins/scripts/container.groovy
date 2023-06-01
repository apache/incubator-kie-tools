containerOpenshift = null

containerEngine = 'docker'
containerEngineTlsOptions = ''

void pullImage(String image) {
    retry(env.MAX_REGISTRY_RETRIES ?: 1) {
        sh "${containerEngine} pull ${containerEngineTlsOptions} ${image}"
    }
}

void tagImage(String oldImage, String newImage) {
    sh "${containerEngine} tag ${oldImage} ${newImage}"
}

void pushImage(String image) {
    retry(env.MAX_REGISTRY_RETRIES ?: 1) {
        sh "${containerEngine} push ${containerEngineTlsOptions} ${image}"
    }
}

void loginContainerRegistry(String registry, String credsId) {
    withCredentials([usernamePassword(credentialsId: credsId, usernameVariable: 'REGISTRY_USER', passwordVariable: 'REGISTRY_PWD')]) {
        sh "${containerEngine} login ${containerEngineTlsOptions} -u ${REGISTRY_USER} -p ${REGISTRY_PWD} ${registry}"
    }
}

void loginOpenshiftRegistry() {
    containerOpenshift.loginOpenshift()
    // username can be anything. See https://docs.openshift.com/container-platform/4.4/registry/accessing-the-registry.html#registry-accessing-directly_accessing-the-registry
    sh "set +x && ${containerEngine} login ${containerEngineTlsOptions} -u anything -p \$(oc whoami -t) ${containerOpenshift.getOpenshiftRegistry()}"
}

return this

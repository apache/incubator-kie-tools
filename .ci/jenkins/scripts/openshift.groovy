openshiftApiKey = ''
openshiftApiCredsKey = ''

openshiftInternalRegistry = 'image-registry.openshift-image-registry.svc:5000'

void loginOpenshift() {
    withCredentials([string(credentialsId: openshiftApiKey, variable: 'OPENSHIFT_API')]) {
        withCredentials([usernamePassword(credentialsId: openshiftApiCredsKey, usernameVariable: 'OC_USER', passwordVariable: 'OC_PWD')]) {
            sh "oc login --username=${OC_USER} --password=${OC_PWD} --server=${OPENSHIFT_API} --insecure-skip-tls-verify"
        }
    }
}

String getOpenshiftRegistry() {
    return sh(returnStdout: true, script: 'oc get routes -n openshift-image-registry | tail -1 | awk \'{print $2}\'').trim()
}

return this

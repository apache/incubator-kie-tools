minikubeVersion = env.MINIKUBE_VERSION ?: '1.30.1'
minikubeKubernetesVersion = env.KUBERNETES_VERSION ?: '1.26.3'
minikubeContainerEngine = env.CONTAINER_ENGINE ?: 'podman'

minikubeCpus = 'max'
minikubeMemory = '4g'
minikubeAddons = [ 'registry', 'metrics-server' ]
minikubeRegistryMirror = env.DOCKER_REGISTRY_MIRROR ?: ''
minikubeInsecureRegistries = []

void start(boolean debug = false) {
    preChecks()

    def minikubeOpts = [
        "--driver=${minikubeContainerEngine}",
        "--kubernetes-version ${minikubeKubernetesVersion}",
        "--cpus ${minikubeCpus}",
        "--memory ${minikubeMemory}",
        // Default insecure registries added
        '--insecure-registry "192.168.0.0/16"',
        '--insecure-registry "localhost:5000"',
    ]
    minikubeOpts.addAll(minikubeAddons.collect { "--addons ${it}" })
    minikubeOpts.addAll(minikubeInsecureRegistries.collect { "--insecure-registry '${it}'" })
    if (minikubeRegistryMirror) {
        minikubeOpts.addAll([
            "--registry-mirror http://${minikubeRegistryMirror}",
            "--insecure-registry ${minikubeRegistryMirror}",
        ])
    }

    println "Start minikube with options ${minikubeOpts}"
    sh """
        minikube delete
        minikube start ${minikubeOpts.join(' ')}
        minikube status

        kubectl version
        kubectl get pods -A
    """

    if (debug) {
        sh 'kubectl get events -n kube-system'
    }
}

void waitForMinikubeStarted() {
    println 'Wait for Minikube components to be in Running state'
    def minikubeStatus = sh(returnStatus: true, script: '''
        set -x
        source ./hack/kube-utils.sh
        MINIKUBE_COMPONENTS=(etcd kube-apiserver kube-controller-manager kube-scheduler)
        for component in "${MINIKUBE_COMPONENTS[@]}"
        do
            echo "Check component '${component}' is in 'Running' state"
            waitKubeSystemForPodReady "-l tier=control-plane -l component=${component}"
        done

        echo "Check kube-dns is in 'Running' state"
        waitKubeSystemForPodReady "-l k8s-app=kube-dns"
    ''')
    if (minikubeStatus != 0) {
        error 'Error starting Minikube ...'
    }
}

void waitForMinikubeRegistry() {
    println 'Wait for Minikube registry to be in Running state'
    def minikubeStatus = sh(returnStatus: true, script: '''
        set -x
        kubectl get pods -A
        source ./hack/kube-utils.sh
        waitKubeSystemForPodReady "-l kubernetes.io/minikube-addons=registry -l actual-registry=true"
    ''')
    if (minikubeStatus != 0) {
        error 'Error waiting for Minikube registry ...'
    }
}

void preChecks() {
    sh """
        ${minikubeContainerEngine} info

        if [[ ! \$(command -v minikube) ]]; then
            curl -LO https://github.com/kubernetes/minikube/releases/download/v${minikubeVersion}/minikube-${minikubeVersion}-0.x86_64.rpm
            sudo yum localinstall -y minikube-${minikubeVersion}-0.x86_64.rpm
        fi
    """
}

void stop() {
    if (sh(returnStatus: true, script: 'which minikube') == 0) {
        sh '''
            minikube delete
        '''
    }
}

String getImageRegistry() {
    return sh(returnStdout: true, script: 'echo "$(minikube ip):5000"').trim()
}

return this

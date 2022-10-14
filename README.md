# Kogito Serverless Operator

The Kogito Serverless Operator is built in order to help the Kogito Serverless users to build and deploy easily on 
Kubernetes/Knative/OpenShift a service based on Kogito that it will be able to execute a workflow.

The CustomResources defined and managed by this operator are the following:
- Workflow 
- Build

# PreRequisites

operator-sdk-v1.24.0

Go 1.19


## Description
The aim of this project is to collect everything is needed for a proof-of-concept of the Kogito Serverless Workflow CRD

## Getting Started
You’ll need a Kubernetes cluster to run against. You can use [KIND](https://sigs.k8s.io/kind) to get a local cluster for testing, or run against a remote cluster.
**Note:** Your controller will automatically use the current context in your kubeconfig file (i.e. whatever cluster `kubectl cluster-info` shows).

### Running on the cluster
1. Install Instances of Custom Resources:

```sh
kubectl apply -f config/samples/
```

2. Build and push your image to the location specified by `IMG`:
	
```sh
make docker-build docker-push IMG=<some-registry>/kogito-serverless-operator:tag
```
	
3. Deploy the controller to the cluster with the image specified by `IMG`:

```sh
make deploy IMG=<some-registry>/kogito-serverless-operator:tag
```

### Uninstall CRDs
To delete the CRDs from the cluster:

```sh
make uninstall
```

### Undeploy controller
UnDeploy the controller to the cluster:

```sh
make undeploy
```

Minikube or CRC

## Test the Greeting workflow on Minikube

A good starting point to check that everything is working well, it is the [Greeting workflow](https://github.com/kiegroup/kogito-examples/blob/stable/README.md#serverless-workflow-getting-started).

Follow these steps to create a container that you can than deploy as a Service on Kubernetes or KNative.

1. Start Minikube
```sh 
minikube start --cpus 4 --memory 4096 --addons registry --insecure-registry "10.0.0.0/24"
```
2. Create a namespace for the building phase

```sh
kubectl create namespace kogito-builder
```

3. Create a secret
```sh
kubectl create secret docker-registry regcred --docker-server=<registry_url> --docker-username=<registry_username> --docker-password=<registry_password> --docker-email=<registry_email> -n kogito-builder
```

4. Build and push your image to the location specified by `IMG`:

```sh
make container-build container-push IMG=<some-registry>/kogito-serverless-operator:tag
```

5. Deploy the controller to the cluster with the image specified by `IMG`:

```sh
make deploy IMG=<some-registry>/kogito-serverless-operator:tag
```

6. Create a dedicated Namespace for the test:

```sh
kubectl create namespace greeting-workflow
```

7. Install Instances of Custom Resources:

```sh
kubectl apply -f config/samples/sw.kogito.kie.org__v08_kogitoserverlessworkflow.yaml -n greeting-workflow
```

8. Default configuration

A configmap called kogito-builder-config will be created under the kogito-builder namespace at the first application of a workflow, it contains:
  
- BUILDER_RESOURCE_DEFAULT = Dockerfile
- WORKFLOW_DEFAULT_EXTENSION = .sw.json
- DEFAULT_KANIKO_SECRET_DEFAULT = regcred
- DEFAULT_REGISTRY_REPO =  quay.io/kiegroup
- Dockerfile = dockerfile content

For the local development the DEFAULT_REGISTRY_REPO must be changed

## Contributing
// TODO(user): Add detailed information on how you would like others to contribute to this project

### How it works
This project aims to follow the Kubernetes [Operator pattern](https://kubernetes.io/docs/concepts/extend-kubernetes/operator/)

It uses [Controllers](https://kubernetes.io/docs/concepts/architecture/controller/) 
which provides a reconcile function responsible for synchronizing resources untile the desired state is reached on the cluster 

### Test It Out
1. Install the CRDs into the cluster:

```sh
make install
```

2. Run your controller (this will run in the foreground, so switch to a new terminal if you want to leave it running):

```sh
make run
```

**NOTE:** You can also run this in one step by running: `make install run`

### Modifying the API definitions
If you are editing the API definitions, generate the manifests such as CRs or CRDs using:

```sh
make manifests
```

**NOTE:** Run `make --help` for more information on all potential `make` targets

More information can be found via the [Kubebuilder Documentation](https://book.kubebuilder.io/introduction.html)

## License

Copyright 2022.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


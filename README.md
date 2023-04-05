# Kogito Serverless Operator

- [Kogito Serverless Operator](#kogito-serverless-operator)
  - [Getting Started](#getting-started)
    - [Prepare a Minikube instance](#prepare-a-minikube-instance)
    - [Run the operator](#run-the-operator)
  - [Test the Greeting workflow](#test-the-greeting-workflow)
    - [Prerequisites](#prerequisites)
    - [Prepare for the build](#prepare-for-the-build)
    - [Install your workflow](#install-your-workflow)
    - [Cleanup your cluster](#cleanup-your-cluster)
  - [Use Workflow Development Mode](#use-workflow-development-mode)
    - [Development mode: External files](#development-mode-external-files) 
  - [Use local scripts](#use-local-scripts)
  - [Development and Contributions](#development-and-contributions)

The Kogito Serverless Operator is built in order to help the Kogito Serverless users to build and deploy easily on 
Kubernetes/Knative/OpenShift a service based on Kogito that will be able to execute a workflow.

The CustomResources defined and managed by this operator are the following:
- Workflow
- Platform
- Build

## Getting Started

You’ll need a Kubernetes cluster to run against. You can use:

- [KIND](https://sigs.k8s.io/kind)
- [MINIKUBE](https://minikube.sigs.k8s.io/)
- [CRC](https://console.redhat.com/openshift/create/local)

to get a local cluster for testing, or run against a remote cluster.

**Note:** Your controller will automatically use the current context in your kubeconfig file (i.e. whatever cluster `kubectl cluster-info` shows).

### Prepare a Minikube instance

```sh 
minikube start --cpus 4 --memory 4096 --addons registry --addons metrics-server --insecure-registry "10.0.0.0/24" --insecure-registry "localhost:5000"
```

**Note:** To speed up, you can increase cpus and memory options. For example, use `--cpus 12 --memory 16384`.

**Tip:** If it does work with the default driver, aka `docker`, you can try to start with the `podman` driver:
```sh 
minikube start [...] --driver podman
```

**Important:** There are some issues with the `crio` container runtime and Kaniko that the operator is using. Reference: https://github.com/GoogleContainerTools/kaniko/issues/2201

### Run the operator

1. Install the CRDs

```sh
make install
```

2. Build and push your image to the location specified by `IMG`:
	
```sh
make docker-build docker-push IMG=<some-registry>/kogito-serverless-operator:tag
```
	
3. Deploy the controller to the cluster with the image specified by `IMG`:

```sh
make deploy IMG=<some-registry>/kogito-serverless-operator:tag
```

This will deploy the operator into the `kogito-serverless-operator-system` namespace.

## Test the Greeting workflow

A good starting point to check that everything is working well, it is the [Greeting workflow](https://github.com/kiegroup/kogito-examples/blob/stable/README.md#serverless-workflow-getting-started).

### Prerequisites

* Operator is deployed on the cluster  
  See [Getting started](#getting-started)


### Prepare for the build

Follow these steps to create a container that you can than deploy as a Service on Kubernetes or KNative.

1. Create a namespace for the building phase

```sh
kubectl create namespace kogito-workflows
```

2. Create a secret for the container registry authentication

```sh
kubectl create secret docker-registry regcred --docker-server=<registry_url> --docker-username=<registry_username> --docker-password=<registry_password> --docker-email=<registry_email> -n kogito-workflows
```

or you directly import your local docker config into your kubernetes cluster:

```sh
kubectl create secret generic regcred --from-file=.dockerconfigjson=${HOME}/.docker/config.json --type=kubernetes.io/dockerconfigjson -n kogito-workflows
```

3. Create a Platform containing the configuration (i.e. registry address, secret) for building your workflows:

You can find a basic Platform CR example in the [config](config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml) folder. 

```sh
kubectl apply -f config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml -n kogito-workflows
```

**Note:** In this Custom Resource, `spec.platform.registry.secret` is the name of the secret you created just before.

**Tip:** You can also update "on-the-fly" the platform CR registry field with this command (change `<YOUR_REGISTRY>`):
```sh
cat config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml | sed "s|address: .*|address: <YOUR_REGISTRY>|g" | kubectl apply -n kogito-workflows -f -
```

### Install your workflow

1. Install the Serverless Workflow custom resource:

```sh
kubectl apply -f config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml -n kogito-workflows
```

2. You can check the logs of the build of your workflow via:

```sh
kubectl logs kogito-greeting-builder -n kogito-workflows
```

The final pushed image should be printed into the logs at the end of the build.

## Cleanup your cluster

You will need to remove the different resources you created.

1. Remove created workflow resources

```sh
kubectl delete -f config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml -n kogito-workflows
```

2. Remove the `kogito-workflows` namespace

```sh
kubectl delete namespace kogito-workflows
```

3. Remove the operator

```sh
make undeploy
```

## Use Workflow Development Mode
In the development mode, a user can edit and reload the files using the quarkus dev mode. 
To enable the dev mode add this annotation into the KogitoServerlessWorkflow CR:
```
sw.kogito.kie.org/profile: dev
```

### Development mode External files

In development mode, different external files can be edited.  
Each file type should be saved into a different configmap.  
In order to be able to edit a file via a configmap, you must set up an annotation in your KogitoServerlessWorkflow CR which reference the specific configmap which is already existing into the namespace.  
Then, each annotation entry will be turned into a file inside the path `src/main/resources/<File type specific folder>` and using the key as the filename.
See the table below for the supported types and the corresponding annotations and folder paths.

File type  | Annotation                          | Folder path
-----------|-------------------------------------|---
CamelRoute | sw.kogito.kie.org/resource-camel    | src/main/resources/routes
AsyncApi   | sw.kogito.kie.org/resource-openapi  | src/main/resources/templates
OpenApi    | sw.kogito.kie.org/resource-asyncapi | src/main/resources/specs
Generic    | sw.kogito.kie.org/resource-generic  | src/main/resources/generic

You could see an example of configmap and a workflow with one of this annotation in the file
```
https://github.com/kiegroup/kogito-serverless-operator/tree/main/config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow_devmodeWithConfigMapAndExternalResource.yaml:
```

## Use Workflow Development Mode

#### Override Builder image and version

By default the builder image use a stable version aligned to the Kogito version.

In the dev mode you can override the default builder image using the devBaseImage in the KogitoServerlessPlatform:

```sh
apiVersion: sw.kogito.kie.org/v1alpha08
kind: KogitoServerlessPlatform
metadata:
  name: kogito-workflow-platform
spec:
  cluster: kubernetes
  devBaseImage:  quay.io/<your_org>/<your-swf-builder>
  platform:
    registry:
      address: quay.io/kiegroup
      secret: regcred  
```

## Use local scripts

You can find some scripts in the [hack](./hack/local/) folder.

- `greeting_example_deploy.sh` will install operator and deploy all resources in your current cluster
- `greeting_example_remove.sh` will remove the created workflow resource from `greeting_example_deploy.sh` script.  
  If you give the `-A` or `--all` option, it will also remove the operator from the cluster.

## Development and Contributions 

Contributing is easy, just take a look at our [contributors](./CONTRIBUTING.md)'guide.

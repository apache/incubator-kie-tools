# Kogito Serverless Operator

The Kogito Serverless Operator is built in order to help the Kogito Serverless users to build and deploy easily on 
Kubernetes/Knative/OpenShift a service based on Kogito that it will be able to execute a workflow.

The CustomResources defined and managed by this operator are the following:
- Workflow
- Platform
- Build`

## Getting Started
You’ll need a Kubernetes cluster to run against. You can use
- [KIND](https://sigs.k8s.io/kind)
- [MINIKUBE](https://minikube.sigs.k8s.io/)
- [CRC](https://console.redhat.com/openshift/create/local)

to get a local cluster for testing, or run against a remote cluster.
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


## Test the Greeting workflow on Minikube

A good starting point to check that everything is working well, it is the [Greeting workflow](https://github.com/kiegroup/kogito-examples/blob/stable/README.md#serverless-workflow-getting-started).

Follow these steps to create a container that you can than deploy as a Service on Kubernetes or KNative.

1. Start Minikube
```sh 
minikube start --cpus 4 --memory 4096 --addons registry --insecure-registry "10.0.0.0/24"
```
2. Create a namespace for your workflows

```sh
kubectl create namespace kogito-workflows
```

3. Create a secret

This secret will be used by the Operator to push the images built to the desired docker registry.

```sh
kubectl create secret docker-registry regcred --docker-server=<registry_url> --docker-username=<registry_username> --docker-password=<registry_password> --docker-email=<registry_email> -n kogito-workflows
```

4. Build and push your image to the location specified by `IMG`:

```sh
make container-build container-push IMG=<some-registry>/kogito-serverless-operator:tag
```

5. Deploy the controller to the cluster with the image specified by `IMG`:

```sh
make deploy IMG=<some-registry>/kogito-serverless-operator:tag
```

6. Create a Platform containing the configuration (i.e. registry address, secret) for building your workflows:

You can find a basic Platform CR example in the [config](config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml) folder. 

```sh
kubectl apply -f config/samples/sw.kogito_v1alpha08_kogitoserverlessplatform.yaml -n kogito-workflows
```

7. Build and deploy the Greeting workflow with the Operator

You can find a basic Workflow CR example in the [config](config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml) folder.
```sh
kubectl apply -f config/samples/sw.kogito_v1alpha08_kogitoserverlessworkflow.yaml -n kogito-workflows
```

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


**NOTE:** Run `make help` for more information on all potential `make` targets

More information can be found via the [Kubebuilder Documentation](https://book.kubebuilder.io/introduction.html)

# Development and Contributions 

Contributing is easy, just take a look at our [contributors](./CONTRIBUTING.md)'guide.

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


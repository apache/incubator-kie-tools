# Workflow Project Handler

Handler to programmatically convert a local SonataFlow project into Kubernetes manifests to deploy with
the operator.

## How to

Add this module to your project's dependencies:

```shell
go get github.com/kiegroup/kogito-serverless-workflow/workflowproj
```

Then you should have access to the main entry point of this package, which is the workflow project handler builder.

The API is simple enough to describe in a few lines:

```go
package main

import (
	"os"

	"github.com/apache/incubator-kie-tools/packages/kogito-serverless-operator/workflowproj"
)

func Main() {
	// we are ignoring errors just for demo purposes, but don't do this!
	workflowFile, _ := os.Open("myworkflow.sw.json")
	propertiesFile, _ := os.Open("application.properties")
	specFile, _ := os.Open("myopenapi.yaml")
	defer workflowFile.Close()
	defer propertiesFile.Close()
	defer specFile.Close()

	// create the handler
	handler := workflowproj.New("mynamespace").
		WithWorkflow(workflowFile).
		WithAppProperties(propertiesFile).
		AddResource("myopenapi.yaml", specFile)

	// You can easily generate the Kubernetes manifests to later use client-go to deploy them in the cluster...
	objs, _ := handler.AsObjects()
	// client.Create(...), other stuff

	// ... or you can save the files locally to use them later or to integrate in a GitOps process
	_ = handler.SaveAsKubernetesManifests("/my/dir/")
}
```

The `SonataFlow` custom resource generated is annotated with
the [devmode profile](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/cloud/operator/developing-workflows.html)
.
Every other resource added to the project is a `ConfigMap` handling these resources for you.

Given that you already have the SonataFlow
Operator [installed](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/cloud/operator/install-serverless-operator.html)
, to deploy the generated project you can simply run:

```shell
kubectl apply -f /my/dir/* -n "mynamespace"
```

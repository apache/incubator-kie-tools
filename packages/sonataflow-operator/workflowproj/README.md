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

	"github.com/apache/incubator-kie-tools/packages/sonataflow-operator/workflowproj"
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

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html

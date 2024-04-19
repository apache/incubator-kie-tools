# SonataFlow Operator

The SonataFlow Operator defines a set
of [Kubernetes Custom Resources](https://kubernetes.io/docs/concepts/extend-kubernetes/api-extension/custom-resources/)
to help users to deploy SonataFlow projects on Kubernetes and OpenShift.

Please [visit our official documentation](https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/cloud/operator/install-serverless-operator.html)
to know more.

## Available modules for integrations

If you're a developer, and you are interested in integrating your project or application with the SonataFlow Operator
ecosystem, this repository provides a few Go Modules described below.

### SonataFlow Operator Types (api)

Every custom resource managed by the operator is exported in the module [api](api). You can use it to programmatically
create any custom type managed by the operator.
To use it, simply run:

```shell
go get github.com/kiegroup/kogito-serverless-workflow/api
```

Then you can create any type programmatically, for example:

```go
workflow := &v1alpha08.SonataFlow{
ObjectMeta: metav1.ObjectMeta{Name: w.name, Namespace: w.namespace},
Spec:       v1alpha08.SonataFlowSpec{Flow: *myWorkflowDef>}
}
```

You can use the [Kubernetes client-go library](https://github.com/kubernetes/client-go) to manipulate these objects in
the cluster.

You might need to register our schemes:

```go
    s := scheme.Scheme
utilruntime.Must(v1alpha08.AddToScheme(s))
```

### Container Builder (container-builder)

Please see the module's [README file](container-builder/README.md).

### Workflow Project Handler (workflowproj)

Please see the module's [README file](workflowproj/README.md).

## Development and Contributions

Contributing is easy, just take a look at our [contributors](docs/CONTRIBUTING.md)'guide.

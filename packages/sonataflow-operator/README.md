<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

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

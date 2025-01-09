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

## K8s YAML to APIServer Requests

Library to map Kubernetes API resources and apply resource YAMLs

## Features

- interpolateK8sResourceYamls(): Given an YAML and a tokens map, interpolate the YAML content with values provided by the token map. Tokens are represented with `${{token.path.name}}`.
- parseK8sResourceYaml(): Given a YAML file, return an array of JSON objects parsed.
- buildK8sApiServerEndpointsByResourceKind(): Generates a map of Kubernetes resources and their API endpoints.
- callK8sApiServer(): Make the requests to the Kubernetes APIServer in order to create given resources.

## How to use

Import as a library and invoke the functions described above.

## How to test

Run

```bash
pnpm start <k8sApiServerUrl> <k8sNamespace> <k8sServiceAccountToken> <k8sYamlFilepath>
```

to test changes made to this libray.

#### Example:

```bash
pnpm start https://api.to.my.openshift.cluster.com:6443 my-project sha256~MGnPXMPsi1YJkCV6kr970gQYI6KtQWztIObm3jQxUJI ../myDeployment.yaml
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

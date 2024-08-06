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

## Webapp Example

You can read [here](https://blog.kie.org/2020/10/kogito-tooling-examples%e2%80%8a-%e2%80%8ahow-to-integrate-a-custom-editor-an-existing-editors-and-custom-views.html) a step-by-step tutorial of how create this WebApp.

This is a Web application example that shows how to integrate an Embedded Editor [1] or an Embedded Envelope [2]

1. The Embedded Editor enables you to use your Custom Editors or even to use the available Kogito Editor (BPMN and DMN)
1. The Embedded Envelope gives you more flexibility to create any kind of application, in this example we bring two custom Views.

## Details

To get more the details please take a look on each implementation:

- [Base64 PNG Editor]("src/Pages/Base64Png/Base64PngPage.tsx")
- [BPMN Editor]("src/Pages/KogitoEditors/BpmnPage.tsx")
- [DMN Editor]("src/Pages/KogitoEditors/DmnPage.tsx")
- ['To-do' list View]("src/Pages/TodoList/TodoListViewPage.tsx")
- [Ping-Pong View]("src/Pages/PingPong/PingPongViewsPage.tsx")

## Setup

### Initialize

To install all dependencies it's necessary to execute the following command on the root folder of the project:

```shell script
pnpm init
```

### Build

To build the webapp execute one of the following commands on the root folder of the project:

```shell script
KIE_TOOLS_BUILD__buildExamples=true pnpm -F @kie-tools-examples/webapp... build:dev
KIE_TOOLS_BUILD__buildExamples=true pnpm -F @kie-tools-examples/webapp... build:prod
```

### Run

To start the webapp execute the following command on the root folder of the project:

```shell script
pnpm -F @kie-tools-examples/webapp start
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

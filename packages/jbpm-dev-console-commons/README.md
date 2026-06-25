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

# jBPM Dev Console Commons

Framework-agnostic code shared by the jBPM development consoles:

- [`jbpm-quarkus-devui`](../jbpm-quarkus-devui) (Quarkus Dev UI extension)
- [`jbpm-addons-springboot-dev-console`](../jbpm-addons-springboot-dev-console) (Spring Boot Dev Console)

It contains the custom forms support (`FormsStorage` and its filesystem-backed implementation, plus the form model classes
serialized by the `/forms` REST APIs) and the `User` model used to initialize the Dev UI webapp's user switcher.

The only dependencies are Jackson and SLF4J. Framework specifics stay in the consuming modules: the JAX-RS/Spring MVC REST
resources, CDI/Spring wiring, and configuration lookup (e.g. `quarkus.kogito-runtime-tools.forms.folder` on Quarkus,
`jbpm.dev-console.forms.folder` on Spring Boot) all live in the respective console packages, which pass the resolved settings
into this module's classes.

## Build

```bash
pnpm -F @kie-tools/jbpm-dev-console-commons... build:dev
```

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator.

Incubation is required of all newly accepted projects until a further review
indicates that the infrastructure, communications, and decision making process have
stabilized in a manner consistent with other successful ASF projects.

While incubation status is not necessarily a reflection of the completeness
or stability of the code, it does indicate that the project has yet to be
fully endorsed by the ASF.

Some of the incubating project's releases may not be fully compliant with ASF
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

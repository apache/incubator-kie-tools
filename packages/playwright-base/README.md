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

## @kie-tools/playwright-base

## Overview

This package collects common configurations to run end-to-end Playwright tests.

## Using containers to generate screen shots

Each OS has a slighty different UI, even for the same browser. This difference causes screnshot comparison tests to fail. To solve this problem and have a stable environment with tests passing locally and in the CI, we can take advantage of containers. Running Playwright tests inside a container that mimics the CI environment independent of the host OS can make screenshot tests reproducible.

To run the tests using containers, first is required to build the image based on this package's Containerfile. To do so, please use the `image:docker:build` script as follow:

```sh
# In this package folder
KIE_TOOLS_BUILD__buildContainerImages=true pnpm image:docker:build
# or in any folder of the kie-tools monorepo
KIE_TOOLS_BUILD__buildContainerImages=true pnpm -F @kie-tools/plawright-base image:docker:build
```

By default, the tests will run in the host machine, and to run using the container environment it will be required to set the `KIE_TOOLS_BUILD__runContainerizedEndToEndTests` environment variable to `true`.

```sh
KIE_TOOLS_BUILD__runContainerizedEndToEndTests=true pnpm test-e2e
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

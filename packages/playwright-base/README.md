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

## Installing Playwright deps

Currently, all Playwright end-to-end tests run inside containers. If you need to debug a test, we recommend doing so on the host machine. To do this, you need to install the Playwright dependencies. Use the `PLAYWRIGHT_BASE__installDeps` environment variable during the bootstrap phase to install all the required dependencies.

```sh
# in the `kie-tools` root
PLAYWRIGHT_BASE__installDeps=true pnpm bootstrap
```

or

```sh
# in the `kie-tools` root
PLAYWRIGHT_BASE__installDeps=true pnpm bootstrap -F playwright-base
```

> **i NOTE**
>
> Since this step install the Playwright browsers, it requires sudo permision.

## Using containers to generate screenshots

Each operating system has slight variations in UI, even within the same browser. These differences can cause screenshot comparison tests to fail. To address this issue and ensure a stable environment with consistent test results locally and in CI, containers can be used. Running Playwright tests inside a container that is also used in the CI environment makes screenshot tests reproducible, regardless of the host OS.

> **ℹ️ NOTE**
>
> Due to compatibility issues, this containerization solution cannot yet be used on native Windows and requires running it directly within WSL (Windows Subsystem for Linux). Also, Linux arm64 doesn't support Google Chrome, and due to this caveat, some tests will be disabled for this arch.

---

To run tests in a container, you first need to build the image using the Containerfile provided in this package. Use the `build:dev` script as shown below:

```sh
# In this package folder
KIE_TOOLS_BUILD__buildContainerImages=true pnpm build:dev
# or in any folder of the kie-tools monorepo
KIE_TOOLS_BUILD__buildContainerImages=true pnpm -F @kie-tools/playwright-base build:dev
```

By default, tests run on using containers. To execute them in the native OS environment, set the `KIE_TOOLS_BUILD__containerizedEndToEndTests` environment variable to `false`.

```sh
KIE_TOOLS_BUILD__containerizedEndToEndTests=true pnpm test-e2e
```

## Updating Playwright screenshots

By default, running tests does not automatically update screenshots. The `test-e2e` script launches the test suite with standard settings. For greater control over Playwright parameters, you can use the `test-e2e:container:shell` script, which opens a shell inside the container. This script will start the dev server (if not already running), initialize the Playwright container, and provide access to a new shell.

To update screenshots, use the `-u` or `--update-snapshots` flags with the `test-e2e:run` script as shown below:

```sh
pnpm test-e2e:container:shell

# Wait until the new shell is ready
pnpm test-e2e:run --update-snapshots
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

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

This package collects common configurations to run end-to-end Playwright tests. Currently, all Playwright end-to-end tests run inside containers. If you need to debug a test, we recommend doing so on the host machine, refer to the [Installing Playwright deps](#Installing-Playwright-deps)

## Using containers to generate screenshots

Each operating system has slight variations in UI, even within the same browser. These differences can cause screenshot comparison tests to fail. To address this issue and ensure a stable environment with consistent test results locally and in CI, containers can be used. Running Playwright tests inside a container that is also used in the CI environment makes screenshot tests reproducible, regardless of the host OS.

> **ℹ️ NOTE**
>
> Due to compatibility issues, this containerization solution cannot yet be used on native Windows and requires running it directly within WSL (Windows Subsystem for Linux). Additionally, Google Chrome is not available for Linux on ARM64, so any Chrome-based tests in an ARM64 container must be disabled. At present, tests run on AMD64 containers to ensure all browsers and test scenarios execute safely.

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
KIE_TOOLS_BUILD__containerizedEndToEndTests=false pnpm test-e2e
```

## Updating Playwright screenshots

By default, running tests does not automatically update screenshots. The `test-e2e` script launches the test suite with standard settings. For greater control over Playwright parameters, you can use the `test-e2e:container:shell` script, which opens a shell inside the container. This script will start the dev server (if not already running), initialize the Playwright container, and provide access to a new shell.

To update screenshots, use the `-u` or `--update-snapshots` flags with the `test-e2e:run` script as shown below:

```sh
pnpm test-e2e:container:shell

# Wait until the new shell is ready
pnpm test-e2e:run --update-snapshots
```

## Playwright-base CLI

A CLI tool to assist running containerized Playwright tests. It loads environment variables and passes them through the docker-compose file, and helps with starting the test suite, opening an interactive shell inside the container, and cleaning up.

```sh
pnpm playwright-base-container --help
playwright-base-container <command>

Commands:
  playwright-base-container run    Run the Playwright test suite inside Docker containers. This command will start the required containers using docker-compose
                          and execute the Playwright tests in the specified container workdir.

  Options:
      --ci                 Enable CI mode by applying the CI-specific docker-compose override file.                         [boolean] [default: false]
      --additional-env     Comma-separated KEY=VALUE pairs of additional environment variables to forward to docker-compose. Can be repeated.
                                                                                                                                [string] [default: ""]
      --container-name     Name of the container as defined in the docker-compose file. Required.                    [string] [required] [default: ""]
      --container-workdir  Path inside the container where Playwright tests are located. Example: incubator-kie-tools/packages/<package_name>.
                           Required.                                                                                 [string] [required] [default: ""]

  playwright-base-container shell  Open an interactive shell inside the Playwright test container. This command starts the required container using
                          docker-compose and launches a shell in the specified workdir inside the container.

  Options:
      --additional-env     Comma-separated KEY=VALUE pairs of additional environment variables to forward to docker-compose. Can be repeated.
                                                                                                                                [string] [default: ""]
      --container-name     Name of the container as defined in the docker-compose file. Required.                    [string] [required] [default: ""]
      --container-workdir  Path inside the container where Playwright tests are located. Example: incubator-kie-tools/packages/<package_name>.
                           Required.                                                                                 [string] [required] [default: ""]

  playwright-base-container clean  Stop and remove all Playwright-related containers created by docker-compose. This command runs 'docker compose down' using
                          the base Playwright compose file.

Examples:
  playwright-base-container run --container-name my_playwright_container        Run the Playwright test suite locally (no CI override) using the
  --container-workdir incubator-kie-tools/packages/my-package                  specified container and workdir.

  playwright-base-container run --ci --container-name my_playwright_container   Run the Playwright test suite in CI mode (applies CI docker-compose
  --container-workdir incubator-kie-tools/packages/my-package                  override).

  playwright-base-container run --container-name e2e --container-workdir        Run with extra environment variables forwarded to docker-compose
  incubator-kie-tools/packages/foo --additional-env                            (comma-separated KEY=VALUE pairs).
  BUILD_ID=123,REPORT_DIR=/tmp/reports

  playwright-base-container run --container-name e2e --container-workdir        Pass multiple --additional-env options; later pairs override earlier
  incubator-kie-tools/packages/foo --additional-env FOO=bar --additional-env   keys if duplicated.
  COMMIT_SHA=deadbeef

  CI=true playwright-base-container run --container-name e2e                    Leverage CI environment variable to auto-enable CI mode (equivalent to
  --container-workdir incubator-kie-tools/packages/foo                         --ci when CI=true or CI=1).

  playwright-base-container shell --container-name my_playwright_container      Start the container (if needed) and open an interactive bash shell in
  --container-workdir incubator-kie-tools/packages/my-package                  the package workdir.

  playwright-base-container shell --container-name e2e --container-workdir      Open an interactive shell with extra environment variables forwarded to
  incubator-kie-tools/packages/foo --additional-env DEBUG=true                 docker-compose.

  playwright-base-container shell --container-name e2e --container-workdir      Forward multiple env variables in a single --additional-env option
  incubator-kie-tools/packages/foo --additional-env FOO=bar,BAZ=qux            using comma-separated pairs.

  playwright-base-container clean                                               Stop and remove Playwright-related containers using the base
                                                                               docker-compose file.
```

## Installing Playwright dependencies on host machine

> **ℹ️ Warning**
>
> The tests screenshot comparisons will not work in an environment different than the containers, meaning, this setup is only used to debug.
> To install the Playwright dependencies use the `PLAYWRIGHT_BASE__installDeps` environment variable during the bootstrap phase to install all the required dependencies.

```sh
# in the `kie-tools` root
PLAYWRIGHT_BASE__installDeps=true pnpm bootstrap
```

or

```sh
# in the `kie-tools` root
PLAYWRIGHT_BASE__installDeps=true pnpm bootstrap -F playwright-base
```

> **ℹ️ NOTE**
>
> Since this step install the Playwright browsers, it requires sudo permision.

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

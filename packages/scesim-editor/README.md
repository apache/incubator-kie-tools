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

# Test Scenario Editor

This module hosts the Test Scenario Editor
The scope of this editor is to create, view and edit (\*.scesim) files.
Test Scenario is a technology that enables Apache KIE users who rely on the Decision Engine or the Rule Engine to
test their business logic defined in DMN or DRL files easily and efficiently.

The Test Scenario Editor still doesn't fully support the DRL test case, the following table clarifies which feature are currently supported:

| x          | DRL         | DMN |
| ---------- | ----------- | --- |
| **Create** | NO          | YES |
| **View**   | YES         | YES |
| **Edit**   | PARTIALLY\* | YES |

> \* **NOTE** At this time, DRL-based Test Scenario file can be opened and partially edited, but not created.

The editor is based on the following technology stack:

- [Typescript](https://www.typescriptlang.org/)
- [React.js](https://react.dev/)
- [Patternfly](https://www.patternfly.org/)
- [Zustand](https://zustand-demo.pmnd.rs/) + [Immer](https://immerjs.github.io/immer/) frameworks to manage the editor's state

# Project Structure

Below, a brief description of the project's structure:

| Directory            | Description                                                                                          |
| -------------------- | ---------------------------------------------------------------------------------------------------- |
| `src`                | Source root code. It contains the entrrypoint ´TestScenarioEditor` component.                        |
| `src/creation`       | It contains the Test Scenario components used to create a new scesim file (i.e an empty scesim file) |
| `src/drawer`         | It contains the Test Scenario components used in the right Drawer component of the editor            |
| `src/externalModels` | It contains resources (Context) to manage external modules (DMN files)                               |
| `src/hook`           | It contains custom hooks required for this module                                                    |
| `src/i18n`           | It contains the editor's internationalization resources                                              |
| `src/mutations`      | It contains a set of functions that mutate the editor's state                                        |
| `src/resources`      | It contains a set of resources (e.g empty scesim file)                                               |
| `src/sidebar`        | It contains the Test Scenario's left sidebar component, which acts as the editor menu                |
| `src/store`          | It contains the Test Scenario Zunstand + Immer state and related resources.                          |
| `src/store`          | It contains the Test Scenario's table component, inherited by the `boxed-expression-component`       |
| `stories`            | It contains the Storybook's stories and the Dev WebApp showcase                                      |
| `test-e2e`           | It contains the Playwright integration tests                                                         |

## How to build it

Like most of the projects of this repository, pnpm and NodeJS are mandatory to build the project. Please refer to the
repository main README file to know more about the requested versions and installation steps.

## How to launch the Test Scenario Storybook Dev WebApp

After building the project, you can benefit of the Storybook Dev Webapp for development or testing scope.
The DevApp code lives in the `stories` directory.
To launch it, simply type in your terminal the following command:

`pnpm -F @kie-tools/scesim-editor start`

A web server with a Dev Webapp of Test Scenario editor will be launched, reachable at the following address:

http://localhost:9902/ or http://172.20.10.3:9902/

## How to test it

This module relies on [Playwright](https://playwright.dev/) test framework to perform integration tests.
The tests live in the `test-e2e` directory.

> **OS different than Ubuntu 22.04**
>
> The current test cases in place are targeting Ubuntu 22.04 OS only. To run the tests in a different OS, a preliminary
> step is required. This step requires the creation of a containerized Ubuntu OS environment in which the tests will be performed.
> To create the container, run the following command:
>
> `KIE_TOOLS_BUILD__buildContainerImages=true pnpm -F @kie-tools/playwright-base image:docker:build`
>
> To have more information about this step, please refer to the [playwrite-base](https://github.com/apache/incubator-kie-tools/tree/main/packages/playwright-base) module.

To run the tests, run this command in your terminal:

`KIE_TOOLS_BUILD__runEndToEndTests=true pnpm -F @kie-tools/scesim-editor test-e2e`

In case of any test failure, Playwright will create a useful report with screenshots and videos attached, worthwhile to investigate unexpected behavior. To view the Playright report, run the following command:

`pnpm -F @kie-tools/scesim-editor test-e2e:open`

If you need to update the Playright tests' screenshots after your code changes, the following commands are required:

`pnpm -F @kie-tools/scesim-editor test-e2e:container:shell`

`pnpm -F @kie-tools/scesim-editor test-e2e:run -u`

To improve performance, you can filter which tests will run by adding the -g flag:

`pnpm -F @kie-tools/scesim-editor test-e2e:run -u -g "test name"`

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

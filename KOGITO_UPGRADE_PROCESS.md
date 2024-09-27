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

This guide aims to assist you in the process of upgrading all Kogito dependencies versions present in the kie-tools repository.
Typically, these dependencies point to backend-side modules or images

# When apply the upgrade

The upgrade should be applied soon after a new Kogito version is released.
At the writing time, it's still not planned to use pure SNAPSHOT versions, but TAGGED and TIMESTAMPED SNAPSHOT versions only.
It's mandatory to always upgrade the Kogito versions in case a new TAGGED version is released, to keep the codebase consistent when releasing kie-tools.
The approach with TIMESTAMPED SNAPSHOT may vary depending on the team's plans. These versions are automatically released weekly.
It's important to always apply the same TAG or TIMESTAMPED SNAPSHOT version in the whole repository.

# Prerequisites

The following prerequisites must be assessed before beginning the procedure:

- Retrieve the Kogito TAG version (eg. `10.0.0`) or TIMESTAMPED SNAPSHOT to apply (eg. `10.1.0-20240218-SNAPSHOT`)
- Check if the below Kogito version upgraded the Quarkus version (eg. from `3.8.0` to `3.8.1`). In that case, we need to upgrade Quarkus version in kie-tools accordingly.
- Check if the below Kogito version upgraded to the Java version (eg. from Java `17` to Java `21`). In such a case, we need to upgrade kie-tools Java modules accordingly.
- Check if the below Kogito version upgraded to the Maven version (eg. from Maven `3.9.6` to Maven `3.9.7`). In such a case, we need to upgrade kie-tools Maven modules accordingly.

# Upgrading Java & Maven

To upgrade Java and Maven versions, you should first identify all the Java modules currently present in the kie-tools guide.

In detail, the involved module's categories are:

- Jenkins and GitHub actions configuration files;
- GWT based modules;
- Images generation modules;
- Quarkus-based application (eg. extended-services-java)
- Java application (eg. vscode-java-code-completion-extension-plugin-core)
- JBang scripts (eg dmn-marshaller tests)

The suggested strategy is to check all:

- pom.xml files for Java version references (typically in maven compiler properties)
- Github Action yaml configuration files
- Images Dockerfile and Containerfile
- JBang scripts (eg. Plain .java files that start with `///usr/bin/env jbang "$0" "$@" ; exit $?`)

To double-check that all the versions are correctly updated, please perform a search with grep (or the IDE-integrated search) if the old version is still referenced somewhere. As a key, you can use:

- The version number (eg. `17` for Java and `3.9.6` for Maven);
- OpenJDK references (`openjdk`);
- Apache Maven references (`apache-maven`);
- GitHub Action references (`java-version` and `maven-version`);
- Maven compiler properties (`maven.compiler`);

Of course, new Java and Maven versions may lead to incompatibilities in the code and with other dependencies. In such a case, an investigation and eventually a fix are required to complete the process.

Don't forget to update the Java and Maven versions in this file `repo/build-dependencies-versions.json` and the `README.md` (root and submodules) as well.

You can find an example of the Java / Maven versions upgrade in [this PR](https://github.com/apache/incubator-kie-tools/pull/2182)

# Upgrading Quarkus

The Quarkus version is present in the following file categories:

- `root-env/env/index.js` file
- go test files

The best (and fastest) way to catch all the Quarkus versions is to perform a search a grep (or the IDE integrated search) and replace it with the new version. So, as a key, you can use:

- The version number (eg. `3.8.0`);

Of course, a new Quarkus version may lead to incompatibilities in the code and with other dependencies. In such a case, an investigation and eventually a fix are required to complete the process.

You can find an example of the Quarkus upgrade in [this PR](https://github.com/apache/incubator-kie-tools/pull/2193)

# Upgrading Kogito

In the root directory, run `pnpm update-kogito-version-to --maven [version]`.

Of course, a new Kogito version may lead to incompatibilities in the code and with other dependencies. In such a case, an investigation and evetually a fix is required to complete the process.

You can find an example of the Kogito version upgrade in [this PR](https://github.com/apache/incubator-kie-tools/pull/2229)

# Upgrading kie-sandbox-quarkus-accelerator

The above updates (Java, Maven, Quarkus, and Kogito) must be reflected in the `kie-sandbox-quarkus-accelerator` module, which lives in another [repo](https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator/)

You need to update the `pom.xml` file of the `0.0.0` branch with the same version you applied in the `kie-tools` repo.
To test the `kie-sandbox-quarkus-accelerator` module with the updated version, please follow these steps:

- Create a PR with the updated version in the `pom.xml` file;
- In kie-tools, `temporarily change gitRepositoryUrl` (fork link) and `gitRepositoryGitRef` (branch name) keys in `packages/online-editor/build/defaultEnvJson.ts` to point to your fork's PR (the one created in the above step)
- Run `pnpm -F @kie-tools/cors-proxy... build:dev` and `pnpm -F @kie-tools/cors-proxy start`;
- Run `pnpm -F @kie-tools/online-editor... build:dev` and `pnpm -F @kie-tools/online-editor start` to test it.

You can find an example of the Kogito version upgrade in [this PR](https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator/pull/8)

# Upgrading GraphQL schemas in `@kie-tools/runtime-tools-process-gateway-api`& `@kie-tools/runtime-tools-swf-gateway-api`

The following commands will help to sync up the gateway apis the GraphQL schema with the new Kogito Data Index GraphQL schema:

- Start a blank Data Index Container, for example `docker run -p8180:8080 docker.io/apache/incubator-kie-kogito-data-index-ephemeral:{$KOGITO_VERSION}`
- Run `pnpm -F @kie-tools/runtime-tools-process-gateway-api graphql:codegen`
- Run `pnpm -F @kie-tools/runtime-tools-swf-gateway-api graphql:codegen`

After upgrading the GraphQL schemas it is recommended to verify that the incoming changes aren't breaking the consoles or
devui's and fix any possible conflict if needed.

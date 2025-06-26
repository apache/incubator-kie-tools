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

This guide aims to assist you in the process of upgrading the Kogito version `kie-tools` depends on.

Kogito dependencies are Maven libraries, and transitively bring in Drools, OptaPlanner, and jBPM.

# When apply the upgrade

The upgrade should be applied whenever you want to make `kie-tools` more closely follow the development of other Apache KIE repositories it depends on.

# Upgrading Kogito

To make sure you're upgrading Kogito to a version that is consistent across the many repositories which `kie-tools` depends on, the best way is to use a date as referece.

With the [GitHub CLI](https://cli.github.com/) installed, you can run:

```bash

DATE='2025-06-22' # CHANGE THIS TO WHATEVER DATE YOU'D LIKE TO UPGRADE TO.
PAGER='cat'
DROOLS_SHA=$(gh search commits --author-date "<${DATE}" -R "apache/incubator-kie-drools" --sort author-date --order desc --json sha --limit 1 --jq '.[].sha')
OPTAPLANNER_SHA=$(gh search commits --author-date "<${DATE}" -R "apache/incubator-kie-optaplanner" --sort author-date --order desc --json sha --limit 1 --jq '.[].sha')
KOGITO_RUNTIMES_SHA=$(gh search commits --author-date "<${DATE}" -R "apache/incubator-kie-kogito-runtimes" --sort author-date --order desc --json sha --limit 1 --jq '.[].sha')
KOGITO_APPS_SHA=$(gh search commits --author-date "<${DATE}" -R "apache/incubator-kie-kogito-apps" --sort author-date --order desc --json sha --limit 1 --jq '.[].sha')
VERSION=$(echo "${DATE//-/}")
pnpm update-kogito-version-to --maven "999-${VERSION}-local" --droolsGitRef "${DROOLS_SHA}" --optaplannerGitRef "${OPTAPLANNER_SHA}" --kogitoRuntimesGitRef "${KOGITO_RUNTIMES_SHA}" --kogitoAppsGitRef "${KOGITO_APPS_SHA}"
```

Of course, a new Kogito version may lead to incompatibilities in the code and with other dependencies. In such a case, an investigation and evetually a fix is required to complete the process.

You can find an example of the Kogito version upgrade in [this PR](https://github.com/apache/incubator-kie-tools/pull/2229)

# Related upgrades

The following prerequisites must be assessed before beginning the procedure:

- Check if the below Kogito version upgraded the **Quarkus version** (E.g., from `3.8.0` to `3.8.1`). In that case, we need to upgrade Quarkus version in `kie-tools` accordingly.
- Check if the below Kogito version upgraded to the **Java version** (E.g., from Java `17` to Java `21`). In such a case, we need to upgrade `kie-tools` Java modules accordingly.
- Check if the below Kogito version upgraded to the **Maven version** (E.g., from Maven `3.9.6` to Maven `3.9.7`). In such a case, we need to upgrade `kie-tools` Maven modules accordingly.

## Upgrading Quarkus

The Quarkus version is present in the following file categories:

- `root-env/env/index.js` file
- go test files

The best (and fastest) way to catch all the Quarkus versions is to perform a search a grep (or the IDE integrated search) and replace it with the new version. So, as a key, you can use:

- The version number (E.g., `3.8.0`);

Of course, a new Quarkus version may lead to incompatibilities in the code and with other dependencies. In such a case, an investigation and eventually a fix are required to complete the process.

You can find an example of the Quarkus upgrade in [this PR](https://github.com/apache/incubator-kie-tools/pull/2193)

## Upgrading Java & Maven

To upgrade Java and Maven versions, you should first identify all the Java modules currently present in the `kie-tools` guide.

In detail, the involved module's categories are:

- Jenkins and GitHub actions configuration files;
- GWT based modules;
- Images generation modules;
- Quarkus-based application (E.g., extended-services-java)
- Java application (E.g., vscode-java-code-completion-extension-plugin-core)
- JBang scripts (E.g., dmn-marshaller tests)

The suggested strategy is to check all:

- pom.xml files for Java version references (typically in maven compiler properties)
- Github Action yaml configuration files
- Images Dockerfile and Containerfile
- JBang scripts (E.g., Plain .java files that start with `///usr/bin/env jbang "$0" "$@" ; exit $?`)

To double-check that all the versions are correctly updated, please perform a search with grep (or the IDE-integrated search) if the old version is still referenced somewhere. As a key, you can use:

- The version number (E.g., `17` for Java and `3.9.6` for Maven);
- OpenJDK references (`openjdk`);
- Apache Maven references (`apache-maven`);
- GitHub Action references (`java-version` and `maven-version`);
- Maven compiler properties (`maven.compiler`);

Of course, new Java and Maven versions may lead to incompatibilities in the code and with other dependencies. In such a case, an investigation and eventually a fix are required to complete the process.

Don't forget to update the Java and Maven versions in this file `repo/build-dependencies-versions.json` and the `README.md` (root and submodules) as well.

You can find an example of the Java / Maven versions upgrade in [this PR](https://github.com/apache/incubator-kie-tools/pull/2182)

## Upgrading GraphQL schemas in `@kie-tools/runtime-tools-process-gateway-api` & `@kie-tools/runtime-tools-swf-gateway-api`

The following commands will help to sync up the gateway apis the GraphQL schema with the new Kogito Data Index GraphQL schema:

- Start a blank Data Index Container, for example `docker run -p8180:8080 docker.io/apache/incubator-kie-kogito-data-index-ephemeral:{$KOGITO_VERSION}`
- Run `pnpm -F @kie-tools/runtime-tools-process-gateway-api graphql:codegen`
- Run `pnpm -F @kie-tools/runtime-tools-swf-gateway-api graphql:codegen`

After upgrading the GraphQL schemas it is recommended to verify that the incoming changes aren't breaking the consoles or devui's and fix any possible conflict if needed.

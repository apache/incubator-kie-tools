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

<p align="center"><img width=40% src="docs/kie.svg"></p>

---

**[Apache KIE](http://kie.apache.org)** is a home for leading Open Source projects that play a role in delivering solutions around Business Automation and Artificial Intelligence in the Cloud.

[![GitHub Stars](https://img.shields.io/github/stars/apache/incubator-kie-tools.svg)](https://github.com/apache/incubator-kie-tools/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/apache/incubator-kie-tools.svg)](https://github.com/apache/incubator-kie-tools/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/apache/incubator-kie-tools.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/apache/incubator-kie-tools.svg?style=flat-square)](https://github.com/apache/incubator-kie-tools/pulls)
[![Contributors](https://img.shields.io/github/contributors/apache/incubator-kie-tools.svg?style=flat-square)](https://github.com/apache/incubator-kie-tools/graphs/contributors)
[![License](https://img.shields.io/github/license/apache/incubator-kie-tools.svg)](https://github.com/apache/incubator-kie-tools/blob/main/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/KieCommunity.svg?label=Follow&style=social)](https://twitter.com/KieCommunity?lang=en)

This repository contains tooling applications and libraries for Apache KIE projects.

## Contribute

- _Work in progress 🔨_

## Build from source

#### Step 0: Install the necessary tools

> **💡 RECOMMENDED**
>
> **Nix development environment**: A _devbox_ configuration is provided to automatically setup all the tools below. Read more in [here](./repo/NIX_DEV_ENV.md).

To build and test all packages on this repository, you're going to need:

- Node.js `22` _(To install, follow these instructions: https://nodejs.org/en/download/package-manager/)_
- pnpm `9.3.0` _(To install, follow these instructions: https://pnpm.io/installation#using-npm)_
- Maven `3.9.6`
- Java `17`
- Go `1.22.12` _(To install, follow these instructions: https://go.dev/doc/install)_
- Python `3.12` _(To install, follow these instructions: https://www.python.org/downloads/)_
- Helm `3.13.3` _(To install, follow these instructions: https://helm.sh/docs/intro/install/)_
- Make
- xmllint _(To install, follow these instructions: https://www.baeldung.com/linux/xmllint)_
- bash `5.x` _(On Linux or Nix you should be fine. On macOS, follow these instructions to use zsh: https://support.apple.com/102360)_

> **ℹ️ NOTE**
>
> If you plan on building container images, make sure you have a working Docker setup. Setting `KIE_TOOLS_BUILD__buildContainerImages=true` will also be necessary.

#### Step 1: Bootstrap

Installs the necessary 3rd party dependencies and links packages of this repository together.

- `pnpm bootstrap` --> Will bootstrap all packages
- `pnpm bootstrap [pnpm-filter]` --> Will bootstrap packages filtered by [`pnpm`-filter](https://pnpm.io/filtering)
- > E.g.,
  >
  > `pnpm bootstrap -F dmn-editor...` bootstraps the `dmn-editor` package and its dependencies.

#### Step 2: Build

- Dev

  - `pnpm -r build:dev`
    - Will build all packages for development. Skipping linters, tests, minifiers etc.
  - `pnpm [pnpm-filter] build:dev`
    - Will build packages filtered by [`pnpm`-filter](https://pnpm.io/filtering)
  - > E.g.,
    >
    > `pnpm -F dmn-editor... build:dev` builds the `dmn-editor` package and its dependencies.

- Prod

  - `pnpm -r build:prod`
    - Will build all packages for production. Optimizers will run, binaries will be produced for multiple architectures etc.
  - `pnpm [pnpm-filter] build:prod`
    - Will build packages filtered by [`pnpm`-filter](https://pnpm.io/filtering)
  - > E.g.,
    >
    > `pnpm -F dmn-editor... build:prod` builds the `dmn-editor` package and its dependencies.

- Local changes
  - `pnpm run on-affected [cmd]` (_alias for `pnpm -F '...[HEAD]'`_); or
  - `pnpm run on-affected-only [cmd]` (_alias for `pnpm -F '...^[HEAD]'`_); or
  - `pnpm run on-changed [cmd]` (_alias for `pnpm -F '[HEAD]'`_); or
  - `pnpm run on-changed-deps-only [cmd]` (_alias for `pnpm -F '[HEAD]^...'`_);
  - > E.g.,
    >
    > If you have local changes (staged or unstaged) done to the `dmn-editor` package:
    >
    > - `pnpm run on-affected build:dev`
    >   - builds the `dmn-editor` package and all packages that depend on it.
    > - `pnpm run on-affected-only build:dev`
    >   - doesn't build the `dmn-editor` package, but builds all packages that depend on it.
    > - `pnpm run on-changed build:dev`
    >   - builds the `dmn-editor` package and nothing else.
    > - `pnpm run on-changed-deps-only build:dev`
    >   - doesn't build the `dmn-editor` package, but builds all packages that it depends on.

> **ℹ️ NOTE**
>
> This repository's build is parameterized by several Environment Variables. For an extensive list of these variables, please see the list printed by the `bootstrap` step.
>
> - To enable Examples build: `export KIE_TOOLS_BUILD__buildExamples=true`
> - To enable Container images build: `export KIE_TOOLS_BUILD__buildContainerImages=true`
> - To enable E2E tests: `export KIE_TOOLS_BUILD__runEndToEndTests=true`

> **ℹ️ NOTE**
>
> Ubuntu 22.04 is the only OS that nativelly supports running E2E tests and by default the E2E tests will run using a Docker container. To run the tests natively in your OS please install the Playwright dependencies during the Bootstrap phase by adding the `PLAYWRIGHT_BASE__installDeps=true` environment variable. Additionally, tweak the containerized tests variable to `false` (`KIE_TOOLS_BUILD__containerizedEndToEndTests=false`). Please refer to @kie-tools/playwright-base [README](./packages/playwright-base/README.md).

> **ℹ️ NOTE**
>
> Final artifacts will be in `{packages,examples}/*/dist` directories.

> **ℹ️ NOTE**
>
> For more information about how this repository works, please refer to [the `kie-tools` Manual](./repo/MANUAL.md)

---

## Reproducible Builds for _maven-based_ packages

It is mandatory that any _Maven-based_ package that releases artifacts runs [Reproducible Builds](https://reproducible-builds.org/)
to build it's artifacts, in this case, in our `build:prod` scripts.

`@kie-tools/maven-base` provides the `reproducible-build` profile to enable _Reproducible Builds_ in our builds.
To use it follow the steps:

- Make sure the `package.json` depends on `@kie-tools/maven-base`:

```json
{
  "dependencies": {
    "@kie-tools/maven-base": "workspace:*"
  }
}
```

- Make sure the package `pom.xml` has `kie-tools-maven-base` as a parent and declares the `project.build.outputTimestamp` property like:

```xml
<project>
  <parent>
    <groupId>org.kie</groupId>
    <artifactId>kie-tools-maven-base</artifactId>
    <version>${revision}</version>
    <relativePath>./node_modules/@kie-tools/maven-base/pom.xml</relativePath>
  </parent>
  ...
  <properties>
    <project.build.outputTimestamp>2024-01-12T00:00:00Z</project.build.outputTimestamp>
  </properties>
  ...
<projec>
```

- In your `package.json` scripts, enable the _Reproducible Build_ profile adding the `-Dreproducible` argument in `build:prod` scripts, like:

```json
{
  "scripts": {
    "build:prod": "pnpm lint && run-script-os",
    "build:prod:darwin:linux": "mvn clean deploy [...other maven options...] -Dreproducible",
    "build:prod:win32": "pnpm powershell \"mvn clean deploy [...other maven options...] `-Dreproducible\""
  }
}
```

> IMPORTANT: the current version of the `maven-artifact-plugin` (3.4.1) used in `kie-tools` bans the `maven-flatten-plugin` that
> we use to generate deployable artifacts using the dynamic `${revision}` variable. You can check the full list of banned
> plugins [here](https://maven.apache.org/plugins-archives/maven-artifact-plugin-3.4.1/plugin-issues.html).
> The issue that caused the ban [flatten-maven-plugin/issues/256](https://github.com/mojohaus/flatten-maven-plugin/issues/256) was a result
> of change in Maven behaviour between `v3.8.1` and `v3.8.2`, and isn't a problem on the `maven-flatten-plugin`.
> Actually, in later versions of the `maven-artifact-plugin` the ban got revoked.
> Having this in mind, and due to the fact that `kie-tools` requires newer Maven versions, our _Reproducible Builds_ require
> temporarily overriding the list of banned plugins, until we upgrade to a newer `maven-artifact-plugin` version.
> This will be addressed by https://github.com/apache/incubator-kie-issues/issues/1371

---

## Applications

This repository contains several applications. To develop each one of them individually, refer to the instructions below.

#### VS Code Extension (DMN, BPMN, SceSim, and PMML Editors)

1. After you've successfully built the project following the instructions above, open the `packages/kie-editors-dev-vscode-extension` folder on VS Code. Use a new VS Code window so that the `packages/kie-editors-dev-vscode-extension` folder shows up as root in the VS Code explorer.
2. From there, you can Run the extension or the end-to-end tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **NOTE:** To run the VS Code extension in development mode, you need `webpack` and `webpack-cli` to be globally installed on NPM. Normally you can do that with `npm install -g webpack@^5.94.0 webpack-cli@^4.10.0`, but `sudo` may be required depending on your installation.
4. **Remember!** If you make changes to any package other than `packages/kie-editors-dev-vscode-extension`, you have to manually rebuild them before relaunching the extension on VS Code.

#### VS Code Extension (Serverless Workflow Editor)

1. After you've successfully built the project following the instructions above, open the `packages/serverless-workflow-vscode-extension` folder on VS Code. Use a new VS Code window so that the `packages/serverless-workflow-vscode-extension` folder shows up as root in the VS Code explorer.
1. From there, you can Run the extension or the end-to-end tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
1. **Remember!** If you make changes to any package other than `packages/serverless-workflow-vscode-extension`, you have to manually rebuild them before relaunching the extension on VS Code.

#### Chrome Extension (DMN, BPMN, and SceSim Editors)

1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-pack-kogito-kie-editors` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
2. Run `pnpm build:dev` on `packages/chrome-extension-pack-kogito-kie-editors`. This will create a version of the Chrome Extension that fetches the envelope locally.
3. Open a terminal and run `pnpm start` on `packages/chrome-extension-pack-kogito-kie-editors`. This will start a `webpack serve` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
4. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#temporary-unexpire-flags-m118` in your Chrome browser, enable this flag and restart browser. Then go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable also this flag. Alternativelly, you can go to `https://localhost:9001` and add an exception.
5. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" in the top-right corner and click on "Load unpacked". Choose the `packages/chrome-extension-pack-kogito-kie-editors/dist` folder.
6. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

#### Chrome Extension (Serverless Workflow Editor)

1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-serverless-workflow-editor` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
1. Run `pnpm build:dev` on `packages/chrome-extension-serverless-workflow-editor`. This will create a version of the Chrome Extension that fetches the envelope locally.
1. Open a terminal and run `pnpm start` on `packages/chrome-extension-serverless-workflow-editor`. This will start a `webpack serve` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
1. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#temporary-unexpire-flags-m118` in your Chrome browser, enable this flag and restart browser. Then go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable also this flag. Alternativelly, you can go to `https://localhost:9000` and add an exception.
1. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" in the top-right corner and click on "Load unpacked". Choose the `packages/chrome-extension-serverless-workflow-editor/dist` folder.
1. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

#### KIE Sandbox

1. After you've successfully built the project following the instructions above, go to `packages/online-editor`.
2. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Online Editor resources.
3. From now on you can use the development version of the Online Editor by accessing `https://localhost:9001`.
4. Run the CORS Proxy by running `pnpm start` at `packages/cors-proxy`.

#### Serverless Logic Web Tools

1. After you've successfully built the project following the instructions above, go to `packages/serverless-logic-web-tools`.
2. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Serverless Logic Web Tools resources.
3. From now on you can use the development version of the Serverless Logic Web Tools by accessing `https://localhost:9020`.
4. Run the CORS Proxy by running `pnpm start` at `packages/cors-proxy`.
5. (Optional) To try the "Runtime Tools" functionalities, run the SonataFlow Dev App by running `pnpm start` at `packages/sonataflow-dev-app`, then open `https://localhost:9020/#/settings/runtime-tools` and set `http://localhost:4000/graphql` in the "Data Index URL" field

#### Standalone Editors (DMN and BPMN)

1. After you've successfully built the project following the instructions above, go to `packages/kie-editors-standalone`.
2. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Standalone Editors test page.
3. From now on you can use the development version of the Standalone DMN Editor by accessing `https://localhost:9001/resources/dmn` and the Standalone BPMN Editor by accessing `https://localhost:9001/resources/bpmn`.

#### Knative Workflow plugin

[Read the documentation](./packages/kn-plugin-workflow/README.md)

## Libraries

#### Stunner Editors

The `stunner-editors` package contains the BPMN, DMN, and SceSim Editors that are used in many applications of this repository.
After cloning the repo, start with a fresh build.

- `pnpm bootstrap -F @kie-tools/stunner-editors...`

- `pnpm -F @kie-tools/stunner-editors... build:dev`

After that, you're ready to start developing the Editors individually.

- BPMN

  - Located at `packages/stunner-editors/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime`.
  - Run `mvn clean gwt:run` to start. To enable live-reloading capability, run `mvn clean gwt:run -Phot-reload`

- DMN

  - Located at `packages/stunner-editors/kie-wb-common-dmn/kie-wb-common-dmn-webapp-kogito-runtime`.
  - Run `mvn clean gwt:run` to start. To enable live-reloading capability, run `mvn clean gwt:run -Phot-reload`
  - If you want to enable live-reloading capabilities of the React components that are part of the DMN Editor, follow [these steps](./packages/stunner-editors/docs/live-reload-dmn-loader.md).

- Test Scenario (SceSim)

  - Located at `packages/stunner-editors/drools-wb-screens/drools-wb-scenario-simulation-editor/drools-wb-scenario-simulation-editor-kogito-testing`.
  - Run `mvn clean gwt:run` to start.

<p align="center"><img width=40% src="docs/kie.svg"></p>

---

The **[KIE Community](http://kie.org)** is a home for leading Open Source projects that play a role in delivering solutions around Business Automation and Artificial Intelligence in the Cloud.

[![GitHub Stars](https://img.shields.io/github/stars/apache/incubator-kie-tools.svg)](https://github.com/apache/incubator-kie-tools/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/apache/incubator-kie-tools.svg)](https://github.com/apache/incubator-kie-tools/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/apache/incubator-kie-tools.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/apache/incubator-kie-tools.svg?style=flat-square)](https://github.com/apache/incubator-kie-tools/pulls)
[![Contributors](https://img.shields.io/github/contributors/apache/incubator-kie-tools.svg?style=flat-square)](https://github.com/apache/incubator-kie-tools/graphs/contributors)
[![License](https://img.shields.io/github/license/apache/incubator-kie-tools.svg)](https://github.com/apache/incubator-kie-tools/blob/main/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/KieCommunity.svg?label=Follow&style=social)](https://twitter.com/KieCommunity?lang=en)

This repository contains tooling applications and libraries for KIE projects.

## Contribute

- _Work in progress 🔨_

## Build from source

#### Step 0: Install the necessary tools

> **💡 RECOMMENDED**
>
> **Nix development environment**: A _devbox_ configuration is provided to automatically setup all the tools below. Read more in [here](./NIX_DEV_ENV.md).

To build and test all packages of the Apache KIE Tools project, you're going to need:

- Node `18` _(To install, follow these instructions: https://nodejs.org/en/download/package-manager/)_
- pnpm `8.7.0` _(To install, follow these instructions: https://pnpm.io/installation#using-npm)_
- Maven `3.9.6`
- Java `17`
- Go `1.21.9` _(To install, follow these instructions: https://go.dev/doc/install)_
- Python `3.12` _(To install, follow these instructions: https://www.python.org/downloads/)_
- Helm `3.13.3` _(To install, follow these instructions: https://helm.sh/docs/intro/install/)_
- Make

> **ℹ️ NOTE**
>
> If you plan on building container images, make sure you have a working Docker setup. Setting `KIE_TOOLS_BUILD__buildContainerImages=true` will also be necessary.

#### Step 1: Bootstrap

Bootstrapping installs the necessary dependencies for each package.

- `pnpm bootstrap` --> Will bootstrap all packages
- `pnpm bootstrap [pnpm-filter]` --> Will bootstrap packages filtered by [`pnpm` filter](https://pnpm.io/filtering)
- > E.g.,
  >
  > `pnpm bootstrap -F dmn-editor...` bootstraps the `dmn-editor` package and its dependencies.

> **ℹ️ NOTE**
>
> If you plan on running Playwright tests, set the `PLAYWRIGHT_BASE__installDeps` environment variable to `true` before running the command above.
>
> `PLAYWRIGHT_BASE__installDeps=true pnpm bootstrap`.
>
> This will install all Playwright dependencies (such as browsers engines and OS-specific libraries).

#### Step 2: Build

- Dev

  - `pnpm -r build:dev`
    - Will build all packages for development. Skipping linters, tests, minifiers etc.
  - `pnpm [pnpm-filter] build:dev`
    - Will build packages filtered by [`pnpm` filter](https://pnpm.io/filtering)
  - > E.g.,
    >
    > `pnpm -F dmn-editor... build:dev` builds the `dmn-editor` package and its dependencies.

- Prod

  - `pnpm -r build:prod`
    - Will build all packages for production. Optimizers will run, binaries will be produced for multiple architectures etc.
  - `pnpm [pnpm-filter] build:prod`
    - Will build packages filtered by [`pnpm` filter](https://pnpm.io/filtering)
  - > E.g.,
    >
    > `pnpm -F dmn-editor... build:prod` builds the `dmn-editor` package and its dependencies.

- Changed
  - `pnpm -F '...[HEAD]' build:dev`; or
  - `pnpm -F '...[HEAD]' build:prod`
    - Will build changed and affected packages based on your local changes. Useful for verifying that you didn't break anything.

> **ℹ️ NOTE**
>
> The Apache KIE Tools build is parameterized by several Environment Variables. For an extensive list of these variables, please see the list printed by the `bootstrap` step.
>
> To enable the examples build: `export KIE_TOOLS_BUILD__buildExamples=true`
> To enable container images build: `export KIE_TOOLS_BUILD__buildContainerImages=true`
> To enable E2E tests: `export KIE_TOOLS_BUILD__runEndToEndTests=true`

> **ℹ️ NOTE**
>
> Final artifacts will be in `{packages,examples}/*/dist` directories.

---

## Applications

The Apache KIE Tools project contains several applications. To develop each one of them individually, refer to the instructions below.

#### VS Code Extension (DMN, BPMN, SceSim, and PMML Editors)

1. After you've successfully built the project following the instructions above, open the `packages/kie-editors-dev-vscode-extension` folder on VS Code. Use a new VS Code window so that the `packages/kie-editors-dev-vscode-extension` folder shows up as root in the VS Code explorer.
2. From there, you can Run the extension or the end-to-end tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **NOTE:** To run the VS Code extension in development mode, you need `webpack` and `webpack-cli` to be globally installed on NPM. Normally you can do that with `npm install -g webpack@^5.88.2 webpack-cli@^4.10.0`, but `sudo` may be required depending on your installation.
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
1. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Serverless Logic Web Tools resources.
1. From now on you can use the development version of the Serverless Logic Web Tools by accessing `https://localhost:9020`.
1. Run the CORS Proxy by running `pnpm start` at `packages/cors-proxy`.

#### Standalone Editors (DMN and BPMN)

1. After you've successfully built the project following the instructions above, go to `packages/kie-editors-standalone`.
2. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Standalone Editors test page.
3. From now on you can use the development version of the Standalone DMN Editor by accessing `https://localhost:9001/resources/dmn` and the Standalone BPMN Editor by accessing `https://localhost:9001/resources/bpmn`.

#### Knative Workflow plugin

[Read the documentation](./packages/kn-plugin-workflow/README.md)

## Libraries

#### Stunner Editors

The `stunner-editors` package contains the BPMN, DMN, and SceSim Editors that are used in many applications of Apache KIE Tools.
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

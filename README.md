<p align="center"><img width=40% src="docs/kie.svg"></p>

---

The **[KIE Community](http://kie.org)** is a home for leading Open Source projects that play a role in delivering solutions around Business Automation and Artificial Intelligence in the Cloud.

[![GitHub Stars](https://img.shields.io/github/stars/kiegroup/kie-tools.svg)](https://github.com/kiegroup/kie-tools/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/kiegroup/kie-tools.svg)](https://github.com/kiegroup/kie-tools/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/kiegroup/kie-tools.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/kiegroup/kie-tools.svg?style=flat-square)](https://github.com/kiegroup/kie-tools/pulls)
[![Contributors](https://img.shields.io/github/contributors/kiegroup/kie-tools.svg?style=flat-square)](https://github.com/kiegroup/kie-tools/graphs/contributors)
[![License](https://img.shields.io/github/license/kiegroup/kie-tools.svg)](https://github.com/kiegroup/kie-tools/blob/main/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/KieCommunity.svg?label=Follow&style=social)](https://twitter.com/KieCommunity?lang=en)

This repository contains tooling applications and libraries for KIE projects.

## Contribute

- _Work in progress 🔨_

## Build from source

To start building the KIE Tools project, you're going to need:

- Node `16` _(To install, follow these instructions: https://nodejs.org/en/download/package-manager/)_
- pnpm `7.0.0` _(To install, follow these instructions: https://pnpm.io/installation)_
- Maven `3.8.6`
- Java `11`
- Go `1.19` _(To install, follow these instructions: https://go.dev/doc/install)_

> **ℹ️ NOTE:** Some packages will require that `make` is available as well.

> **ℹ️ NOTE:** \*nix users will also need:
>
> - `lib-gtk-3-dev`
> - `appindicator3-0.1` (`libappindicator3-dev` and `gir1.2-appindicator3-0.1`)

> **ℹ️ NOTE:** Users of Fedora or RHEL will need to add a repository:
>
> `sudo yum install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm`

After installing the tools above, you'll need to download the dependencies and link the packages locally. Simply run:

- `pnpm bootstrap`

To install only the dependencies that are relevant to the package called `[pkg-name]`.

- `pnpm bootstrap -F [pkg-name]...`

  > **⚠️ NOTE:** Here, `...` is actually **necessary**! They're part of a [`pnpm` filter](https://pnpm.io/filtering#--filter-package_name-1).

After dependencies are installed, you'll be able to build. To do so, you'll have two choices - `dev`, or `prod`.

Note that it is recommended that you specify which package you want to build, so replace `[pkg-name]` with the name of the desired package on one of the commands below:

- `pnpm -F [pkg-name]... build:dev` - This is fast, but not as strict. It skips tests, linters, and some type checks. Be prepared for the CI to fail on your PRs.
- `pnpm -F [pkg-name]... build:prod` - The default command to build production-ready packages. Use that to make sure your changes are correct.

> **⚠️ NOTE:** Here, `...` is actually **necessary**! They're part of a [`pnpm` filter](https://pnpm.io/filtering#--filter-package_name-1).

> **ℹ️ NOTE:** If you want to build _everything_, run `pnpm -r build:dev` or `pnpm -r build:prod`. It's going to take a while, though :)

> **ℹ️ NOTE:** The KIE Tools build is parameterized by several Environment Variables. For an extensive list of these variables, please see the list printed by the `bootstrap` script.

> **ℹ️ NOTE:** Final artifacts will be on `{packages,examples}/*/dist` directories.

## Applications

The KIE Tools project contains several applications. To develop each one of them individually, refer to the instructions below.

#### VS Code Extension (DMN, BPMN, SceSim, and PMML Editors)

1. After you've successfully built the project following the instructions above, open the `packages/vscode-extension-pack-kogito-kie-editors` folder on VS Code. Use a new VS Code window so that the `packages/vscode-extension-pack-kogito-kie-editors` folder shows up as root in the VS Code explorer.
2. From there, you can Run the extension or the integration tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **NOTE:** To run the VS Code extension in development mode, you need `webpack` and `webpack-cli` to be globally installed on NPM. Normally you can do that with `npm install -g webpack@^5.36.2 webpack-cli@^4.7.0`, but `sudo` may be required depending on your installation.
4. **Remember!** If you make changes to any package other than `packages/vscode-extension-pack-kogito-kie-editors`, you have to manually rebuild them before relaunching the extension on VS Code.

#### VS Code Extension (Serverless Workflow Editor)

1. After you've successfully built the project following the instructions above, open the `packages/vscode-extension-serverless-workflow-editor` folder on VS Code. Use a new VS Code window so that the `packages/vscode-extension-serverless-workflow-editor` folder shows up as root in the VS Code explorer.
1. From there, you can Run the extension or the integration tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
1. **Remember!** If you make changes to any package other than `packages/vscode-extension-serverless-workflow-editor`, you have to manually rebuild them before relaunching the extension on VS Code.

#### Chrome Extension (DMN, BPMN, and SceSim Editors)

1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-pack-kogito-kie-editors` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
2. Run `pnpm build:dev` on `packages/chrome-extension-pack-kogito-kie-editors`. This will create a version of the Chrome Extension that fetches the envelope locally.
3. Open a terminal and run `pnpm start` on `packages/chrome-extension-pack-kogito-kie-editors`. This will start a `webpack serve` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
4. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable this flag. Alternativelly, you can go to `https://localhost:9001` and add an exception.
5. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" in the top-right corner and click on "Load unpacked". Choose the `packages/chrome-extension-pack-kogito-kie-editors/dist` folder.
6. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

#### Chrome Extension (Serverless Workflow Editor)

1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-serverless-workflow-editor` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
1. Run `pnpm build:dev` on `packages/chrome-extension-serverless-workflow-editor`. This will create a version of the Chrome Extension that fetches the envelope locally.
1. Open a terminal and run `pnpm start` on `packages/chrome-extension-serverless-workflow-editor`. This will start a `webpack serve` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
1. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable this flag. Alternativelly, you can go to `https://localhost:9000` and add an exception.
1. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" in the top-right corner and click on "Load unpacked". Choose the `packages/chrome-extension-serverless-workflow-editor/dist` folder.
1. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

#### KIE Sandbox

1. After you've successfully built the project following the instructions above, go to `packages/online-editor`.
2. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Online Editor resources.
3. From now on you can use the development version of the Online Editor by accessing `https://localhost:9001`.
4. Run the Git CORS Proxy by running `pnpm start` at `packages/git-cors-proxy-image`.

#### Serverless Logic Web Tools

1. After you've successfully built the project following the instructions above, go to `packages/serverless-logic-sandbox`.
1. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Serverless Logic Web Tools resources.
1. From now on you can use the development version of the Serverless Logic Web Tools by accessing `https://localhost:9020`.

#### Desktop app (DMN and BPMN)

1. After you've successfully built the project following the instructions above, go to `packages/desktop`.
2. To start the application in development mode, you can run `pnpm start`. If you make changes and want to reload the app, run `pnpm build:dev && pnpm start`. This will recompile the module and restart the Electron app. Remember: if you make changes to other modules, you have to build them too!
3. To build and package the application for production (i.e. generating an executable), you can run `pnpm build:prod`. This will pack the application for the current OS. If you want to pack the application for a different OS, run `pnpm pack:linux`, for example. See `package.json` for more details.

#### Standalone Editors (DMN and BPMN)

1. After you've successfully built the project following the instructions above, go to `packages/kie-editors-standalone`.
2. Open a terminal and run `pnpm start`. This will start a `webpack serve` instance with the Standalone Editors test page.
3. From now on you can use the development version of the Standalone DMN Editor by accessing `https://localhost:9001/resources/dmn` and the Standalone BPMN Editor by accessing `https://localhost:9001/resources/bpmn`.

#### Knative Workflow plugin

[Read the documentation](./packages/kn-plugin-workflow/README.md)

## Libraries

#### Stunner Editors

The `stunner-editors` package contains the BPMN, DMN, and SceSim Editors that are used in many applications of KIE Tools.
After cloning the repo, start with a fresh build.

- `pnpm bootstrap -F @kie-tools/stunner-editors...`

- `pnpm -F @kie-tools/stunner-editors... build:dev`

After that, you're ready to start developing the Editors individually.

- BPMN

  - Located at `packages/stunner-editors/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime`.
  - Run `mvn clean gwt:run` to start.

- DMN

  - Located at `packages/stunner-editors/kie-wb-common-dmn/kie-wb-common-dmn-webapp-kogito-testing`.
  - Run `mvn clean gwt:run` to start.
  - If you want to enable live-reloading capabilities of the React components that are part of the DMN Editor, follow [these steps](./packages/stunner-editors/docs/live-reload-dmn-loader.md).

- Test Scenario (SceSim)

  - Located at `packages/stunner-editors/drools-wb-screens/drools-wb-scenario-simulation-editor/drools-wb-scenario-simulation-editor-kogito-testing`.
  - Run `mvn clean gwt:run` to start.

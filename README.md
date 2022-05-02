<p align="center"><img width=55% src="docs/kogito.png"></p>

# KIE Tools

**[Kogito](http://kogito.kie.org)** is the next generation of business automation platform focused on cloud-native development, deployment and execution.

This repository contains all the tooling artifacts of the Kogito project.

[![GitHub Stars](https://img.shields.io/github/stars/kiegroup/kie-tools.svg)](https://github.com/kiegroup/kie-tools/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/kiegroup/kie-tools.svg)](https://github.com/kiegroup/kie-tools/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/kiegroup/kie-tools.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/kiegroup/kie-tools.svg?style=flat-square)](https://github.com/kiegroup/kie-tools/pulls)
[![Contributors](https://img.shields.io/github/contributors/kiegroup/kie-tools.svg?style=flat-square)](https://github.com/kiegroup/kie-tools/graphs/contributors)
[![License](https://img.shields.io/github/license/kiegroup/kie-tools.svg)](https://github.com/kiegroup/kie-tools/blob/main/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/kogito_kie.svg?label=Follow&style=social)](https://twitter.com/kogito_kie?lang=en)

## Contribute

- _Work in progress 🔨_

## Build from source

To start building the KIE Tools project, you're going to need:

- Node `>= 16.13.2` _(To install, follow these instructions: https://nodejs.org/en/download/package-manager/)_
- Yarn `1.22.10` _(To install, run `npm install -g yarn@1.22.10`)_
- pnpm `7.0.0` _(To install, run `npm install -g pnpm@7.0.0`)_
- Maven `3.8.1`
- Java `11`
- Go `1.16`

\*nix users will also need:

- `lib-gtk-3-dev`
- `appindicator3-0.1` (`libappindicator3-dev` and `gir1.2-appindicator3-0.1`)

Users of Fedora or RHEL will also need to add an additional repository:

`sudo yum install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm`

After installing the tools above, you'll need to download the dependencies and link the packages locally. Simply run:

- `yarn bootstrap`

To build it, you'll have two choices. Note that you always need to specify which package you want to build, so replace `[pkg-name]` with the name of the desired package.

- `yarn build:dev:until [pkg-name]` - This is fast, but not as strict. It skips tests, linters, and some type checks. Be prepared for the CI to fail on your PRs.
- `yarn build:prod:until [pkg-name]` - The default command to build production-ready packages. Use that to make sure your changes are correct.

**NOTE:** The KIE Tools build is parameterized by several Environment Variables. For an extensive list of these variables, please see `packages/build-env/README.md` ([link](/packages/build-env/index.js#L24)).

Final artifacts will be on `packages/*/dist` directories.

## Applications

The KIE Tools project contains several applications. To develop each one of them individually, refer to the instructions below.

#### VS Code Extension

1. After you've successfully built the project following the instructions above, open the `packages/vscode-extension-pack-kogito-kie-editors` folder on VS Code. Use a new VS Code window so that the `packages/vscode-extension-pack-kogito-kie-editors` folder shows up as root in the VS Code explorer.
2. From there, you can Run the extension or the integration tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **NOTE:** To run the VS Code extension in development mode, you need `webpack` and `webpack-cli` to be globally installed on NPM. Normally you can do that with `npm install -g webpack@4.41.2 webpack-cli@3.3.10`, but `sudo` may be required depending on your installation.
4. **Remember!** If you make changes to packages other than `packages/vscode-extension-pack-kogito-kie-editors`, you have to manually rebuild them before relaunching the extension on VS Code.

#### Chrome Extension

1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-pack-kogito-kie-editors` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
2. Run `yarn build:dev` on `packages/chrome-extension-pack-kogito-kie-editors`. This will create a version of the Chrome Extension that fetches the envelope locally.
3. Open a terminal and run `yarn start` on `packages/chrome-extension-pack-kogito-kie-editors`. This will start a `webpack serve` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
4. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable this flag. Alternativelly, you can go to `https://localhost:9001` and add an exception.
5. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" in the top-right corner and click on "Load unpacked". Choose the `packages/chrome-extension-pack-kogito-kie-editors/dist` folder.
6. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

#### Online Editor

1. After you've successfully built the project following the instructions above, go to `packages/online-editor`.
2. Open a terminal and run `yarn start`. This will start a `webpack serve` instance with the Online Editor resources.
3. From now on you can use the development version of the Online Editor by accessing `https://localhost:9001`.

#### Desktop

1. After you've successfully built the project following the instructions above, go to `packages/desktop`.
2. To start the application in development mode, you can run `yarn start`. If you make changes and want to reload the app, run `yarn run build:dev && yarn start`. This will recompile the module and restart the Electron app. Remember: if you make changes to other modules, you have to build them too!
3. To build and package the application for production (i.e. generating an executable), you can run `yarn run build:prod`. This will pack the application for the current OS. If you want to pack the application for a different OS, run `yarn run pack:linux`, for example. See `package.json` for more details.

#### Standalone Editors

1. After you've successfully built the project following the instructions above, go to `packages/kie-editors-standalone`.
2. Open a terminal and run `yarn start`. This will start a `webpack serve` instance with the Standalone Editors test page.
3. From now on you can use the development version of the Standalone DMN Editor by accessing `https://localhost:9001/resources/dmn` and the Standalone BPMN Editor by accessing `https://localhost:9001/resources/bpmn`.

## Libraries

#### Stunner Editors

The `stunner-editors` package contains the BPMN, DMN, and SceSim Editors that are used in many applications of KIE Tools.
After cloning the repo, start with a fresh build.

`lerna run build:dev --scope=@kie-tools/stunner-editors --include-dependencies --stream`

After that, you're ready to start developing the Editors individually.

- BPMN

  - Located at `packages/stunner-editors/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime`.
  - Run `mvn clean gwt:run` to start.

- DMN

  - Located at `packages/stunner-editors/kie-wb-common-dmn/kie-wb-common-dmn-webapp-kogito-testing`.
  - Run `mvn clean gwt:run` to start.
  - If you want to enable live-reloading capabilities of the React components that are part of the DMN Editor, follow [these steps](./packages/stunner-editors/docs/live-reload-dmn-loader.md).

- Test Scenario

  - Located at `packages/stunner-editors/drools-wb-screens/drools-wb-scenario-simulation-editor/drools-wb-scenario-simulation-editor-kogito-testing`.
  - Run `mvn clean gwt:run` to start.

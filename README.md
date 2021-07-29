<p align="center"><img width=55% src="docs/kogito.png"></p>

# Kogito Tooling

**[Kogito](http://kogito.kie.org)** is the next generation of business automation platform focused on cloud-native development, deployment and execution.

This repository contains all the tooling artifacts of the Kogito project.

[![GitHub Stars](https://img.shields.io/github/stars/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/kiegroup/kogito-tooling.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/kiegroup/kogito-tooling.svg?style=flat-square)](https://github.com/kiegroup/kogito-tooling/pulls)
[![Contributors](https://img.shields.io/github/contributors/kiegroup/kogito-tooling.svg?style=flat-square)](https://github.com/kiegroup/kogito-tooling/graphs/contributors)
[![License](https://img.shields.io/github/license/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/blob/main/LICENSE)
[![Twitter Follow](https://img.shields.io/twitter/follow/kogito_kie.svg?label=Follow&style=social)](https://twitter.com/kogito_kie?lang=en)

## Contribute

- _Work in progress 🔨_

## Build from source

To start building the KIE Tooling Core project, you're going to need:

- Node `>= 16.2.0` _(To install, follow these instructions: https://nodejs.org/en/download/package-manager/)_
- Yarn `1.22.10` _(To install, run `npm install -g yarn@1.22.10`)_
- Lerna `4.0.0` _(To install, run `npm install -g lerna@4.0.0`)_
- Maven `3.8.1`
- Java `11`

After installing the tools above, you'll need to download the dependencies and link the packages locally. Simply run:

- `yarn bootstrap`

To build it, you'll have two choices:

- `yarn build:dev` - This is fast, but not as strict. It skips tests, linters, and some type checks. Be prepared for the CI to fail on your PRs.
- `yarn build:prod` - The default command to build production-ready packages. Use that to make sure your changes are correct.

**NOTE:** The Kogito Tooling build is parameterized by several Environment Variables. For an extensive list of these variables, please see `packages/build-env/README.md` ([link](/packages/build-env/README.md)).

Final artifacts will be on `packages/*/dist` directories.

## Using custom Editor builds from `kogito-editors-java`

While `kogito-editors-java` is not integrated in this monorepo, we have dedicated environment variables to configure where to copy the Editors from. If those environment variables are empty, the Editors are copied from the installed NPM package on `node_modules`.

#### Examples:

```bash
export EXTERNAL_RESOURCE_PATH__bpmnEditor=/Users/tiago/redhat/kogito-editors-java/kie-wb-common-stunner/kie-wb-common-stunner-sets/kie-wb-common-stunner-bpmn/kie-wb-common-stunner-bpmn-kogito-runtime/target/kie-wb-common-stunner-bpmn-kogito-runtime/
```

```bash
export EXTERNAL_RESOURCE_PATH__dmnEditor=/Users/tiago/redhat/kogito-editors-java/kie-wb-common-dmn/kie-wb-common-dmn-webapp-kogito-runtime/target/kie-wb-common-dmn-webapp-kogito-runtime/
```

```bash
export EXTERNAL_RESOURCE_PATH__scesimEditor=/Users/tiago/redhat/kogito-editors-java/drools-wb-screens/drools-wb-scenario-simulation-editor/drools-wb-scenario-simulation-editor-kogito-runtime/target/drools-wb-scenario-simulation-editor-kogito-runtime
```

## Applications

The Kogito Tooling project contains several applications. To develop each one of them individually, refer to the instructions below.

### Available properties to customize the build

> - `KOGITO_TOOLING_BUILD_test`: Runs or skips the unit tests on all packages. Runs the tests if empty. Can be `"true"` or `"false"`.
> - `KOGITO_TOOLING_BUILD_lint`: Runs or skips ESLint on all projects. Runs the linter if empty. Can be `"true"` or `"false"`.
> - `WEBPACK_TS_LOADER_transpileOnly`: Configures `ts-loader` with its value. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/webpack.common.config.js#L16)
> - `WEBPACK_minimize`: Configures Webpack to minimize the bundles or not. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/webpack.common.config.js#L16)
> - `DOWNLOAD_HUB_linuxUrl`: Used in `packages/online-editor`. Configures the URL to download the Linux Hub on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/online-editor/webpack.config.js#L16) -
> - `DOWNLOAD_HUB_macOsUrl`: Used in `packages/online-editor`. Configures the URL to download the macOS Hub on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/online-editor/webpack.config.js#L16) -
> - `DOWNLOAD_HUB_windowsUrl`: Used in `packages/online-editor`. Configures the URL to download the Windows Hub on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/online-editor/webpack.config.js#L16) -
> - `ROUTER_targetOrigin`: Used in `packages/chrome-extension-pack-kogito-kie-editors`. Configures the origin from which the Editor envelopes will be fetched. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/chrome-extension-pack-kogito-kie-editors/webpack.config.js#L16)
> - `ROUTER_relativePath`: Used in `packages/chrome-extension-pack-kogito-kie-editors`. Configures the URI from which the Editor envelopes will be fetched. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/chrome-extension-pack-kogito-kie-editors/webpack.config.js#L16)
> - `ONLINEEDITOR_url`: Used in `packages/chrome-extension-pack-kogito-kie-editors`. Configures the URL of the Online Editor to be used on the "Open in ..." button. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/chrome-extension-pack-kogito-kie-editors/webpack.config.js#L16)
> - `EXTERNAL_RESOURCE_PATH__bpmnEditor`: Used in several packages. Configures the local path from which the BPMN Editor files will be copied. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/external-assets-base/index.js#L16)
> - `EXTERNAL_RESOURCE_PATH__dmnEditor`: Used in several packages. Configures the local path from which the DMN Editor files will be copied. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/external-assets-base/index.js#L16)
> - `EXTERNAL_RESOURCE_PATH__scesimEditor`: Used in several packages. Configures the local path from which the SceSim Editor files will be copied. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/external-assets-base/index.js#L16)
> - `EXTERNAL_RESOURCE_PATH__quarkusRunner`: Used in `packages/vscode-extension-backend`. Configures the local path from which the Quarkus Runner files will be copied. [See default](https://github.com/kiegroup/kogito-tooling/blob/main/packages/external-assets-base/index.js#L16)
> - `KIE_TOOLING_EXTENDED_SERVICES__linuxDownloadUrl`: Used in `packages/online-editor`. Configures the URL to download the Linux KIE Tooling Extended Services on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/master/packages/online-editor/webpack.config.js#L71)
> - `KIE_TOOLING_EXTENDED_SERVICES__macOsDownloadUrl`: Used in `packages/online-editor`. Configures the URL to download the macOS KIE Tooling Extended Services on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/master/packages/online-editor/webpack.config.js#L65)
> - `KIE_TOOLING_EXTENDED_SERVICES__windowsDownloadUrl`: Used in `packages/online-editor`. Configures the URL to download the Windows KIE Tooling Extended Services on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/master/packages/online-editor/webpack.config.js#L68)
> - `KIE_TOOLING_EXTENDED_SERVICES__compatibleVersion`: Used in `packages/online-editor`. Configures the version of the KIE Tooling Extended Services on the Online Editor. [See default](https://github.com/kiegroup/kogito-tooling/blob/master/packages/online-editor/webpack.config.js#L62)

#### VS Code Extension

1. After you've successfully built the project following the instructions above, open the `packages/vscode-extension-pack-kogito-kie-editors` folder on VS Code. Use a new VS Code window so that the `packages/vscode-extension-pack-kogito-kie-editors` folder shows up as root in the VS Code explorer.
2. From there, you can Run the extension or the integration tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **NOTE:** To run the VS Code extension in development mode, you need `webpack` and `webpack-cli` to be globally installed on NPM. Normally you can do that with `npm install -g webpack@4.41.2 webpack-cli@3.3.10`, but `sudo` may be required depending on your installation.
4. **Remember!** If you make changes to packages other than `packages/vscode-extension-pack-kogito-kie-editors`, you have to manually rebuild them before relaunching the extension on VS Code.

#### Chrome Extension

1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-pack-kogito-kie-editors` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
2. Run `yarn build:dev` on `packages/chrome-extension-pack-kogito-kie-editors`. This will create a version of the Chrome Extension that fetches the envelope locally.
3. Open a terminal and run `yarn run serve-envelope` on `packages/chrome-extension-pack-kogito-kie-editors`. This will start a `webpack serve` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
4. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable this flag. Alternativelly, you can go to `https://localhost:9001` and add an exception.
5. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" in the top-right corner and click on "Load unpacked". Choose the `packages/chrome-extension-pack-kogito-kie-editors/dist` folder.
6. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

#### Online Editor

1. After you've successfully built the project following the instructions above, go to `packages/online-editor`.
2. Open a terminal and run `yarn start`. This will start a `webpack serve` instance with the Online Editor resources.
3. From now on you can use the development version of the Online Editor by accessing `https://localhost:9001`.

#### Desktop and Hub

1. After you've successfully built the project following the instructions above, go to `packages/desktop` or `packages/hub`. They work exactly the same.
2. To start the application in development mode, you can run `yarn start`. If you make changes and want to reload the app, run `yarn run build:dev && yarn start`. This will recompile the module and restart the Electron app. Remember: if you make changes to other modules, you have to build them too!
3. To build and package the application for production (i.e. generating an executable), you can run `yarn run build:prod`. This will pack the application for the current OS. If you want to pack the application for a different OS, run `yarn run pack:linux`, for example. See `package.json` for more details.

#### Standalone Editors

1. After you've successfully built the project following the instructions above, go to `packages/kie-editors-standalone`.
2. Open a terminal and run `yarn start`. This will start a `webpack serve` instance with the Standalone Editors test page.
3. From now on you can use the development version of the Standalone DMN Editor by accessing `https://localhost:9001/resources/dmn` and the Standalone BPMN Editor by accessing `https://localhost:9001/resources/bpmn`.

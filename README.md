Kogito
------
test

**Kogito** is the next generation of business automation platform focused on cloud-native development, deployment and execution.

<p align="center"><img width=55% height=55% src="docs/kogito.png"></p>

[![GitHub Stars](https://img.shields.io/github/stars/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/network/members)
[![GitHub Issues](https://img.shields.io/github/issues/kiegroup/kogito-tooling.svg)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/kiegroup/kogito-tooling.svg?style=flat-square)](https://github.com/kiegroup/kogito-tooling/pulls)
[![Contributors](https://img.shields.io/github/contributors/kiegroup/kogito-tooling.svg?style=flat-square)](https://github.com/kiegroup/kogito-tooling/graphs/contributors)
[![License](https://img.shields.io/github/license/kiegroup/kogito-tooling.svg)](https://github.com/kiegroup/kogito-tooling/blob/master/LICENSE-ASL-2.0.txt)
[![Twitter Follow](https://img.shields.io/twitter/follow/kogito_kie.svg?label=Follow&style=social)](https://twitter.com/kogito_kie?lang=en)

Quick Links
-----------

**Homepage:** http://kogito.kie.org

**Wiki:** https://github.com/kiegroup/kogito-tooling/wiki

**JIRA:** https://issues.jboss.org/projects/KOGITO

**jBPM:** https://www.jbpm.org/

**Drools:** https://www.drools.org/


Getting Started
---------------

This module contains a number of examples that you can take a look at and try out yourself.
 Please take a look at the readme of each individual example for more details on how the example works and how to run it yourself (either locally or on Kubernetes):
- Process + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/process-quarkus-example/README.md)
- Process + Spring Boot: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/process-springboot-example/README.md)
- Process + Rules + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/onboarding-example/README.md) - Onboarding example combining one process and two decision services
- Rules + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/rules-quarkus-helloworld/README.md)
- Rule Unit + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/ruleunit-quarkus-example/README.md)

Releases
--------------------

In Kogito Tooling [releases page](https://github.com/kiegroup/kogito-tooling/releases) you will find extensions and released tooling packages.


Build from source
--------------------

1. Check out the source:
    ```
    git clone git@github.com:kiegroup/kogito-tooling.git
    ```
    
    > If you don't have a GitHub account use this command instead:
    > ```
    > git clone https://github.com/kiegroup/kogito-tooling.git
    > ```

1. Build with Yarn:
    ```bash
    cd kogito-tooling
    yarn run init
    
    #prod
    yarn run build:prod
    
    # dev
    yarn run build # skips integration tests and production packing
    yarn run build:fast # skips lint and unit tests
    ```

    > **NOTE**: It's necessary to have the Git tags fetched when building with `build:prod`.
    
    > Final artifacts will be on `packages/*/dist` directories.

Develop
--------------------

> **NOTE:** We recommend using *LTS* version of Node.js when developing this project.
    
##### VSCode Extension
1. After you've successfully built the project following the instructions above, open the `packages/vscode-extension-pack-kogito-kie-editors` folder on VSCode. Use a new VSCode window so that the `packages/vscode-extension-pack-kogito-kie-editors` folder shows up as root in the VSCode explorer.
2. From there, you can Run the extension or the integration tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **Remember!** If you make changes to packages other than `packages/vscode-extension-pack-kogito-kie-editors`, you have to manually rebuild them before relaunching the extension on VSCode.

##### Chrome Extension
1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-pack-kogito-kie-editors` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
2. Run `yarn build:fast` on `packages/chrome-extension-pack-kogito-kie-editors`. This will create a version of the Chrome Extension that fetches the envelope locally.
2. Open a terminal and run `yarn run serve-envelope` on `packages/chrome-extension-pack-kogito-kie-editors`. This will start a `webpack-dev-server` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
4. You also have to enable invalid certificates for resources loaded from localhost in your browser. To do that, go to `chrome://flags/#allow-insecure-localhost` in your Chrome browser and enable this flag. Alternativelly, you can go to `https://localhost:9001` and add an exception.
3. Open Chrome and go to `chrome://extensions`. Enable "Developer mode" on the top right corner and click on "Load unpacked". Choose the `packages/chrome-extension-pack-kogito-kie-editors/dist` folder.
4. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.

##### Online Editor
1. After you've successfully built the project following the instructions above, go to `packages/online-editor`.
1. Open a terminal and run `yarn start`. This will start a `webpack-dev-server` instance with the Online Editor resources.
5. From now on you can use the development version of the Online Editor by accessing `https://localhost:9001`.

##### Desktop and Hub
1. After you've successfully built the project following the instructions above, go to `packages/desktop` or `packages/hub`. They work exactly the same.
2. To start the application in development mode, you can run `yarn start`. If you make changes and want to reload the app, run `yarn run build:fast && yarn start`. This will recompile the module and restart the Electron app. Remember: if you make changes to other modules, you have to build them too!
3. To build and package the application for production (i.e. generating an executable), you can run `yarn run build:prod`. This will pack the application for the current OS. If you want to pack the application for a different OS, run `yarn run pack:linux`, for example. See `package.json` for more details.


Contribute
--------------------
- When opening PRs, please make sure to provide a detailed description of the issue along with the JIRA, if there's one.
- If you are a member of [kiegroup](https://github.com/kiegroup) and want to test a change you made in our tooling, you 
can go to our [Run FDB issue](https://github.com/kiegroup/kogito-tooling/issues/221) and make a comment following the 
format `Build: {github-username}/{branch-name}`. This will trigger a job that will fetch the forks (1) of `{github-username}`, 
merge `{branch-name}` into master, and build them.
In a few seconds you should see a new comment on the same issue, saying that a new build was triggered for you. The 
GitHub Actions bot will also provide a link so you can follow the build logs and download artifacts, and another link to 
access an Online Editor instance (2) containing your changes, once it's finished running. 

(1) This process considers the following repositories: `droolsjbpm-build-bootstrap`, `kie-soup`, `appformer`, `kie-wb-common`, `drools-wb` and `kogito-tooling`.

(2) The Online Editor instance will be accessible for 30 days.   

Contributing to Kogito
--------------------

All contributions are welcome! Before you start please read the [Developing Drools and jBPM](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md) guide.


Guides
--------------------

Here are some of the most notable ones for quick reference:

- [Quarkus - Using Kogito to add business automation capabilities to an application](https://quarkus.io/guides/kogito-guide) - This guide demonstrates how your Quarkus application can use Kogito to add business automation to power it up with business processes and rules.
- [Quarkus - Getting Started](https://quarkus.io/get-started/) - Quarkus Getting Started guide

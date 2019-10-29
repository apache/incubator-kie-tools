Kogito
------

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
- jBPM + Quarkus Hello World: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/jbpm-quarkus-helloworld/README.md)
- jBPM + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/jbpm-quarkus-example/README.md)
- jBPM + Spring Boot: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/jbpm-springboot-example/README.md)
- jBPM + Drools + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/onboarding-example/readme.md) - Onboarding example combining one process and two decision services
- Polyglot Drools with GraalVM: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/drools-polyglot-example/README.md)
- Drools + Quarkus: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/drools-quarkus-example/README.md)
- Drools + Quarkus with Unit: [README.md](https://github.com/kiegroup/kogito-examples/tree/master/drools-quarkus-unit-example/README.md)

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

    > Final artifacts will be on `packages/*/dist` directories.

Develop
--------------------

> **NOTE:** We currently pack two GWT-based editors -- BPMN and DMN. They are exploded WARs, so we don't commit them in this repo. We are currently working on automatically fetching them during the build, but we didn't do it **yet**. For builds to work properly, you must have the exploded WARs copied on `packages/unpacked-gwt-editors/` `dmn` and `bpmn` folders. You can go to [the latest commit](https://github.com/kiegroup/kogito-tooling/commits/master), click on the "check" icon and download the exploded WARs from the Artifacts tab. Be aware that each editor must go on its own folder.
    
##### VSCode Extension
1. After you've successfully built the project following the instructions above, open the `packages/vscode-extension-pack-kogito-kie-editors` folder on VSCode. Use a new VSCode window so that the `packages/vscode-extension-pack-kogito-kie-editors` folder shows up as root in the VSCode explorer.
2. From there, you can Run the extension or the integration tests by using the `Debug` menu/section. You can also use the respective shortcuts (F5 to start debugging, for instance).
3. **Remember!** If you make changes to packages other than `packages/vscode-extension-pack-kogito-kie-editors`, you have to manually rebuild them before relaunching the extension on VSCode.

##### Chrome Extension
1. After you've successfully built the project following the instructions above, open the `packages/chrome-extension-pack-kogito-kie-editors` folder on your favourite IDE. You can import the entire repo as well if you want to make changes to other packages.
2. Open a terminal and run `yarn run serve-envelope` on `packages/chrome-extension-pack-kogito-kie-editors`. This will start a `webpack-dev-server` instance with the editors and their envelope. We use that because we don't pack the Chrome Extension bundle with the editors inside. Instead, we fetch them from GitHub pages.
3. Open Chrome and go to `chrome://extension`. Enable "Developer mode" on the top right corner and click on "Load unpacked". Choose the `packages/chrome-extension-pack-kogito-kie-editors/dist` folder.
4. From now on you can use the development version of the extension. **Remember!** After each change, you have to rebuild the changed modules and hit the "Refresh" button of the extension card.


Contributing to Kogito
--------------------

All contributions are welcome! Before you start please read the [Developing Drools and jBPM](https://github.com/kiegroup/droolsjbpm-build-bootstrap/blob/master/README.md) guide.


Guides
--------------------

Here are some of the most notable ones for quick reference:

- [Quarkus - Using Kogito to add business automation capabilities to an application](https://quarkus.io/guides/kogito-guide) - This guide demonstrates how your Quarkus application can use Kogito to add business automation to power it up with business processes and rules.
- [Quarkus - Getting Started](https://quarkus.io/get-started/) - Quarkus Getting Started guide

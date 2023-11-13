# Graphical DMN modeling tool

This module contains various children for different purposes.

1. `kie-wb-common-dmn-api`

Common API and UI model.

2. `kie-wb-common-dmn-client`

Common client-side code for the _core_ editor.

3. `kie-wb-common-dmn-webapp-kogito-common`

Substitute implementations of services in `kie-wb-common-dmn-backend` for _kogito_ client-side use.

4. `kie-wb-common-dmn-webapp-kogito-marshaller`

Client-side marshaller for _kogito_.

5. `kie-wb-common-dmn-webapp-kogito-runtime`

Webapp targeting _kogito_ integration with VS Code etc. No decorations.

Please refer to the [Kogito's DMN Editor README](./kie-wb-common-dmn-webapp-kogito-runtime/README.md) for building and usage.

To run this module, for testing and debugging purposes, launch the `gwt` plugin; i.e. `mvn clean process-resources gwt:run`.

This module contains also selenium end-to-end tests. They use `headless`
browser mode by default thus are not visible. To see the actual progress of tests include `-Dorg.kie.dmn.kogito.browser.headless=false` property into your
`mvn` command.

There is small set of performance test checking loading of large models. These set of tests is not started by default. You can activate these tests by `-Dperformance-tests` property.

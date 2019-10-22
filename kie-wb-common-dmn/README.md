Graphical DMN modeling tool
===========================

This module contains various children for different purposes.

1) `kie-wb-common-dmn-api`

Common API and UI model used by Business Central and _kogito_.

2) `kie-wb-common-dmn-backend`

Server-side implementation of services relating to marshalling, imports and validation 
used exclusively by Business Central and the `kie-wb-common-webapp-standalone` module.

3) `kie-wb-common-dmn-client`

Common client-side code for the _core_ editor used by Business Central and _kogito_.

4) `kie-wb-common-dmn-project-api`

API specific to integration with Business Central.

5) `kie-wb-common-dmn-project-client`

Client-side code specific to the integration with Business Central.

6) `kie-wb-common-dmn-webapp-common`

Client-side code common to the `kie-wb-common-webapp-standalone` and `kie-wb-common-webapp-kogito-testing` 
modules. Includes _docks_ and diagram _navigator_. This code is not used by Business Central.

7) `kie-wb-common-dmn-webapp-kogito-common`

Substitute implementations of services in `kie-wb-common-dmn-backend` for _kogito_ client-side use.

8) `kie-wb-common-dmn-webapp-kogito-marshaller`

Client-side marshaller for _kogito_.

9) `kie-wb-common-dmn-webapp-kogito-runtime`

Webapp targeting _kogito_ integration with VSCode etc. No decorations.

10) `kie-wb-common-dmn-webapp-kogito-testing`

Webapp used for development of _kogito_ decorated with a diagram _navigator_ to emulate integration with VSCode etc.

11) `kie-wb-common-dmn-webapp-standalone`

Webapp used for development of Business Central integration.


The _kogito_ modules are isolated by the `kogito` Maven profile. In order to compile and run a _kogito_ related 
webapp the following steps need to be adhered to.

1) Clone https://github.com/kiegroup/droolsjbpm-build-bootstrap/ and build the `DROOLS-3727` branch.

2) Clone https://github.com/highsource/jsonix and build the `master` branch. Ignore the failure of the compilation of some sub-modules.. enough is compiled for our use.

3) Clone https://github.com/kiegroup/gwt-jsonix-marshallers and build the `master` branch.

4) Compile `kie-wb-common` using the `kogito` profile; e.g. `mvn clean install -Pkogito`

5) Run one of the _kogito_ webapps using `mvn clean process-resources gwt:run`
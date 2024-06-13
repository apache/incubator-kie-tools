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

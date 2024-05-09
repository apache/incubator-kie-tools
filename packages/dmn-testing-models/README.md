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

## DMN testing models

This package is meant to contain all the DMN models published inside [kie-dmn-test-resources](https://github.com/apache/incubator-kie-drools/tree/main/kie-dmn/kie-dmn-test-resources) to make them available for testing purposes.

Models are separated between < 1.5 version and 1.5 version; such classification is based on actual version-specific features, and not on the referenced tag itself.

For future DMN versions there will be version specific folders.

The original `org.kie:kie-dmn-test-resources` also contains _invalid_ models, but for the moment being we use only the valid ones to verify round-trip validation.

### Usage

The command `mvn clean verify` downloads the jar and extract the models under `dist/`.

To make them available for testing purpose:

1. set the `KOGITO_RUNTIME_version` in the terminal (if different from the default one)
2. `pnpm bootstrap` on the root directory
3. cd dmn-testing-models
4. `pnpm build:dev` or `pnpm build:prod` (they behave the same way - this executes `mvn clean verify`)

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

# sonataflow-image-common utility scripts

This directory contains a set of python scripts used to help to perform some tasks during the build and configuration of the `kogito-swf` images:

## Common script

The [common.py](common.py) defines the basic functions that will be used by other scripts

## Retrieve Versions script

The [retrieve_versions.py](retrieve_version.py) script is used to read the actual image version from the
`kogito-project-versions` module during the image build process.

## Versions Manager

The [versions_manager.py](versions_manager.py) script offers a CLI that helps upgrading versions properties in
the images yaml descriptor or cekit modules. This script is being used during the image build time, but it's also used
to update the images / cekit modules versions when bootstraping the `@kie-tools` repo.

Usage:

- Bumping images / cekit modules versions of a package in `@kie-tools`
  Args:

  - `--bump-to`: bumps the image and module versions (in the `resources` folder) to the specified version.
  - `--source-folder`: specifies the path to the `resources` folder.

- Upgrading platform dependencies in all images / modules envs and labels during the image build process.
  Args:
- `--quarkus-version`: Sets the Quarkus version
- `--kogito-version`: Sets the Kogito version

## Build Kogito Apps Components

The [build-kogito-apps-components.sh](build-kogito-apps-components.sh) script pulls and build the target Kogito Apps application, e.g., Data Index. Required to build Kogito Services images in any flavour.

## Setup Maven

The [setup-maven.sh](setup-maven.sh) script configures the internal image Maven repository such as adding new repositories, setup other profiles and so on.

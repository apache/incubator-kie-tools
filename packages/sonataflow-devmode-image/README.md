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

# sonataflow-devmode

This package contains the `cekit` image descriptor (`sonataflow-devmode-image.yaml`) and modules needed to build the `sonataflow-devmode`
image along with the modules and scripts provided in `@kie-tools/sonataflow-image-common`.

## Additional requirements

- **python3** with the following packages installed:
  - `behave` `lxml` `docker` `docker-squash` `elementPath` `pyyaml` `ruamel.yaml` `python-dateutil` `Jinja2` `pykwalify` `colorlog` `click`
- **cekit 4.11.0**: [docs.cekit.io](https://docs.cekit.io/en/latest/index.html)
- **s2i**: [source-to-image](https://github.com/openshift/source-to-image)
- **make**
- **docker**

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- (Optional) The image name and tags can be customized by setting the following environment variables:

  ```bash
  export SONATAFLOW_DEVMODE_IMAGE__registry=<registry>
  export SONATAFLOW_DEVMODE_IMAGE__account=<account>
  export SONATAFLOW_DEVMODE_IMAGE__name=<image-name>
  export SONATAFLOW_DEVMODE_IMAGE__buildTag=<image-tag>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/sonataflow-devmode-image build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

## Testing the generated image (only for Linux)

- With the image generated, run:

  ```bash
  pnpm -F @kie-tools/sonataflow-devmode-image image:test
  ```

## Envs

|                 Name                 |                    Description                     |                          Default                          |
| :----------------------------------: | :------------------------------------------------: | :-------------------------------------------------------: |
| `SONATAFLOW_DEVMODE_IMAGE__registry` | Registry where the generated image will be pushed. |                        "docker.io"                        |
| `SONATAFLOW_DEVMODE_IMAGE__account`  |        Account where image will be stored.         |                         "apache"                          |
|   `SONATAFLOW_DEVMODE_IMAGE__name`   |              SWF DevMode Image name.               |                   "sonataflow-devmode"                    |
| `SONATAFLOW_DEVMODE_IMAGE__buildTag` |                    Tag to use .                    | $KIE_TOOLS_BUILD\_\_streamName (E.g., "main" or "10.0.x") |

---

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html

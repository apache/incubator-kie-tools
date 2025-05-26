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

# kogito-management-console

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for Management Console. It also generated a JSON Schema for the `env.json` file, enabling it to be validated.

## Additional requirements

- docker

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- (Optional) The image name and tags can be customized by setting the following environment variables:

  ```bash
  export KOGITO_MANAGEMENT_CONSOLE__registry=<registry>
  export KOGITO_MANAGEMENT_CONSOLE__account=<account>
  export KOGITO_MANAGEMENT_CONSOLE__name=<image-name>
  export KOGITO_MANAGEMENT_CONSOLE__buildTag=<image-tags>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/runtime-tools-management-console-webapp... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

## Run

- Start up a clean container with:

  ```bash
  docker run -t -p 8080:8080 -i --rm docker.io/apache/incubator-kie-kogito-management-console:main
  ```

  Management Console will be up at http://localhost:8080

## Customization

1. Run a container with custom environment variables:

   [comment]: <> (//TODO: Use EnvJson.schema.json to generate this documentation somehow.. See https://github.com/kiegroup/kie-issues/issues/16)

   |                             Name                              |                             Description                              |                                            Default                                            |
   | :-----------------------------------------------------------: | :------------------------------------------------------------------: | :-------------------------------------------------------------------------------------------: |
   |          `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME`          |                     Management Console app name.                     | See [ defaultEnvJson.js ](../runtime-tools-management-console-webapp/build/defaultEnvJson.js) |
   |   `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID`    |    OpenID Connect client ID for connecting to Identity Providers.    | See [ defaultEnvJson.js ](../runtime-tools-management-console-webapp/build/defaultEnvJson.js) |
   | `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_DEFAULT_SCOPES` | OpenID Connect default scopes when connecting to Identity Providers. | See [ defaultEnvJson.js ](../runtime-tools-management-console-webapp/build/defaultEnvJson.js) |

   ### Examples

   1. Using a different Client ID.

      ```bash
      docker run -t -p 8080:8080 -e RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID=<my_client_id> -i --rm docker.io/apache/incubator-kie-kogito-management-console:main
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM docker.io/apache/incubator-kie-kogito-management-console:main

   ENV RUNTIME_TOOLS_MANAGEMENT_CONSOLE_APP_NAME=<my_app_name>
   ENV RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_CLIENT_ID=<my_client_id>
   ENV RUNTIME_TOOLS_MANAGEMENT_CONSOLE_OIDC_CLIENT_DEFAULT_SCOPES=<my_default_scopes>
   ```

3. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

## Custom Port

The port used internally on the container can be changed:

When building, set the `KOGITO_MANAGEMENT_CONSOLE__port` environment variable to any port you want, and the Containerfile will be built using that port.

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

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

# SonataFlow Management Console Image

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for SonataFlow Management Console. It also generated a JSON Schema for the `env.json` file, enabling it to be validated.

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
  pnpm -F @kie-tools/sonataflow-management-console-image... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

## Run

- Start up a clean container with:

  ```bash
  docker run -t -p 8080:8080 -i --rm docker.io/apache/incubator-kie-sonataflow-management-console:main
  ```

  Management Console will be up at http://localhost:8080

## Customization

1. Run a container with custom environment variables:

   [comment]: <> (//TODO: Use EnvJson.schema.json to generate this documentation somehow.. See https://github.com/kiegroup/kie-issues/issues/16)

   |                        Name                         |                          Description                          |                                          Default                                           |
   | :-------------------------------------------------: | :-----------------------------------------------------------: | :----------------------------------------------------------------------------------------: |
   |   `SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_ENV_MODE`   | Env Mode: "PROD" or "DEV". PROD enables Keycloak integration. |                                           "PROD"                                           |
   |   `SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_APP_NAME`   |                 Management Console app name.                  | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   | `SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_APP_VERSION`  |                Management Console app version.                | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   | `SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT` |        The URL that points to the Data Index service.         | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   |   `KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK`   |                Disables Keycloak health-check.                | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   |  `KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY`   |               Update token validity in minutes.               | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   |     `KOGITO_CONSOLES_KEYCLOAK_HEALTH_CHECK_URL`     |                  Keycloak health-check URL.                   | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   |          `KOGITO_CONSOLES_KEYCLOAK_REALM`           |                     Keycloak realm name.                      | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   |           `KOGITO_CONSOLES_KEYCLOAK_URL`            |                      Keycloak auth URL.                       | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |
   |        `KOGITO_CONSOLES_KEYCLOAK_CLIENT_ID`         |                      Keycloak Client ID.                      | See [ defaultEnvJson.ts ](../sonataflow-management-console-webapp/build/defaultEnvJson.ts) |

   ### Examples

   1. Using a different Data Index Service.

      ```bash
      docker run -p 8080:8080 -e SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT=<my_value> -i --rm docker.io/apache/incubator-kie-sonataflow-management-console:main
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM docker.io/apache/incubator-kie-sonataflow-management-console:main

   ENV SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT=<my_value>
   ```

3. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

## Custom Port

The port used internally on the container can be changed:

When building, set the `SONATAFLOW_MANAGEMENT_CONSOLE__port` environment variable to any port you want, and the Containerfile will be built using that port.

## Run the Docker Image Locally

1. This command will start the container and configure it to access your local data-index service from the container.
   Replace `<HOST_IP_ADDRESS>` with the IP address of your host machine.

   ```bash
   docker run --rm -p 8080:8080 \
     -e SONATAFLOW_MANAGEMENT_CONSOLE_KOGITO_ENV_MODE='DEV' \
     -e SONATAFLOW_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT='http://<HOST_IP_ADDRESS>:4000/graphql' \
     docker.io/apache/incubator-kie-sonataflow-management-console:main
   ```

2. In a separate terminal, start Sonataflow Dev App for the Data Index service.

   ```bash
    cd ../sonataflow-dev-app
    pnpm start
   ```

   **Important Note:** Avoid using the `-it` option (interactive and TTY) with the docker run command, as it may cause the internal Apache HTTPD server to terminate with SIGWINCH during terminal resizing. For more details, refer to https://bz.apache.org/bugzilla/show_bug.cgi?id=50669

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

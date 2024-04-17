# kogito-managment-console

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for Management Console. It also generated a JSON Schema for the `env.json` file, enabling it to be validated.

## Additional requirements

- docker or podman

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
  export KOGITO_MANAGEMENT_CONSOLE__buildTags=<image-tags>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/runtime-tools-managment-console-webapp-image... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

  or

  ```bash
  podman images
  ```

## Run

- Start up a clean container with:

  ```bash
  docker run -t -p 8080:8080 -i --rm quay.io/kie-tools/runtime-tools-managment-console-image:daily-dev
  ```

  or

  ```bash
  podman run -t -p 8080:8080 -i --rm quay.io/kie-tools/runtime-tools-managment-console-image:daily-dev
  ```

  Management Console will be up at http://localhost:8080

## Customization

1. Run a container with custom environment variables:

   [comment]: <> (//TODO: Use EnvJson.schema.json to generate this documentation somehow.. See https://github.com/kiegroup/kie-issues/issues/16)

   |                          Name                          |                          Description                          |                                           Default                                            |
   | :----------------------------------------------------: | :-----------------------------------------------------------: | :------------------------------------------------------------------------------------------: |
   |   `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_KOGITO_ENV_MODE`   | Env Mode: "PROD" or "DEV". PROD enables Keycloak integration. |                                            "PROD"                                            |
   |   `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_KOGITO_APP_NAME`   |                 Management Console app name.                  | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   | `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_KOGITO_APP_VERSION`  |                Management Console app version.                | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   | `RUNTIME_TOOLS_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT` |        The URL that points to the Data Index service.         | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   |    `KOGITO_CONSOLES_KEYCLOAK_DISABLE_HEALTH_CHECK`     |                Disables Keycloak health-check.                | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   |    `KOGITO_CONSOLES_KEYCLOAK_UPDATE_TOKEN_VALIDITY`    |               Update token validity in minutes.               | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   |      `KOGITO_CONSOLES_KEYCLOAK_HEALTH_CHECK_URL`       |                  Keycloak health-check URL.                   | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   |            `KOGITO_CONSOLES_KEYCLOAK_REALM`            |                     Keycloak realm name.                      | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   |             `KOGITO_CONSOLES_KEYCLOAK_URL`             |                      Keycloak auth URL.                       | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |
   |          `KOGITO_CONSOLES_KEYCLOAK_CLIENT_ID`          |                      Keycloak Client ID.                      | See [ defaultEnvJson.ts ](../runtime-tools-managment-console-webapp/build/defaultEnvJson.js) |

   ### Examples

   1. Using a different Data Index Service.

      ```bash
      docker run -t -p 8080:8080 -e RUNTIME_TOOLS_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT=<my_value> -i --rm quay.io/kie-tools/runtime-tools-managment-console-webapp-image:daily-dev
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM quay.io/kie-tools/runtime-tools-managment-console-webapp-image:daily-dev

   ENV RUNTIME_TOOLS_MANAGEMENT_CONSOLE_DATA_INDEX_ENDPOINT=<my_value>
   ```

3. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

## Custom Port

The port used internally on the container can be changed:

When building, set the `KOGITO_MANAGEMENT_CONSOLE__port` environment variable to any port you want, and the Containerfile will be built using that port.

# runtime-tools-task-console-webapp-image

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for Task Console. It also generated a JSON Schema for the `env.json` file, enabling it to be validated.

## Additional requirements

- docker or podman

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- (Optional) The image name and tags can be customized by setting the following environment variables:

  ```bash
  export RUNTIME_TOOLS_TASK_CONSOLE_WEBAPP_IMAGE__registry=<registry>
  export RUNTIME_TOOLS_TASK_CONSOLE_WEBAPP_IMAGE__account=<account>
  export RUNTIME_TOOLS_TASK_CONSOLE_WEBAPP_IMAGE__name=<image-name>
  export RUNTIME_TOOLS_TASK_CONSOLE_WEBAPP_IMAGE__buildTags=<image-tags>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/runtime-tools-task-console-webapp-image... build:prod
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
  docker run -t -p 8080:8080 -i --rm quay.io/kie-tools/runtime-tools-task-console-image:daily-dev
  ```

  or

  ```bash
  podman run -t -p 8080:8080 -i --rm quay.io/kie-tools/runtime-tools-task-console-image:daily-dev
  ```

  Task Console will be up at http://localhost:8080

## Customization

1. Run a container with custom environment variables:

   [comment]: <> (//TODO: Use EnvJson.schema.json to generate this documentation somehow.. See https://github.com/kiegroup/kie-issues/issues/16)

   |                      Name                       |                  Description                   |                                         Default                                         |
   | :---------------------------------------------: | :--------------------------------------------: | :-------------------------------------------------------------------------------------: |
   | `RUNTIME_TOOLS_TASK_CONSOLE_DATAINDEX_HTTP_URL` | The URL that points to the Data Index service. | See [ defaultEnvJson.ts ](../runtime-tools-task-console-webapp/build/defaultEnvJson.js) |

   ### Examples

   1. Using a different Data Index Service.

      ```bash
      docker run -t -p 8080:8080 -e RUNTIME_TOOLS_TASK_CONSOLE_DATAINDEX_HTTP_URL=<my_value> -i --rm quay.io/kie-tools/runtime-tools-task-console-webapp-image:daily-dev
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM quay.io/kie-tools/runtime-tools-task-console-webapp-image:daily-dev

   ENV RUNTIME_TOOLS_TASK_CONSOLE_DATAINDEX_HTTP_URL=<my_value>
   ```

3. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

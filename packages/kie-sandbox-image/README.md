# kie-sandbox-image

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for KIE Sandbox. It also generated a JSON Schema for the `env.json` file, enabling it to be validated.

## Additional requirements

- podman (for Linux)
- docker (for macOS)

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- (Optional) The image name and tags can be customized by setting the following environment variables:

  ```bash
  export KIE_SANDBOX__imageRegistry=<registry>
  export KIE_SANDBOX__imageAccount=<account>
  export KIE_SANDBOX__imageName=<image-name>
  export KIE_SANDBOX__imageBuildTags=<image-tags>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/kie-sandbox-image... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  podman images
  ```

## Run

- Start up a clean container with:

  ```bash
  podman run -t -p 8080:8080 -i --rm quay.io/kie-tools/kie-sandbox-image:latest
  ```

  KIE Sandbox will be up at http://localhost:8080

## Customization

1. Run a container with custom environment variables:

   [comment]: <> (//TODO: Use EnvJson.schema.json to generate this documentation somehow.. See https://github.com/kiegroup/kie-issues/issues/16)

   |                Name                 |                                                     Description                                                      | Default                                                           |
   | :---------------------------------: | :------------------------------------------------------------------------------------------------------------------: | ----------------------------------------------------------------- |
   | `KIE_SANDBOX_EXTENDED_SERVICES_URL` |                              The URL that points to the KIE Sandbox Extended Services.                               | See [defaultEnvJson.ts](../online-editor/build/defaultEnvJson.ts) |
   |  `KIE_SANDBOX_GIT_CORS_PROXY_URL`   |                    The URL that points to the Git CORS proxy for interacting with Git providers.                     | See [defaultEnvJson.ts](../online-editor/build/defaultEnvJson.ts) |
   |    `KIE_SANDBOX_AUTH_PROVIDERS`     | Authentication providers configuration. Used to enable integration with GitHub Enterprise Server instances and more. | See [defaultEnvJson.ts](../online-editor/build/defaultEnvJson.ts) |

   ### Examples

   1. Using a different Extended Services deployment.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value> -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   1. Enabling authentication with a GitHub Enterprise Server instance.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_AUTH_PROVIDERS='[{"id":"github_at_my_company","domain":"github.my-company.com","supportedGitRemoteDomains":["github.my-company.com","gist.github.my-company.com"],"type":"github","name":"GitHub @ MyCompany","enabled":true }]' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

1. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM quay.io/kie-tools/kie-sandbox-image:latest

   ENV KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value>
   ENV KIE_SANDBOX_GIT_CORS_PROXY_URL=<my_value>
   ENV KIE_SANDBOX_AUTH_PROVIDERS=<my_value>
   ```

1. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

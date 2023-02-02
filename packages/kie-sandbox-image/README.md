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

   |                            Name                            |                                                     Description                                                      |                               Default                               |
   | :--------------------------------------------------------: | :------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------: |
   |            `KIE_SANDBOX_EXTENDED_SERVICES_URL`             |                              The URL that points to the KIE Sandbox Extended Services.                               | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |              `KIE_SANDBOX_GIT_CORS_PROXY_URL`              |                    The URL that points to the Git CORS proxy for interacting with Git providers.                     | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |        `KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE`         |                      Require users to type a custom commit message when creating a new commit.                       | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   | `KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL` |                                       Service URL to validate commit messages.                                       | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                `KIE_SANDBOX_AUTH_PROVIDERS`                | Authentication providers configuration. Used to enable integration with GitHub Enterprise Server instances and more. | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |

   ### Examples

   1. Using a different Extended Services deployment.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value> -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   2. Enabling authentication with a GitHub Enterprise Server instance.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_AUTH_PROVIDERS='[{"id":"github_at_my_company","domain":"github.my-company.com","supportedGitRemoteDomains":["github.my-company.com","gist.github.my-company.com"],"type":"github","name":"GitHub @ MyCompany","enabled":true, "group":"git" }]' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   3. Requiring users to input a custom commit message on every commit.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE='true' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   4. Requiring users to input a custom commit message on every commit and validate it via the example [Commit Message Validation Service](../../examples/commit-message-validation-service/README.md).

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE='true' KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL='http://localhost:8090/validate' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM quay.io/kie-tools/kie-sandbox-image:latest

   ENV KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value>
   ENV KIE_SANDBOX_GIT_CORS_PROXY_URL=<my_value>
   ENV KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE=<my_value>
   ENV KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL=<my_value>
   ENV KIE_SANDBOX_AUTH_PROVIDERS=<my_value>
   ```

3. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

## Providing your own _Commit message validation service_

To validate a commit message against your custom rules you'll need to provide a service that takes the message as input and returns the desired validation.

The KIE Sandbox expects that your service provides an endpoint for a POST request containing the commit message in its body:

```
POST http://yourdomain.com/commit-message-validation-url
Content-Type: text/plain

This is the commit message to be validated.
```

In return, your service should respond with a JSON object with the properties `result` and `reasons`.

- `result`: Boolean value ( `true` | `false` ). True if the validation passes, else false.
- `reasons`: Array of strings with the reasons why the validation failed (only when `result = false`). If `result = true` this property can be an empty array or omitted completely.

### Validations

- #### Validation success

```bash
HTTP/1.1 200 OK
Content-Type: application/json

{
    "result": true,
}
```

- #### Validation failed (single reason)

```bash
HTTP/1.1 200 OK
Content-Type: application/json

{
    "result": false,
    "reasons": ["Message exceeds the maximum length of 72 characters."]
}
```

- #### Validation failed (multiple reasons)

```bash
HTTP/1.1 200 OK
Content-Type: application/json

{
    "result": false,
    "reasons": [
      "Message exceeds the maximum length of 72 characters.",
      "Missing required prefix with issue number in the format: my-issue#123."
    ]
}
```

### Errors

- #### Unreachable URL

  The KIE Sandbox will display an error message if the validation service URL is unreachable and won't allow the user to proceed with the commit.

- #### HTTP Status different from 200
  If the service responds with an HTTP code other than 200, an error message is displayed alongside the HTTP Code + the response body of the request in full.

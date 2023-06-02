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

   |                            Name                             |                                                         Description                                                         |                               Default                               |
   | :---------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------: |
   |             `KIE_SANDBOX_EXTENDED_SERVICES_URL`             |                                        The URL that points to the Extended Services.                                        | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |              `KIE_SANDBOX_GIT_CORS_PROXY_URL`               |                        The URL that points to the Git CORS proxy for interacting with Git providers.                        | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |       `KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL`       |                           The URL that points to base image that is used on DMN Dev deployments.                            | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |         `KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE`         |                          Require users to type a custom commit message when creating a new commit.                          | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   | `KIE_SANDBOX_CUSTOM_COMMIT_MESSAGES_VALIDATION_SERVICE_URL` |                                          Service URL to validate commit messages.                                           | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                `KIE_SANDBOX_AUTH_PROVIDERS`                 |    Authentication providers configuration. Used to enable integration with GitHub Enterprise Server instances and more.     | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                 `KIE_SANDBOX_ACCELERATORS`                  | Accelerators configuration. Used to add a template to a set of Decisions and Workflows, making it buildable and deployable. | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                    `KIE_SANDBOX_EDITORS`                    |                               Editors configuration. Used to enable/disable specific editors.                               | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                   `KIE_SANDBOX_APP_NAME`                    |                                   Allows KIE Sandbox to be referred by a different name.                                    | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |

   ### Examples

   1. Using a different Extended Services deployment.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value> -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   2. Enabling authentication with a GitHub Enterprise Server instance.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_AUTH_PROVIDERS='[{
        "id":"github_at_my_company", \
        "domain":"github.my-company.com", \
        "supportedGitRemoteDomains":["github.my-company.com","gist.github.my-company.com"], \
        "type":"github", \
        "name":"GitHub @ MyCompany", \
        "enabled":true, \
        "group":"git" \
      }]' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   3. Requiring users to input a custom commit message on every commit.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE='true' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   4. Requiring users to input a custom commit message on every commit and validate it via the example [Commit Message Validation Service](../../examples/commit-message-validation-service/README.md).

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE='true' KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL='http://localhost:8090/validate' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

   5. Adding Accelerators available for your users.

      ```bash
      podman run -t -p 8080:8080 -e KIE_SANDBOX_ACCELERATORS='[{ \
        name: "Quarkus", \
        iconUrl: "https://github.com/kiegroup/kie-sandbox-quarkus-accelerator/raw/0.0.0/quarkus-logo.png", \
        gitRepositoryUrl: "https://github.com/kiegroup/kie-sandbox-quarkus-accelerator", \
        gitRepositoryGitRef: "0.0.0", \
        dmnDestinationFolder: "src/main/resources/dmn", \
        bpmnDestinationFolder: "src/main/resources/bpmn", \
        otherFilesDestinationFolder: "src/main/resources/others", \
      }]' -i --rm quay.io/kie-tools/kie-sandbox-image:latest
      ```

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM quay.io/kie-tools/kie-sandbox-image:latest

   ENV KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value>
   ENV KIE_SANDBOX_GIT_CORS_PROXY_URL=<my_value>
   ENV KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL=<my_value>
   ENV KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE=<my_value>
   ENV KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL=<my_value>
   ENV KIE_SANDBOX_AUTH_PROVIDERS=<my_value>
   ENV KIE_SANDBOX_ACCELERATORS=<my_value>
   ENV KIE_SANDBOX_EDITORS=<my_value>
   ENV KIE_SANDBOX_APP_NAME=<my_value>
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

### Accelerators

Accelerators are Git repositories that contain a skeleton of an application and will convert a working directory with your .dmn and .bpmn files into a fully functional application that can be built and deployed.

After creating yours you must define where resources should be placed inside these repositories. For example, `.dmn` files should be placed inside `src/main/resources` for a Quarkus application. As a bonus, adding an image/logo can be used to better represent your Accelerator wherever it's listed.

#### The Accelerator configuration

Having all of that, it's time to create the configuration required to add it to the **KIE_SANDBOX_ACCELERATORS** list env var.
It looks like this:

```js
{
    name: "Your Accelerator name",
    iconUrl: "https://link.to/your/logo/image",
    gitRepositoryUrl: "https://github.com/...",
    gitRepositoryGitRef: "branchName",
    dmnDestinationFolder: "path/to/place/dmn/files",
    bpmnDestinationFolder: "path/to/place/bpmn/files",
    otherFilesDestinationFolder: "path/to/place/other/files",
}
```

- **name**: This is how the Accelerator will be known inside KIE Sandbox.
- **iconUrl**: An optional parameter to add an image/logo besides you Accelerator name.
- **gitRepositoryUrl**: This is where your Accelerator is hosted. Should be an URL that can be used with `git clone`.
- **gitRepositoryGitRef**: Where in your repository is this Accelerator located. Could be a branch, commit, tag, anything that can be used with `git checkout`.
- **dmnDestinationFolder**: Where your DMN and PMML files will be moved to after applying the Accelerator.
- **bpmnDestinationFolder**: Where your BPMN files will be moved to after applying the Accelerator.
- **otherFilesDestinationFolder**: Where other files will be moved to after applying the Accelerator.

Here's an example of what it should look like:

```js
{
    name: "Quarkus",
    iconUrl: `https://github.com/kiegroup/kie-sandbox-quarkus-accelerator/raw/0.0.0/quarkus-logo.png`,
    gitRepositoryUrl: "https://github.com/kiegroup/kie-sandbox-quarkus-accelerator",
    gitRepositoryGitRef: "main,
    dmnDestinationFolder: "src/main/resources/dmn",
    bpmnDestinationFolder: "src/main/resources/bpmn",
    otherFilesDestinationFolder: "src/main/resources/others",
}
```

### Editors

By default all three standard editors will be enabled (BPMN, DMN, PMML). To disable an editor simply delete/comment out the respective json.

- **extension**: The extension of the file that you want to edit.
- **filePathGlob**: The glob pattern of the file you want to edit.
- **editor.resourcesPathPrefix**: The path to the gwt-editor.
- **editor.path**: The path of the editor envelope.html.
- **card.title**: The title of the editor that will be displayed on the home page.
- **card.description**: Displays a short description of the editor under the title on the home page.

Here's an example of what it should look like:

```js
    {
      extension: "bpmn",
      filePathGlob: "**/*.bpmn?(2)",
      editor: {
        resourcesPathPrefix: "gwt-editors/bpmn",
        path: "bpmn-envelope.html",
      },
      card: {
        title: "Workflow",
        description: "BPMN files are used to generate business workflows.",
      },
    },
    {
      extension: "dmn",
      filePathGlob: "**/*.dmn",
      editor: {
        resourcesPathPrefix: "gwt-editors/dmn",
        path: "dmn-envelope.html",
      },
      card: {
        title: "Decision",
        description: "DMN files are used to generate decision models",
      },
    },
    {
      extension: "pmml",
      filePathGlob: "**/*.pmml",
      editor: {
        resourcesPathPrefix: "",
        path: "pmml-envelope.html",
      },
      card: {
        title: "Scorecard",
        description: "PMML files are used to generate scorecards",
      },
    }
```

## Custom branding

KIE Sandbox can be customized to show your own logo and/or branding by extending this image and overriding environment variables and files.

- **Header logo:** Override `/var/www/html/images/app_logo_rgb_fullcolor_reverse.svg`. Fixed height of `38px`.
- **Colored logo:** Override `/var/www/html/images/app_logo_rgb_fullcolor_default.svg`. Fixed height of `80px`.
- **Favicon:** Override `/var/www/html/favicon.svg`
- **App name:** Use the `KIE_SANDBOX_APP_NAME` environment variable.

## Custom base image for DMN Dev deployments

KIE Sandbox allows for the base image used on DMN Dev deplouyments to be customized. For example:

```docker
ENV KIE_SANDBOX_DMN_DEV_DEPLOYMENT_BASE_IMAGE_URL="quay.io/kie-tools/dmn-dev-deployment-base-image:latest"
```

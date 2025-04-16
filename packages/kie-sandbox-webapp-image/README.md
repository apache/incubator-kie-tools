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

# kie-sandbox-image

This package contains the `Containerfile/Dockerfile` and scripts to build a container image for KIE Sandbox. It also generated a JSON Schema for the `env.json` file, enabling it to be validated.

## Additional requirements

- docker

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- (Optional) The image name and tags can be customized by setting the following environment variables:

  ```bash
  export KIE_SANDBOX_WEBAPP_IMAGE__imageRegistry=<registry>
  export KIE_SANDBOX_WEBAPP_IMAGE__imageAccount=<account>
  export KIE_SANDBOX_WEBAPP_IMAGE__imageName=<image-name>
  export KIE_SANDBOX_WEBAPP_IMAGE__imageBuildTag=<image-tag>
  export KIE_SANDBOX_WEBAPP_IMAGE__imagePort=<port>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/kie-sandbox-webapp-image... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

## Run

- Start up a clean container with:

  ```bash
  docker run -t -p 8080:8080 -i --rm docker.io/apache/incubator-kie-sandbox-webapp:latest
  ```

  KIE Sandbox will be up at http://localhost:8080

## Customization

1. Run a container with custom environment variables:

   [comment]: <> (//TODO: Use EnvJson.schema.json to generate this documentation somehow.. See https://github.com/apache/incubator-kie-issues/issues/16)

   |                            Name                             |                                                         Description                                                         |                               Default                               |
   | :---------------------------------------------------------: | :-------------------------------------------------------------------------------------------------------------------------: | :-----------------------------------------------------------------: |
   |             `KIE_SANDBOX_EXTENDED_SERVICES_URL`             |                                        The URL that points to the Extended Services.                                        | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                `KIE_SANDBOX_CORS_PROXY_URL`                 |                        The URL that points to the CORS proxy for interacting with external services.                        | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |         `KIE_SANDBOX_DEV_DEPLOYMENT_BASE_IMAGE_URL`         |                           The URL that points to the Base image that is used on Dev Deployments.                            | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |  `KIE_SANDBOX_DEV_DEPLOYMENT_QUARKUS_BLANK_APP_IMAGE_URL`   |                     The URL that points to the Quarkus Blank App image that is used on Dev Deployments.                     | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |   `KIE_SANDBOX_DEV_DEPLOYMENT_DMN_FORM_WEBAPP_IMAGE_URL`    |                          The URL that points to form webapp image that is used on Dev Deployments.                          | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |       `KIE_SANDBOX_DEV_DEPLOYMENT_IMAGE_PULL_POLICY`        |                                          The image pull policy for Dev Deployments                                          | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |         `KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE`         |                          Require users to type a custom commit message when creating a new commit.                          | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   | `KIE_SANDBOX_CUSTOM_COMMIT_MESSAGES_VALIDATION_SERVICE_URL` |                                          Service URL to validate commit messages.                                           | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                `KIE_SANDBOX_AUTH_PROVIDERS`                 |    Authentication providers configuration. Used to enable integration with GitHub Enterprise Server instances and more.     | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                 `KIE_SANDBOX_ACCELERATORS`                  | Accelerators configuration. Used to add a template to a set of Decisions and Workflows, making it buildable and deployable. | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                    `KIE_SANDBOX_EDITORS`                    |                               Editors configuration. Used to enable/disable specific editors.                               | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |
   |                   `KIE_SANDBOX_APP_NAME`                    |                                   Allows KIE Sandbox to be referred by a different name.                                    | See [ defaultEnvJson.ts ](../online-editor/build/defaultEnvJson.ts) |

   ### Examples

   1. Using a different Extended Services deployment.

      ```bash
      docker run -t -p 8080:8080 -e KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value> -i --rm docker.io/apache/incubator-kie-sandbox-webapp:latest
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

   2. Enabling authentication with a GitHub Enterprise Server instance.

      ```bash
      docker run -t -p 8080:8080 \
      -e "KIE_SANDBOX_AUTH_PROVIDERS=$(cat << EOConfig
      [
          {
            "id":"github_at_my_company",
            "domain":"github.ibm.com",
            "supportedGitRemoteDomains":["github.ibm.com","gist.github.ibm.com"],
            "type":"github",
            "name":"GitHub @ MyCompany",
            "enabled":true,
            "group":"git"
          }
      ]
      EOConfig
      )" \
      -i --rm docker.io/apache/incubator-kie-sandbox-webapp:latest
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

   3. Requiring users to input a custom commit message on every commit.

      ```bash
      docker run -t -p 8080:8080 -e KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE='true' -i --rm docker.io/apache/incubator-kie-sandbox-webapp:latest
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

   4. Requiring users to input a custom commit message on every commit and validate it via the example [Commit Message Validation Service](../../examples/commit-message-validation-service/README.md).

      ```bash
      docker run -t -p 8080:8080 -e KIE_SANDBOX_REQUIRE_CUSTOM_COMMIT_MESSAGE='true' KIE_SANDBOX_CUSTOM_COMMIT_MESSAGE_VALIDATION_SERVICE_URL='http://localhost:8090/validate' -i --rm docker.io/apache/incubator-kie-sandbox-webapp:latest
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

   5. Adding Accelerators available for your users.

      ```bash
      docker run -t -p 8080:8080 \
      -e "KIE_SANDBOX_AUTH_PROVIDERS=$(cat << EOConfig
      [
        {
          "name": "Quarkus",
          "iconUrl": "https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator/raw/0.0.0/quarkus-logo.png",
          "gitRepositoryUrl": "https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator",
          "gitRepositoryGitRef": "0.0.0",
          "dmnDestinationFolder": "src/main/resources/dmn",
          "bpmnDestinationFolder": "src/main/resources/bpmn",
          "otherFilesDestinationFolder": "src/main/resources/others",
        }
      ]
      EOConfig
      )" \
      -i --rm docker.io/apache/incubator-kie-sandbox-webapp:latest
      ```

      _NOTE: Replace `docker` with `podman` if necessary._

2. Write a custom `Containerfile/Dockerfile` from the image:

   ```docker
   FROM docker.io/apache/incubator-kie-sandbox-webapp:latest

   ENV KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value>
   ENV KIE_SANDBOX_CORS_PROXY_URL=<my_value>
   ENV KIE_SANDBOX_DEV_DEPLOYMENT_BASE_IMAGE_URL=<my_value>
   ENV KIE_SANDBOX_DEV_DEPLOYMENT_QUARKUS_BLANK_APP_IMAGE_URL=<my_value>
   ENV KIE_SANDBOX_DEV_DEPLOYMENT_DMN_FORM_WEBAPP_IMAGE_URL=<my_value>
   ENV KIE_SANDBOX_DEV_DEPLOYMENT_IMAGE_PULL_POLICY=<my_value>
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
    iconUrl: `https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator/raw/0.0.0/quarkus-logo.png`,
    gitRepositoryUrl: "https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator",
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

## Custom images for DMN Dev Deployments

KIE Sandbox allows for the images used on DMN Dev Deployments to be customized. For example:

```docker
ENV KIE_SANDBOX_DEV_DEPLOYMENT_BASE_IMAGE_URL="docker.io/apache/incubator-kie-sandbox0dev-deployment-base:latest"
ENV KIE_SANDBOX_DEV_DEPLOYMENT_QUARKUS_BLANK_APP_IMAGE_URL="docker.io/apache/incubator-kie-sandbox0dev-deployment-quarkus-blank-app:latest"
ENV KIE_SANDBOX_DEV_DEPLOYMENT_DMN_FORM_WEBAPP_IMAGE_URL="docker.io/apache/incubator-kie-sandbox0dev-deployment-dmn-form-webapp:latest"
```

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

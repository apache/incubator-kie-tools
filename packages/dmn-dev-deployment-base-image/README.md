# Testing changes to the dmn-dev-deployment-base-image

After making changes to either `dmn-dev-deployment-form-webapp` or `dmn-dev-deployment-quarkus-app` you may need to rebuild the `dmn-dev-deployment-base-image` to be used while developing. To aid in this scenario, a tool was created to build and load the new image to possible places where it can be deployed (Kubernetes clusters, such as Kind and Minkube, as well as OpenShift sandboxes).

## Testing your image with KIE Sandbox

**To use this tool, simply run `pnpm create-test-image:<target>`, with target being one of `openshift`, `minikube`, `kind` or `build-only`.**

After building and loading the image you'll need to start KIE Sandbox with special environment variables so that it uses the local testing image instead of pulling the `latest` or `daily-dev` tags from `quay.io`.

Go to the `online-editor` package and run the following command:

### If testing with OpenShift:

- `DMN_DEV_DEPLOYMENT_BASE_IMAGE__registry="" DMN_DEV_DEPLOYMENT_BASE_IMAGE__account="" ONLINE_EDITOR__dmnDevDeploymentBaseImageRegistry="" ONLINE_EDITOR__dmnDevDeploymentBaseImageAccount="" ONLINE_EDITOR__dmnDevDeploymentBaseImagePullPolicy="IfNotPresent" pnpm start`

### If testing with Kind or Minikube:

- `ONLINE_EDITOR__dmnDevDeploymentBaseImagePullPolicy="IfNotPresent" pnpm start`

After that you can start deploying to your local Kubernetes clusters or your OpenShift Dev Sandbox.

---

## Usage

`pnpm create-test-image <command> [options]`

## Commands

### **`build-only [options]`**

> Builds the image locally and store it in your local Docker image registry

### **`kind [options]`**

> Builds the image locally and push it to your Kind cluster

### **`minikube [options]`**

> Builds the image locally and push it to your Minikube cluster

### **`openshift [options]`**

> Builds the image directly on your OpenShift cluster and store it in an ImageStream (the 'oc' CLI tool needs to be installed and logged in to your cluster)

## Options:

| Option              | Description                                                                                                                                                             | Possible values                       | Default value   | Required           |
| ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------- | --------------- | ------------------ |
| -t, --tag           | Name and optionally a tag in the name:tag format                                                                                                                        | string                                |                 | :heavy_check_mark: |
| -f, --file          | Dockerfile/Containerfile path                                                                                                                                           | string                                | `Containerfile` |                    |
| -c, --context       | The path to be packaged with your built image                                                                                                                           | string                                | `.`             |                    |
| -a, --arch          | The target build architecture. If not provided will default to the native architecture (This parameter is ignored if targeting OpenShift as it will always build amd64) | `arm64`, `amd64`                      |                 |                    |
| --build-arg         | Build arg to be passed to the Docker builder in the format `<arg>=<value>` (Can be used multiple times)                                                                 | `<arg1>=<value1> <arg2>=<value2> ...` |                 |                    |
| --kind-cluster-name | Your Kind cluster name (only used if loading image to a Kind cluster)                                                                                                   | string                                | `kind`          |                    |
| -h, --help          | Show help                                                                                                                                                               |                                       |                 |                    |

## Examples:

### Build and load an image to a Minikube cluster using custom build-args:

- `pnpm create-test-image minikube -t my-image-name:my-tag --build-arg MY_ARG1=1 MY_ARG2=my_arg2_value`
  or
- `pnpm create-test-image minikube -t my-image-name:my-tag --build-arg MY_ARG1=1 --build-arg MY_ARG2=my_arg2_value`

### Build and load an image to a Kind cluster name kie-sandbox-dev-cluster:

- `pnpm create-test-image kind --kind-cluster-name kie-sandbox-dev-cluster -t my-image-name:my-tag`

### Build an image in a OpenShift cluster:

- `pnpm create-test-image openshift -t my-image-name:my-tag`

### Create an image from the Containerfile and context path:

- `pnpm create-test-image build-only -t quay.io/my-user/my-image-name:latest -f my/context/path/Containerfile -c my/context/path`

## Custom branding

The DMN Dev deployment base image can be customized to show your own logo by extending this image and overriding files.

- **Header logo:** Override `/tmp/kogito/dmn-dev-deployment-quarkus-app/src/main/resources/META-INF/resources/images/app_logo_rgb_fullcolor_reverse.svg`. Fixed height of `38px`.
- **Favicon:** Override `/tmp/kogito/dmn-dev-deployment-quarkus-app/src/main/resources/META-INF/resources/favicon.svg`

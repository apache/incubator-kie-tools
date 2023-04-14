# Testing changes to the dmn-dev-deployment-base-image

After making changes to either `dmn-dev-deployment-form-webapp` or `dmn-dev-deployment-quarkys-app` you may need to rebuild the `dmn-dev-deployment-base-image` to be used while developing. To aid in this scenario, a tool was created to build and load the new image to possible places where it can be deployed (Kubernetes clusters, such as Kind and Minkube, as well as OpenShift sandboxes).

To use this tool, simply run `pnpm create-test-image ...` with the parameters described below:

## Parameters:

- **--target** , **-t** :
  - description: Where to create and load the built image.
  - options:
    - _build-only_: Builds the image locally and store it in your local Docker image registry.
    - _kind_: Builds the image locally and push it to your Kind cluster (the --kind-cluster-name parameter is required in this case).
    - _minikube_: Builds the image locally and push it to your Minikube cluster.
    - _openshift_: Builds the image directly on your OpenShift cluster and store it in an ImageStream (the 'oc' CLI tool needs to be installed and logged in to your cluster).
- **--arch**, **-a**:
  - description: [Optional] The target build architecture. If not provided will default to the native architecture. (This parameter is ignored if targeting OpenShift as it will always build amd64)
  - options:
    - _arm64_: ARM image, good for ARM Macs or other ARM based environments.
    - _amd64_: x86_64 image, good for everything else.
- **--kind-cluster-name**, **-knc**:
  - description: [Required if target = kind] Your Kind cluster name. Required to load images to it.
- **--help**, **-h**:
  - description: Displays this help text.

## Examples of usage:

- Building and loading image to a Kind cluster:
  - `pnpm create-dev-image --target kind -kind-cluster-name kie-sandbox-dev-cluster`
  - `pnpm create-dev-image -t kind -kcn kie-sandbox-dev-cluster`
- Building and loading and arm64 image to a Minikube cluster:
  - `pnpm create-dev-image --target minikube --arch arm64`
  - `pnpm create-dev-image -t minikube -a arm64`
- Creating an OpenShift build:
  - `pnpm create-dev-image --target openshift`
  - `pnpm create-dev-image -t openshift`
- Build only:
  - `pnpm create-dev-image --target build-only`
  - `pnpm create-dev-image -t build-only`
- Build only x86_64 image:
  - `pnpm create-dev-image --target build-only --arch amd64`
  - `pnpm create-dev-image -t build-only -a amd64`

## Testing your image with the KIE Sandbox

After building and loading the image you'll need to run KIE Sandbox with a special environment variable so that it uses the local testing image instead of pulling the `latest` or `daily-dev` tags from `quay.io`.

Go to the `online-editor` package and run the following command:

- `DMN_DEV_DEPLOYMENT__useTestImages=true pnpm start`

After that you can start deploying to your local Kubernetes clusters or your OpenShift Dev Sandbox.

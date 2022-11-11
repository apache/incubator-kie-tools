# kie-sandbox-image

This package contains the `Containerfile`/`Dockerfile` and scripts to build a container image for KIE Sandbox.

## Additional requirements

- podman (for Linux)
- docker (for macOS)

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name and tags can be customized by setting the following environment variables:

```bash
$ export KIE_SANDBOX__imageRegistry=<registry>
$ export KIE_SANDBOX__imageAccount=<account>
$ export KIE_SANDBOX__imageName=<image-name>
$ export KIE_SANDBOX__imageBuildTags=<image-tags>
```

Default values can be found [here](./env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ pnpm -F @kie-tools/kie-sandbox-image... build:prod
```

Then check out the image:

```bash
$ podman images
```

## Run

Start up a new container with:

```bash
$ podman run -p 8080:8080 -i --rm quay.io/kie-tools/kie-sandbox-image:latest
```

KIE Sandbox will be up at http://localhost:8080

## Customization

There are three options to set custom values. Check out the examples below.

1. Write a custom `Containerfile` from our image:

   ```docker
   FROM quay.io/kie-tools/kie-sandbox-image:latest

   ENV KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value>
   ENV KIE_SANDBOX_GIT_CORS_PROXY_URL=<my_value>
   ENV KIE_SANDBOX_AUTH_PROVIDERS=<my_value>
   ```

1. Run a container locally with custom environment variables:

   Runtime environment variables can be passed to the containerized KIE Sandbox.

   Currently, the following environment variables are supported:

   |                Name                 |                                 Description                                  | Default                |
   | :---------------------------------: | :--------------------------------------------------------------------------: | ---------------------- |
   | `KIE_SANDBOX_EXTENDED_SERVICES_URL` |           The URL that points to the KIE Sandbox Extended Services           | http://localhost:21345 |
   |  `KIE_SANDBOX_GIT_CORS_PROXY_URL`   | The URL that points to the Git CORS proxy for interacting with Git providers | https://localhost:3000 |
   |    `KIE_SANDBOX_AUTH_PROVIDERS`     |                                //FIXME: Tiago                                | //FIXME: Tiago         |

   ```bash
   $ podman pull quay.io/kie-tools/kie-sandbox-image:latest
   $ podman run -t -p 8080:8080 -e KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value> -i --rm quay.io/kie-tools/kie-sandbox-image:latest
   ```

1. Create the application from the image in OpenShift and set the deployment environment variable right from the OpenShift UI.

# kie-sandbox-image

This package contains the `Containerfile` and scripts to build a container image for the KIE Sandbox.

## Additional requirements

- podman

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

Default values can be found [here](../build-env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ pnpm build:prod @kie-tools/kie-sandbox-image...
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

The KIE Sandbox will be up at http://localhost:8080

## Customization

Runtime environment variables can be passed to the containerized KIE Sandbox.

Currently, the following environment variables are supported:

|                Name                 |                              Description                              | Default                                                                                                               |
| :---------------------------------: | :-------------------------------------------------------------------: | --------------------------------------------------------------------------------------------------------------------- |
| `KIE_SANDBOX_EXTENDED_SERVICES_URL` |       The URL that points to the KIE Sandbox Extended Services        | http://localhost:21345                                                                                                |
|          `CORS_PROXY_URL`           | The URL that points to the cors-proxy for the interaction with GitHub | https://cors-proxy-kie-sandbox.rhba-cluster-0ad6762cc85bcef5745bb684498c2436-0000.us-south.containers.appdomain.cloud |

There are three options to set custom values. Check out the examples below.

1. Run our image locally with a custom environment variable:

```bash
$ podman pull quay.io/kie-tools/kie-sandbox-image:latest
$ podman run -p 8080:8080 -e KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value> -i --rm quay.io/kie-tools/kie-sandbox-image:latest
```

2. Write a custom `Containerfile` from our image:

```docker
FROM quay.io/kie-tools/kie-sandbox-image:latest

ENV KIE_SANDBOX_EXTENDED_SERVICES_URL=<my_value>
```

3. Create the application from our image in OpenShift and set the deployment environment variable right from the OpenShift UI.

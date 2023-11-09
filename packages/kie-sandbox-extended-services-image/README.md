# kie-sandbox-extended-services-image

This package contains the `Containerfile` and scripts to build a container image for the Extended Services.

## Additional requirements

- docker or podman

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name and tags can be customized by setting the following environment variables:

```bash
$ export KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry=<registry>
$ export KIE_SANDBOX_EXTENDED_SERVICES__imageAccount=<account>
$ export KIE_SANDBOX_EXTENDED_SERVICES__imageName=<image-name>
$ export KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTags=<image-tags>
```

Default values can be found [here](../build-env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ pnpm build:prod @kie-tools/kie-sandbox-extended-services-image...
```

Then check out the image:

```bash
$ docker images
```

or

```bash
$ podman images
```

## Run

Start up a new container with:

```bash
$ docker run -p 21345:21345 -i --rm quay.io/kie-tools/kie-sandbox-extended-services-image:latest
```

or

```bash
$ podman run -p 21345:21345 -i --rm quay.io/kie-tools/kie-sandbox-extended-services-image:latest
```

The service will be up at http://localhost:21345

If you need to change port or Host/IP, use the following parameters:

```bash
EXTENDED_SERVICES_HOST (Defaults to 0.0.0.0)
EXTENDED_SERVICES_PORT (Defaults to 21345)
```

For example:

```bash
$ docker run -p 21000:22222 -i --rm  -e EXTENDED_SERVICES_HOST=127.0.0.1 -e EXTENDED_SERVICES_PORT=22222 quay.io/kie-tools/kie-sandbox-extended-services-image:latest
```

or

```bash
$ podman run -p 21000:22222 -i --rm -e EXTENDED_SERVICES_HOST=127.0.0.1 -e EXTENDED_SERVICES_PORT=22222 quay.io/kie-tools/kie-sandbox-extended-services-image:latest
```

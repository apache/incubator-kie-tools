# cors-proxy-image

This package contains the `Containerfile` and scripts to build a container image for the [@kie-tools/cors-proxy](https://github.com/apache/incubator-kie-tools/packages/cors-proxy).

## Additional requirements

- docker or podman

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name, tags and port can be customized by setting the following environment variables:

```bash
$ export CORS_PROXY_IMAGE__imageRegistry=<registry>
$ export CORS_PROXY_IMAGE__imageAccount=<account>
$ export CORS_PROXY_IMAGE__imageName=<image-name>
$ export CORS_PROXY_IMAGE__imageBuildTags=<image-tags>
$ export CORS_PROXY_IMAGE__imagePort=<port>
$ export CORS_PROXY_IMAGE__imageOrigin=<origin>
$ export CORS_PROXY_IMAGE__imageVerbose=<verbose>
```

Default values can be found [here](./env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ pnpm @kie-tools/cors-proxy-image... build:prod
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
$ docker run -p 8080:8080 -i --rm quay.io/kie-tools/cors-proxy-image:latest
```

or

```bash
$ podman run -p 8080:8080 -i --rm quay.io/kie-tools/cors-proxy-image:latest
```

The service will be up at http://localhost:8080

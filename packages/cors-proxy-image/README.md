# cors-proxy-image

This package contains the `Containerfile` and scripts to build a container image for the [@kie-tools/cors-proxy](https://github.com/apache/incubator-kie-tools/packages/cors-proxy).

## Additional requirements

- docker or podman

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name and tags can be customized by setting the following environment variables:

```bash
$ export CORS_PROXY_IMAGE__imageRegistry=<registry>
$ export CORS_PROXY_IMAGE__imageAccount=<account>
$ export CORS_PROXY_IMAGE__imageName=<image-name>
$ export CORS_PROXY_IMAGE__imageBuildTags=<image-tags>
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

### Container configuration

It's possible to configure certain parameters of the container using the following env variables:

- _CORS_PROXY_HTTP_PORT_: Sets the HTTP Port the proxy should listen to
- _CORS_PROXY_ORIGIN_: Sets the value of the 'Access-Control-Allow-Origin' header, defaults to `*`
- _CORS_PROXY_VERBOSE_: Allows the proxy to run in verbose mode... useful to trace requests on development environments. Defaults to `false`

For example setting an `.env` file like:

```bash
CORS_PROXY_HTTP_PORT=8080
CORS_PROXY_ORIGIN=*
CORS_PROXY_VERBOSE=false
```

or by passing the variables as arguments like

```bash
$ docker run -p 8080:8080 -e CORS_PROXY_HTTP_PORT=8080 -e CORS_PROXY_ORIGIN=* -e CORS_PROXY_VERBOSE=false -i --rm quay.io/kie-tools/cors-proxy-image:latest
```

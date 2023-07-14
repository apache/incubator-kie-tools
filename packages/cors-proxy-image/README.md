# cors-proxy-image

This package contains the `Containerfile` and scripts to build a container image for the [@kie-tools/cors-proxy](https://github.com/kiegroup/kie-tools/packages/cors-proxy).

## Additional requirements

- docker or podman

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name and tags can be customized by setting the following environment variables:

```bash
$ export CORS_PROXY__imageRegistry=<registry>
$ export CORS_PROXY__imageAccount=<account>
$ export CORS_PROXY__imageName=<image-name>
$ export CORS_PROXY__imageBuildTags=<image-tags>
```

The image accepts the following configurations via environment variables:

- _CORS_PROXY\_\_port_: Configures the HTTP Port for the proxy, defaults to `8080`
- _CORS_PROXY\_\_origin_: Sets the value of the 'Access-Control-Allow-Origin' header, defaults to `*`
- _CORS_PROXY\_\_selfSignedCertificates_: Allows the proxy supporting self-signed certificates, useful for local development. It disables the certificate validation, not recommended for production environments. Defaults to `false`.
- _CORS_PROXY\_\_verbose_: Allows the proxy to run in verbose mode... useful to trace requests on development environments

For example:

```bash
$ export CORS_PROXY__port=8080
$ export CORS_PROXY__origin=*
$ export CORS_PROXY__selfSignedCertificates=true
$ export CORS_PROXY__verbose=false
```

Default values can be found [here](../build-env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ pnpm build:prod @kie-tools/cors-proxy-image...
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

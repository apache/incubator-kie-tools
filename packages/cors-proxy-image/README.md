# cors-proxy-image

This package contains the `Containerfile` and scripts to build a container image for the [@isomorphic-git/cors-proxy](https://github.com/isomorphic-git/cors-proxy).

## Additional requirements

- podman

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD_docker=true
```

The image name and tags can be customized by setting the following environment variables:

```bash
$ export CORS_PROXY__imageRegistry=<registry>
$ export CORS_PROXY__imageAccount=<account>
$ export CORS_PROXY__imageName=<image-name>
$ export CORS_PROXY__imageBuildTags=<image-tags>
```

Default values can be found [here](../build-env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ lerna run build:prod --scope=@kie-tools/cors-proxy-image --include-dependencies --stream
```

Then check out the image:

```bash
$ podman images
```

## Run

Start up a new container with:

```bash
$ podman run -p 8080:8080 -i --rm quay.io/kie-tools/cors-proxy-image:latest
```

The service will be up at http://localhost:8080

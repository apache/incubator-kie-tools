# cors-proxy-image

This package contains the `Containerfile` and scripts to build a container image for the [@isomorphic-git/cors-proxy](https://github.com/isomorphic-git/cors-proxy).

## Additional requirements

- podman

## Build

Enable the image to be built:

```bash
$ export KIE_TOOLS_BUILD__buildContainerImages=true
```

The image name and tags can be customized by setting the following environment variables:

```bash
$ export GIT_CORS_PROXY__imageRegistry=<registry>
$ export GIT_CORS_PROXY__imageAccount=<account>
$ export GIT_CORS_PROXY__imageName=<image-name>
$ export GIT_CORS_PROXY__imageBuildTags=<image-tags>
```

Default values can be found [here](../build-env/index.js).

After setting up the environment variables, run the following in the root folder of the repository to build the package:

```bash
$ pnpm build:prod @kie-tools/git-cors-proxy-image...
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

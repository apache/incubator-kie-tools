# kogito-swf-common

This package contains the necessary resources to help other modules to build the `kogito-swf-{builder|devmode}` images.

The contents of this package are:

- [Makefile](./resources/Makefile): convenience tool to help building and testing the images.
- [Cekit Modules](./resources/modules): Common Cekit Module descriptors to be used when building the images.
- [Utility Scripts](./resources/scripts): Utility scripts to be used during the image building process or bootstraping packages
- [Testing infra](./resources/tests): Testing infra to help testing the generated images

## Requirements

- **python3** with the following packages installed:
  - `behave` `lxml` `docker` `docker-squash` `elementPath` `pyyaml` `ruamel.yaml` `python-dateutil` `Jinja2` `pykwalify` `colorlog` `click`
- **cekit 4.11.0**: [docs.cekit.io](https://docs.cekit.io/en/latest/index.html)
- **make**
- **docker** or **podman**

## Using the Makefile

To build and tests the images the package provides a convenient `Makefile` that will do the hard work for you. It relies in the following Envs (or arguments):

- `SWF_IMAGE_NAME`: (required) Specifies the image name to build. It should match the image descriptor.
- `SWF_IMAGE_REGISTRY`: Image registry to use, defaults to 'quay.io'
- `SWF_IMAGE_REGISTRY_ACCOUNT`: Image registry account to use, defaults to 'kiegroup'
- `SWF_IMAGE_TAG`: Custom tag for the image. If not provided it will use the version in the image descriptor.

- `QUARKUS_PLATFORM_VERSION`: (required) Quarkus platform version to use inside the image.
- `KOGITO_VERSION`: (required) Kogito platform version to use inside the image.

- `CEKIT_BUILD_OPTIONS`: extra build options, please refer to [docs.cekit.io](https://docs.cekit.io/en/latest/index.html)
- `BUILD_ENGINE`: (docker/podman) engine used to build the image, defaults to docker
- `BUILD_ENGINE_OPTIONS`: extra build options to pass to the build engine

## Building images..

- Copy your image descriptor and modules along with the contents of the `resources` into a separate folder (eg: `/tmp/build`)
-

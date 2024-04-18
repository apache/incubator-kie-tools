# kogito-swf-builder

This package contains the `cekit` image descriptor (`kogito-swf-builder-image.yaml`) and modules needed to build the `kogito-swf-builder`
image along with the modules and scripts provided in `@kie-tools/kogito-swf-common`.

## Additional requirements

- **python3** with the following packages installed:
  - `behave` `lxml` `docker` `docker-squash` `elementPath` `pyyaml` `ruamel.yaml` `python-dateutil` `Jinja2` `pykwalify` `colorlog` `click`
- **cekit 4.11.0**: [docs.cekit.io](https://docs.cekit.io/en/latest/index.html)
- **s2i**: [source-to-image](https://github.com/openshift/source-to-image)
- **make**
- **docker** or **podman**

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- (Optional) The image name and tags can be customized by setting the following environment variables:

  ```bash
  export KOGITO_SWF_BUILDER_IMAGE__registry=<registry>
  export KOGITO_SWF_BUILDER_IMAGE__account=<account>
  export KOGITO_SWF_BUILDER_IMAGE__name=<image-name>
  export KOGITO_SWF_BUILDER_IMAGE__buildTag=<image-tag>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/kogito-swf-builder build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

  or

  ```bash
  podman images
  ```

## Testing the generated image (only for Linux)

- With the image generated, run:

  ```bash
  pnpm -F @kie-tools/kogito-swf-builder image:test
  ```

## Envs

|                 Name                 |                    Description                     |       Default        |
| :----------------------------------: | :------------------------------------------------: | :------------------: |
| `KOGITO_SWF_BUILDER_IMAGE__registry` | Registry where the generated image will be pushed. |      "quay.io"       |
| `KOGITO_SWF_BUILDER_IMAGE__account`  |        Account where image will be stored.         |      "kiegroup"      |
|   `KOGITO_SWF_BUILDER_IMAGE__name`   |              SWF Builder Image name.               | "kogito-swf-builder" |
| `KOGITO_SWF_BUILDER_IMAGE__buildTag` |                    Tag to use .                    |       "latest"       |

<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# sonataflow-image-common

This package contains the necessary resources to help other modules to build the `sonataflow-{builder|devmode}` images.

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
- **docker**

## Using the Makefile

To build and tests the images the package provides a convenient `Makefile` that will do the hard work for you. It relies in the following Envs (or arguments):

- `SWF_IMAGE_NAME`: (required) Specifies the image name to build. It should match the image descriptor.
- `SWF_IMAGE_REGISTRY`: Image registry to use, defaults to 'docker.io'
- `SWF_IMAGE_REGISTRY_ACCOUNT`: Image registry account to use, defaults to 'apache'
- `SWF_IMAGE_TAG`: Custom tag for the image. If not provided it will use the version in the image descriptor.

- `QUARKUS_PLATFORM_VERSION`: (required) Quarkus platform version to use inside the image.
- `KOGITO_VERSION`: (required) Kogito platform version to use inside the image.

- `CEKIT_BUILD_OPTIONS`: extra build options, please refer to [docs.cekit.io](https://docs.cekit.io/en/latest/index.html)
- `BUILD_ENGINE`: (docker/podman) engine used to build the image, defaults to docker
- `BUILD_ENGINE_OPTIONS`: extra build options to pass to the build engine

## Building images..

- Copy your image descriptor and modules along with the contents of the `resources` into a separate folder (eg: `/tmp/build`)
- ***

Apache KIE (incubating) is an effort undergoing incubation at The Apache Software
Foundation (ASF), sponsored by the name of Apache Incubator. Incubation is
required of all newly accepted projects until a further review indicates that
the infrastructure, communications, and decision making process have stabilized
in a manner consistent with other successful ASF projects. While incubation
status is not necessarily a reflection of the completeness or stability of the
code, it does indicate that the project has yet to be fully endorsed by the ASF.

Some of the incubating project’s releases may not be fully compliant with ASF
policy. For example, releases may have incomplete or un-reviewed licensing
conditions. What follows is a list of known issues the project is currently
aware of (note that this list, by definition, is likely to be incomplete):

- Hibernate, an LGPL project, is being used. Hibernate is in the process of
  relicensing to ASL v2
- Some files, particularly test files, and those not supporting comments, may
  be missing the ASF Licensing Header

If you are planning to incorporate this work into your product/project, please
be aware that you will need to conduct a thorough licensing review to determine
the overall implications of including this work. For the current status of this
project through the Apache Incubator visit:
https://incubator.apache.org/projects/kie.html

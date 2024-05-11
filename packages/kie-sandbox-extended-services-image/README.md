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
$ export KIE_SANDBOX_EXTENDED_SERVICES__imagePort=<port>
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

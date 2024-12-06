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

## (private) Apache KIE™ :: Tools :: Maven M2 repo via HTTP

Used to expose the local Maven repository (E.g., ~/.m2/repository) via HTTP, so it can be used inside other container builds. This is important so that containers can include applications that depend on locally-built Maven artifacts.

Containers referencing this must make use of `settings.xml.envsubst` so that:

1. The default Maven blocker for repositories exposed via HTTP without TSL is disabled.
1. The exposed Maven repository via HTTP is accessible from within the container image build.

> NOTE: Host network access is not enabled by default during image builds. Use the `--allowHostNetworkAccess` option of `@kie-tools/image-builder` to enable it.

### Usage

Running the container:

- `docker run --name m2-repo-via-http -v ~/.m2/repository:/var/www/html -dit -p 8008:80 docker.io/apache/incubator-kie-tools-maven-m2-repo-via-http:main`

Interpolating settings.xml

- Linux and macOS:

  `M2_REPO_VIA_HTTP_URL_WITHOUT_PROTOCOL=localhost:8008 envsubst < settings.xml.envsubst > /tmp/settings.xml`

- Windows (PowerShell):

  `(Get-Content settings.xml.envsubst) -replace '$M2_REPO_VIA_HTTP_URL_WITHOUT_PROTOCOL', localhost:8008 | Set-Content /tmp/settings.xml`

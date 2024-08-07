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

# Dev Deployment Base Image

Docker image with Java and Maven, as well as the dev-deployment-upload-service binary installed and ready to be used.

## Build arguments

- `BUILDER_IMAGE_ARG`: The base image used for building this image (defaults to `registry.access.redhat.com/ubi9/openjdk-17:1.20`).
  - Tested with:
    - registry.access.redhat.com/ubi9/openjdk-17:1.20
    - icr.io/appcafe/ibm-semeru-runtimes:open-17-jdk-ubi-minimal

## Environment variables

### Pre defined (have a default value)

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR`: The directory to extract the files uploaded via the Upload Service. Defaults to `/app` inside the container.
- `DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT`: The port where the Upload Service will listen on. Defaults to `8080`.

### Required

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY`: A string that represents the API Key the Upload Service accepts. It should be passed as a Query Param when making requests to the service.

### Optional

- `DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH`: If the Upload Service is not running in the root path of the URL, this variable should be specified.

## Test locally

Run the image with:

- `docker run -p 8080:8080 -e DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY=123 docker.io/apache/incubator-kie-sandbox-dev-deployment-base:main 'dev-deployment-upload-service && ./mvnw quarkus:dev'`

Then upload a zip file containing the resources (full Java project)

- `curl -X POST -H "Content-Type: multipart/form-data" -F "myFile=@<ABSOLUTE_PATH_TO_YOUR_FILE>" 'http://localhost:8080/upload?apiKey=123'`

---

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

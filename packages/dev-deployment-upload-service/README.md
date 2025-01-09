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

# Dev Deployment Upload Service

This package is used on the KIE Sandbox's Dev Deployments feature and should be the first command to run when a deployed container spins up.

The Dev Deployment Upload Service runs an HTTP server that accepts ZIP file uploads to the `/upload` endpoint. You can check that the service is ready to accept uploads via the `/upload-status` endpoint.

When an upload is done, the application will unzip the file at the configured location and exit with a 0 code. If anything goes wrong during execution, it will exit with code 1 and a helpful message will be printed to stderr.

### Installation:

#### Via built binaries

Extract the .tar.gz file and copy the binary to your `/usr/local/bin` directory:

```bash
tar xf dist/dev-deployment-upload-service-<OS>-<ARCH>-<VERSION>.tar.gz -C /usr/local/bin
```

You may need to run the command as root using `sudo`.

#### Via install script

- Run a server for the built files:
  ```bash
  pnpm build:dev
  pnpm start-test-servers
  ```
- In another terminal, run the installer script:
  ```bash
  curl http://localhost:8092/getDevDeploymentUploadService.sh | bash
  ```
- After installed, you may stop the test servers:
  ```bash
  pnpm stop-test-servers
  ```

### Usage:

```
USAGE: `dev-deployment-upload-service`. Arguments are passed using env vars:
- DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR	: Required. Where the uploaded zip will be extracted to. If it doesn't exist, it will be created.
- DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT		: Required. Port where the HTTP Server will run at. The /upload endpoint will be made available.
- DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY		: Required. Allowed API Key used as a queryParam at the /upload endpoint.
- DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH		: Subpath where the API endpoints will be at. Defaults to "/".
```

### Example:

For a Dev Deployment that runs a Quarkus application, the intended use is:

```Dockerfile
...

EXPOSE [port number]

ENV DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR=[unzip dir path]
ENV DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT=[port number]

CMD ["/bin/bash", "-c", "dev-deployment-upload-service && cd [unzip dir path] && mvn quarkus:dev"]
```

Then, when running your image, pass the following environment variables as parameters:

```
ENV DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY=[api key]
ENV DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH=[subpath]
```

On KIE Sandbox Dev Deployments Kubernetes/OpenShift YAMLs, you can pass them like:

```yaml
...
spec:
  containers:
    ...
    env:
      - name: DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY
        value: 'dev'
      - name: DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH
        value: '/'
```

### Develop:

```bash
# 1.
DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR='/tmp/upload-service-dev' \
DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT='8091' \
DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY='dev' \
DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH='/' \
pnpm start
```

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

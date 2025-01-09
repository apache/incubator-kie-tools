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

# KIE Sandbox Dev Deployment DMN Form Webapp Image

This image is ready to be used for Dev Deployments that contain Decisions (`.dmn`) on KIE Sandbox.
It expects a Quarkus application to be running in the same host, which it will use to fetch information to render a form that let's users interact with a Decision.

For example, if this image is running on `localhost:8080/form-webapp` it will try to fetch the OpenAPI specification from `localhost:8080/q/openapi` and use that to map all other routes and endpoints for the Quarkus application, plus all of the form inputs necessary for each DMN.

Unfortunatelly, with this condition, is pratically impossible to run these container and a Quarkus app locally, because it's impossible to run two containers on the same port.
To solve this issue, two environment variables were created to configure this behavior:

- DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_ORIGIN: Sets the origin of the Quarkus app (`http://localhost:8080` for example); Defaults to `""`, in which case it's replaced by `window.location.origin`.
- DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_PATH: Sets the relative path of the Quarkus app in that origin (`/dev-deployment-1234` for example); Defaults to `""`, in which case it's replaced by `..` or `/` depending on the path the webapp is being served from.

## Run

```bash
docker run -p 8081:8081 docker.io/apache/incubator-kie-sandbox-dev-deployment-dmn-form-webapp:main
# KIE Sandbox Dev Deployment DMN Form Webapp will be up at http://localhost:8081
```

## Developing

To develop this webapp you can start the webapp container and a quarkus app container to be used as backend:

```bash
docker run -d -p 8080:8080 -e DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY=dev docker.io/apache/incubator-kie-sandbox-dev-deployment-kogito-quarkus-blank-app:main

docker run -d -p 8081:8081 -e DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_ORIGIN=http://localhost:8080 docker.io/apache/incubator-kie-sandbox-dev-deployment-dmn-form-webapp:main
```

The Quarkus app will be running on port 8080 and you will have to upload some DMNs to it (check the [dev-deployment-kogito-quarkus-blank-app-image README](../dev-deployment-kogito-quarkus-blank-app-image/README.md));

The DMN Form Webapp will be running on port 8081 and will be using the Quarkus app on port 8080 as it's backend.

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

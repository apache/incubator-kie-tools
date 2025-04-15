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

Form Webapp to fill and submit decisions inputs. Requires a Quarkus Kogito app as it's backend.

## Environment Variables

- DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_ORIGIN: Sets the origin of the quarkus app (`http://localhost:8080` for example); Defaults to `""`, in which case it's replaced by `window.location.origin`.
- DEV_DEPLOYMENT_DMN_FORM_WEBAPP_QUARKUS_APP_PATH: Sets the relative path of the quarkus app in that origin (`/dev-deployment-1234` for example); Defaults to `""`, in which case it's replaced by `..` or `/` depending on the path the webapp is being served from.

## Running

Use the dev-webapp and dev quarkus-app to run and test the form webapp:

```bash
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

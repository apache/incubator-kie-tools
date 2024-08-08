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

# cors-proxy

This package contains a `cors-proxy`, which is a simple Node.js application intended to be used to solve CORS issues while connecting our front end tools (online-editor, serverless-logic-web-tools) with external services (GitHub, Bitbucket, OpenShift, Kubernetes...)

# Build

```bash
$ pnpm @kie-tools/cors-proxy... build:prod
```

# Configuration

The `cors-proxy` can be configured via environment variables:

- _CORS_PROXY_HTTP_PORT_: Sets the HTTP Port the proxy should listen to
- _CORS_PROXY_ORIGIN_: Sets the value of the 'Access-Control-Allow-Origin' header, defaults to `*`
- _CORS_PROXY_VERBOSE_: Allows the proxy to run in verbose mode... useful to trace requests on development environments. Defaults to `false`

For example:

```bash
$ export CORS_PROXY_HTTP_PORT=8080
$ export CORS_PROXY_ORIGIN=*
$ export CORS_PROXY_VERBOSE=false
```

# Running `cors-proxy`

After building the package and setting up the environment variables, in the package folder run the following command:

```bash
$ node ./dist/index.js
```

# Running `cors-proxy` in dev mode.

```bash
$ pnpm @kie-tools/cors-proxy start
```

You can also use the following envs to configure `cors-proxy` when starting in dev-mode:

```bash
$ export CORS_PROXY__port=*
$ export CORS_PROXY__origin=*
$ export CORS_PROXY__verbose=false
```

Default values can be found [here](./env/index.js).

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

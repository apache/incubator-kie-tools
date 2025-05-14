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

# End to End tests

There is a set of playwright tests in `tests-e2e` directory. To run them, please refer to one option bellow.

## Run nativelly

- `KIE_TOOLS_BUILD__runEndToEndTests=true KIE_TOOLS_BUILD__containerizedEndToEndTests=false pnpm test-e2e`

## Run in a container (recommended for more compatibility)

- `KIE_TOOLS_BUILD__runEndToEndTests=true KIE_TOOLS_BUILD__containerizedEndToEndTests=true pnpm test-e2e`

> **NOTE:**
> Before test development, you may need to build `online-editor` as:
>
> - `kie-tools$ pnpm bootstrap`
> - `kie-tools$ pnpm -F @kie-tools/online-editor... build:dev`

# Extended Services, CORS Proxy, and KIE Sandbox Accelerator Quarkus dependencies

These dependencies are required for full functionality. `pnpm start` will automatically start all of them, but everything can be started individually with:

- `pnpm start:kie-sandbox`
- `pnpm start:kie-sandbox-accelerator-quarkus`
- `pnpm start:cors-proxy`
- `pnpm start:extended-services`

> **NOTE:** > `pnpm start:extended-services` will force the Extended Services application to start with host `0.0.0.0` (by setting `EXTENDED_SERVICES_JAVA__host="0.0.0.0"`).
> This is important due to networking issues when running on WSL (Windows Subsystem for Linux).

## Testing insecure/invalid TLS certificates with Git providers

- Install [Caddy](https://caddyserver.com/docs/install)
- Run `sudo caddy run` or `sudo caddy stop`
- If asked to install certificates, choose "No"
- If on macOS or Windows add these lines to your `hosts` file:
  ```
  127.0.0.1 github.localhost
  127.0.0.1 gist.github.localhost
  127.0.0.1 bitbucket.localhost
  127.0.0.1 api.bitbucket.localhost
  ```
  _On Linux, localhosts subdomains work by default_
- Add these new AuthProviders to [defaultEnvJson.ts](build/defaultEnvJson.ts)
  ```js
  {
    enabled: true,
    id: "GitHub_at_Local",
    domain: "github.localhost",
    supportedGitRemoteDomains: [
      "github.localhost",
      "gist.github.localhost"
    ],
    type: AuthProviderType.github,
    name: "GitHub at Local",
    group: AuthProviderGroup.GIT,
    insecurelyDisableTlsCertificateValidation: true
  },
  {
    enabled: true,
    id: "Bitbucket_at_Local",
    domain: "bitbucket.localhost",
    supportedGitRemoteDomains: [
      "bitbucket.localhost",
    ],
    type: AuthProviderType.bitbucket,
    name: "Bitbucket at Local",
    group: AuthProviderGroup.GIT,
    insecurelyDisableTlsCertificateValidation: true
  }
  ```
- Start online-editor;
- To connect to `GitHub at Local` use a github.ibm.com account;
- To connect to `Bitbucket at Local` use a bitbucket.org account.

Obs.: _To use different Git providers remember to change the Caddyfile_;

Obs.: _`github.com` and `github.<enterprise_name>.com` use different APIs. If your Caddyfile is proxying `github.com` you'll need to change the `getGithubInstanceApiUrl` function in [github/Hooks.tsx](src/github/Hooks.tsx)_.

# Dev Deployments

Read more about it here: [Dev Deployments Architecture](./docs/DEV_DEPLOYMENTS_ARCHITECTURE.md)

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

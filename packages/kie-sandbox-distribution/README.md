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

# kie-sandbox-distribution

This package contains the `docker-compose` file to run KIE Sandbox and all related services.

## Additional requirements

- docker

## Build

- Enable the image to be built:

  ```bash
  export KIE_TOOLS_BUILD__buildContainerImages=true
  ```

- For local builds, Run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/kie-sandbox-distribution... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

- (Optional) Use the following environment variables:

  ```bash
  export KIE_SANDBOX_DISTRIBUTION__kieSandboxImageRegistry=<kieSandboxImageRegistry>
  export KIE_SANDBOX_DISTRIBUTION__kieSandboxImageAccount=<kieSandboxImageAccount>
  export KIE_SANDBOX_DISTRIBUTION__kieSandboxImageName=<kieSandboxImageName>
  export KIE_SANDBOX_DISTRIBUTION__kieSandboxImageTag=<kieSandboxImageTag>
  export KIE_SANDBOX_DISTRIBUTION__kieSandboxPort=<kieSandboxPort>
  export KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry=<extendedServicesImageRegistry>
  export KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount=<extendedServicesImageAccount>
  export KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName=<extendedServicesImageName>
  export KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag=<extendedServicesImageTag>
  export KIE_SANDBOX_DISTRIBUTION__extendedServicesPort=<extendedServicesPort>
  export KIE_SANDBOX_DISTRIBUTION__corsProxyImageRegistry=<corsProxyImageRegistry>
  export KIE_SANDBOX_DISTRIBUTION__corsProxyImageAccount=<corsProxyImageAccount>
  export KIE_SANDBOX_DISTRIBUTION__corsProxyImageName=<corsProxyImageName>
  export KIE_SANDBOX_DISTRIBUTION__corsProxyImageTag=<corsProxyImageTag>
  export KIE_SANDBOX_DISTRIBUTION__corsProxyPort=<corsProxyPort>
  ```

## Run

- Run KIE Sandbox with docker compose and current environment variables

  ```bash
  pnpm docker:start
  ```

  KIE Sandbox will be up at http://localhost:9090

- Run KIE Sandbox with docker compose and default environment variables

  ```bash
  docker compose --env-file .env up
  ```

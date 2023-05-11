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
  pnpm -F @kie-tools/kie-sandbox-image... build:prod
  ```

- Then check if the image is correctly stored:

  ```bash
  docker images
  ```

- (Optional) Use the following environment variables:

  ```bash
  export KIE_SANDBOX_DISTRIBUITION__kieSandboxImageTag=<kieSandboxImageTag>
  export KIE_SANDBOX_DISTRIBUITION__kieSandboxPort=<sandbox_port>
  export KIE_SANDBOX_DISTRIBUITION__extendedServicesImageTag=<extendedServicesImageTag>
  export KIE_SANDBOX_DISTRIBUITION__extendedServicesPort=<extendedServicesPort>
  export KIE_SANDBOX_DISTRIBUITION__gitCorsProxyImageTag=<gitCorsProxyImageTag>
  export KIE_SANDBOX_DISTRIBUITION__gitCorsProxyPort=<gitCorsProxyPort>
  ```

## Run

- Run KIE Sandbox with docker compose

  ```bash
  docker compose up
  ```

  KIE Sandbox will be up at http://localhost:9090

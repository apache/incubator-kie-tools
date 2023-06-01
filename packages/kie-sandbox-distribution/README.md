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
  export KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageRegistry=<gitCorsProxyImageRegistry>
  export KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageAccount=<gitCorsProxyImageAccount>
  export KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageName=<gitCorsProxyImageName>
  export KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageTag=<gitCorsProxyImageTag>
  export KIE_SANDBOX_DISTRIBUTION__gitCorsProxyPort=<gitCorsProxyPort>
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

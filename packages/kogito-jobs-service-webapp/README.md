## Build

- The image name and tags can be customized by setting the following environment variables:

  ```bash
  export KOGITO_JOBS_SERVICE_WEBAPP_title=<title>
  export KOGITO_JOBS_SERVICE_WEBAPP_logo=<logo>
  export KOGITO_JOBS_SERVICE_WEBAPP_docLinkHref=<docLinkHref>
  export KOGITO_JOBS_SERVICE_WEBAPP_docLinkText=<docLinkText>
  export SONATAFLOW_JOBS_SERVICE_WEBAPP_docLinkHref=<docLinkHref>
  export SONATAFLOW_JOBS_SERVICE_WEBAPP_docLinkText=<docLinkText>
  ```

  > Default values can be found [here](./env/index.js).

- After optionally setting up the environment variables, run the following in the root folder of the repository to build the package:

  ```bash
  pnpm -F @kie-tools/kogito-jobs-service-webapp... build:prod
  ```

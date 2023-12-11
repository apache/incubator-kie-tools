# Dev deployments upload service

This package used on KIE Sandbox's Dev deployments feature, and should be the first command to run when a deployed container spins up.

The "Dev deployments upload service" runs an HTTP server that accepts ZIP file uploads to the `/upload` endpoint. When an upload is done, the application will unzip the file at the configured location and exit with a 0 code. If anything goes wrong during execution, it will exit with code 1 and a helpful message will be printed to stderr.

### Usage:

```
USAGE: `dev-deployment-upload-service`. Arguments are passed using env vars:
- DEV_DEPLOYMENT__UPLOAD_SERVICE_EXTRACT_TO_DIR	: Required. Where the uploaded zip will be extracted to. If it doesn't exist, it will be created.
- DEV_DEPLOYMENT__UPLOAD_SERVICE_PORT		: Required. Port where the HTTP Server will run at. The /upload endpoint will be made available.
- DEV_DEPLOYMENT__UPLOAD_SERVICE_API_KEY		: Required. Allowed API Key used as a queryParam at the /upload endpoint.
- DEV_DEPLOYMENT__UPLOAD_SERVICE_ROOT_PATH		: Subpath where the API endpoints will be at. Defaults to "/".
```

### Example:

For a Dev deployment that runs a Quarkus application, the intended use is:

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

On KIE Sandbox Dev deployments Kubernetes/OpenShift YAMLs, you can pass them like:

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
pnpm start:dev
```

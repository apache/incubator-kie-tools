## Dev deployments upload service

This package used on KIE Sandbox's Dev deployments feature, and should be the first command to run when a deployed container spins up.

The "Dev deployments upload service" runs an HTTP server that accepts ZIP file uploads to the `/upload` endpoint. When an upload is done, the application will unzip the file at the configured location and exit with a 0 code. If anything goes wrong during execution, it will exit with code 1 and a helpful message will be printed to stderr.

#### Usage:

`dmn-dev-deployments-upload-service --unzip-at [unzip dir path] --port [port number]`.

Both `--unzip-at` and `--port` arguments are required.

#### Example:

For a Dev deployment that runs a Quarkus application, the intended use is:

```Dockerfile

...

EXPOSE [port number]
CMD ["/bin/bash", "-c", "dmn-dev-deployments-upload-service --unzip-at [unzip dir path] --port [port number] && cd [unzip dir path] && mvn quarkus:dev"]
```

#### Develop:

- On macOS:
  `pnpm build:dev && ./dist/darwin/dev-deployments-upload-service --unzip-at 'some/dir/path' --port 8091`
  `open test.html`

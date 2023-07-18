# cors-proxy

This package contains a simple `cors-proxy` is a simple Node.js application intended to be used to solve cors issues while connecting our front end tools (online-editor, serverless-logic-web-tools) with external services (github, bitbucket, openshift, kubernetes...)

# Build

```bash
$ pnpm @kie-tools/cors-proxy... build:prod
```

# Configuration

The `cors-proxy` can be cofingured via environment variables:

- _CORS_PROXY_PORT_: Configures the HTTP Port for the proxy, defaults to `8080`
- _CORS_PROXY_ORIGIN_: Sets the value of the 'Access-Control-Allow-Origin' header, defaults to `*`
- _CORS_PROXY_ALLOW_SELF_SIGNED_CERTIFICATES_: Allows the proxy supporting self-signed certificates, useful for local development. It disables the certificate validation, not recommended for production environments. Defaults to `false`.
- _CORS_PROXY_VERBOSE_: Allows the proxy to run in verbose mode... useful to trace requests on development environments. Defaults to `false`

For example:

```bash
$ export CORS_PROXY_PORT=8080
$ export CORS_PROXY_ORIGIN=*
$ export CORS_PROXY_ALLOW_SELF_SIGNED_CERTIFICATES=true
$ export CORS_PROXY_VERBOSE=false
```

# Running `cors-proxy`

After building the package and setting up the environment variables, in the package folcer run the following command:

```bash
$ node ./dist/index.js
```

# Running `cors-proxy` in dev mode.

```bash
$ pnpm @kie-tools/cors-proxy start
```

You can also use the following envs to configure `cors-proxy` when starting in dev-mode:

```bash
$ export CORS_PROXY__port=8080
$ export CORS_PROXY__origin=*
$ export CORS_PROXY__selfSignedCertificates=true
$ export CORS_PROXY__verbose=false
```

Default values can be found [here](./env/index.js).

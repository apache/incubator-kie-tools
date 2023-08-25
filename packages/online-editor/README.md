# Integration tests

There is a set of cypress tests in `e2e-tests` folder. To run them, please refer to one option bellow.

## Run tests manually

More suitable for running particular tests from `e2e-tests`.

- `packages/extended-services$ pnpm start`
- `packages/online-editor$ pnpm start`
- `packages/online-editor$ pnpm cy:open`

## Run tests automatically

More suitable for running `e2e-tests` completely.

- `packages/online-editor$ START_SERVER_AND_TEST_INSECURE=true KOGITO_TOOLING_BUILD_testIT=true pnpm test:e2e`

> **NOTE:**
> Before test development, you may need to build `online-editor` as:
>
> - `kie-tools$ pnpm bootstrap`
> - `kie-tools$ pnpm -r -F @kie-tools/online-editor... build:dev`

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

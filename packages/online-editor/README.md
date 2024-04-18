# End to End tests

There is a set of cypress tests in `e2e-tests` folder. To run them, please refer to one option bellow.

## Run tests manually

More suitable for running particular tests from `e2e-tests`.

- `packages/extended-services$ pnpm start`
- `packages/online-editor$ pnpm start`
- `packages/online-editor$ pnpm cy:open`

## Run tests automatically

More suitable for running `e2e-tests` completely.

- `packages/online-editor$ KIE_TOOLS_BUILD__runEndToEndTests=true pnpm test:e2e`

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

# Dev deployments

KIE Sandbox allows for Dev deployments targeting OpenShift or simple Kubernetes clusters. This is achieved by applying pre-defined [Kubernetes](src/devDeployments/services/resources/kubernetes/index.ts) and [OpenShift](src/devDeployments/services/resources/openshift/index.ts) resources for each provider.

To apply those YAMLs the `k8s-yaml-to-apiserver-requests` library is used. It first maps the cluster API resources and then parses a YAML to make the required requests. This creates the resources at the Kubernetes cluster and return the resources created.

Dev deployments requires some information to be present on the resources metadata so that it can list and manage these resources, including a Dev deployment name, related workspace id and a created by tag with the value `kie-tools`. To make this easier a set of tokens is generated and can be used to interpolate variables inside the resource YAML. Here's an example:

```yaml
kind: Deployment
apiVersion: apps/v1
metadata:
  name: \${{ devDeployment.uniqueName }}
  namespace: \${{ devDeployment.kubernetes.namespace }}
  labels:
    app: \${{ devDeployment.uniqueName }}
    app.kubernetes.io/component: \${{ devDeployment.uniqueName }}
    app.kubernetes.io/instance: \${{ devDeployment.uniqueName }}
    app.kubernetes.io/name: \${{ devDeployment.uniqueName }}
    app.kubernetes.io/part-of: \${{ devDeployment.uniqueName }}
    \${{ devDeployment.labels.createdBy }}: kie-tools
    \${{ devDeployment.labels.partOf }}: \${{ devDeployment.uniqueName }}
  annotations:
    \${{ devDeployment.annotations.workspaceId }}: \${{ devDeployment.workspace.id }}
    \${{ devDeployment.annotations.workspaceName }}: \${{ devDeployment.workspace.name }}
```

As you can see, there are several variables in use here: `devDeployment.uniqueName`, `devDeployment.labels...`, `devDeployment.annotations...`, `devDeployment.workspace...`. These are replaced via an [interpolation implementation](/packages/k8s-yaml-to-apiserver-requests/src/interpolateK8sResourceYamls.ts).

## Required metadata, labels and annotations

For a successfull deployment this is the required information the resource should have:

```yaml
metadata:
  name: \${{ devDeployment.uniqueName }}
  namespace: \${{ devDeployment.kubernetes.namespace }}
  labels:
    \${{ devDeployment.labels.createdBy }}: kie-tools
    \${{ devDeployment.labels.partOf }}: \${{ devDeployment.uniqueName }}
  annotations:
    \${{ devDeployment.annotations.workspaceId }}: \${{ devDeployment.workspace.id }}
    \${{ devDeployment.annotations.workspaceName }}: \${{ devDeployment.workspace.name }}
```

- The `name` is how the deployment is identified across the board.
- The `namespace` is where the deployment should be created in the cluster and should be the same configured in the connected account.
- The labels (`createdBy` and `partOf`) are required so that KIE Sandbox can filter and map all resources related to a single deployment. **Important:** `createdBy` should always have the value `kie-tools`.
- The annotations (`workspaceId` and `workspaceName`) are useful to match a deployment to a workspace.

Anything else can be customized.

## Available tokens

Some tokens are pre-defined and others are generated during runtime (like `uniqueName` and `uploadService.apiKey`).

```js
devDeployment: {
  labels: {
    createdBy: "tools.kie.org/created-by",
    partOf: "tools.kie.org/part-of",
  },
  annotations: {
    workspaceId: "tools.kie.org/workspace-id",
    workspaceName: "tools.kie.org/workspace-name",
  },
  uniqueName: string,
  uploadService: {
    apiKey: string,
  },
  workspace: {
    id: string,
    name: string,
  },
  kubernetes: {
    namespace: string,
  },
},
```

These tokens can be referenced in the YAML resource using the following notation: `${{ varPath.varName }}`. Here are some examples from the tokens above:

- `${{ devDeployment.labels.createdBy }}`
- `${{ devDeployment.annotations.workspaceId }}`
- `${{ devDeployment.uniqueName }}`
- `${{ devDeployment.kubernetes.namespace }}`

**\*Obs.:** It's important to note that if you're defining a resource YAML in a `.js`/`.ts` file you'll need to escape the `$` character, so a variable would become `\${{ varPath.varName}}`.\*

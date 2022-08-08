# kn-plugin-workflow

`kn-plugin-workflow` is a plugin of the Knative Client, to enable users to quickly set up a local Kogito Serverless Workflow project from the command line.

[Read the documentation](https://kiegroup.github.io/kogito-docs/serverlessworkflow/main/tooling/kn-plugin-workflow-overview.html)

## Build from source

All the commands in this section should be performed in the monorepo root.

### Prerequisites

- Node `>= 16.13.2` _(To install, follow these instructions: https://nodejs.org/en/download/package-manager/)_
- pnpm `7.0.0` _(To install, follow these instructions: https://pnpm.io/installation)_
- Go `1.17` _(To install, follow these instructions: https://go.dev/doc/install)_

### Installing and linking dependencies

The following command will install the `kn-plugin-workflow` dependencies and link it with any other monorepo
package that is listed in the `package.json`:

- `pnpm bootstrap -F "@kie-tools/kn-plugin-workflow..." -F .`

### Building

It has two different strategies to build the `kn-plugin-workflow`:

- `build:dev` _(The build will generate one artifact that is compatible with your local machine)_
- `build:prod` _(The build will generate artifacts for all available architecture and run the available tests)_

To build the `kn-plugin-workflow` run the following command:

- `pnpm -r -F "@kie-tools/kn-plugin-workflow..." <build-strategy>`

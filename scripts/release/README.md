# Release Scripts

Local-first scripts for releasing Apache KIE Tools components. All scripts can be run locally for testing and are also called by Jenkins during the official release process.

## Available Scripts

| Script                                     | What it does                                                                         |
| ------------------------------------------ | ------------------------------------------------------------------------------------ |
| `release-all.sh`                           | Master script — runs all of the below                                                |
| `release-npm-packages.sh`                  | Build and publish npm packages                                                       |
| `release-chrome-extensions.sh`             | Build and publish Chrome extensions                                                  |
| `release-vscode.sh`                        | Build and publish VSCode extensions                                                  |
| `release-container-images.sh`              | Build, save, and push container images                                               |
| `release-helm-charts.sh`                   | Package and push Helm charts                                                         |
| `release-github-pages.sh`                  | Deploy sandbox webapp + standalone editors to GitHub Pages; push Quarkus accelerator |
| `release-kn-plugin-workflow.sh`            | Build kn-plugin-workflow binaries for all platforms                                  |
| `release-dev-deployment-upload-service.sh` | Build dev-deployment-upload-service binaries for all platforms                       |
| `create-source-tarball.sh`                 | Create Apache-compliant source tarball                                               |

## Flags

All scripts except `create-source-tarball.sh` accept the same three flags:

| Flag        | Effect                                                                 |
| ----------- | ---------------------------------------------------------------------- |
| _(none)_    | Build locally only — nothing is published or uploaded                  |
| `--rc`      | Build and collect artifacts into `release-artifacts/` for an Apache RC |
| `--publish` | Build and publish/push to the public registry or service               |

`release-kn-plugin-workflow.sh` and `release-dev-deployment-upload-service.sh` additionally require `--upload-url <url>` when using `--publish`.

`create-source-tarball.sh` accepts only `<version>` (and optional `--git-ref` / `--output-dir`). It always creates the tarball unconditionally — it does not accept `--rc` or `--publish`. When run via `release-all.sh`, the `--rc`/`--publish` flags are not forwarded to this script.

## Prerequisites

- Node.js 22, pnpm, Go, Helm 3, Docker — depending on which scripts you run
- Repository bootstrapped: `pnpm bootstrap`
- Credentials set as environment variables (only needed for `--publish`)

## Local Testing

### Full RC dry-run from a fresh clone

```bash
# 1. Bootstrap once
pnpm bootstrap

# 2. Build and collect all RC artifacts (linters/tests/e2e suppressed automatically)
./scripts/release/release-all.sh 10.2.0 --rc

# 3. Inspect
ls -lh release-artifacts/
```

### Fast iteration (repo already built)

If you have already run a full build and just want to re-collect artifacts:

```bash
rm -rf release-artifacts/
./scripts/release/release-all.sh 10.2.0 --rc --skip-build
```

`--skip-build` skips the `pnpm build:prod` step and goes straight to zipping/copying
from the existing `dist/` folders. Artifact collection is identical to a full run.

### Single component

```bash
./scripts/release/release-all.sh 10.2.0 --rc --components npm-packages
./scripts/release/release-all.sh 10.2.0 --rc --components vscode
./scripts/release/release-all.sh 10.2.0 --rc --skip-build --components helm-charts,source-tarball
```

## RC Artifact Names

All artifacts produced by `--rc` follow the Apache incubator convention:
`apache-kie-<version>-incubating-<artifact-name>.<ext>`

```
apache-kie-<version>-incubating-tools-npm-packages.zip
apache-kie-<version>-incubating-business-automation-chrome-extension.zip
apache-kie-<version>-incubating-business-automation-chrome-extension-editors.zip
apache-kie-<version>-incubating-bpmn-vscode-extension.vsix
apache-kie-<version>-incubating-dmn-vscode-extension.vsix
apache-kie-<version>-incubating-drl-vscode-extension.vsix
apache-kie-<version>-incubating-pmml-vscode-extension.vsix
apache-kie-<version>-incubating-kogito-bundle-vscode-extension.vsix
apache-kie-<version>-incubating-business-automation-bundle-vscode-extension.vsix
apache-kie-<version>-incubating-extended-services-vscode-extension.vsix
apache-kie-<version>-incubating-sandbox-webapp.zip
apache-kie-<version>-incubating-business-automation-standalone-editors.zip
apache-kie-<version>-incubating-sandbox-accelerator-quarkus.zip
apache-kie-<version>-incubating-sandbox-helm-chart.tar.gz
apache-kie-<version>-incubating-runtime-tools-console-helm-chart.tar.gz
apache-kie-<version>-incubating-<image-name>-image.tar.gz  (one per container image)
apache-kie-<version>-incubating-sonataflow-knative-plugin-linux-x86.zip
apache-kie-<version>-incubating-sonataflow-knative-plugin-macOS-arm64.zip
apache-kie-<version>-incubating-sonataflow-knative-plugin-macOS-x86.zip
apache-kie-<version>-incubating-sonataflow-knative-plugin-windows-x86.zip
apache-kie-<version>-incubating-sandbox-dev-deployment-upload-service-macOS-arm64.tar.gz
apache-kie-<version>-incubating-sandbox-dev-deployment-upload-service-macOS-x86.tar.gz
apache-kie-<version>-incubating-sandbox-dev-deployment-upload-service-linux-x86.tar.gz
apache-kie-<version>-incubating-sandbox-dev-deployment-upload-service-windows-x86.tar.gz
apache-kie-<version>-incubating-sources.zip  (source zip — always created by create-source-tarball.sh)
```

Container image names (`<image-name>`):
`kogito-base-builder`, `kogito-data-index-ephemeral`, `kogito-data-index-postgresql`,
`kogito-jit-runner`, `kogito-jobs-service-allinone`, `kogito-jobs-service-ephemeral`,
`kogito-jobs-service-postgresql`, `kogito-management-console`, `kogito-db-migrator-tool`,
`cors-proxy`, `sandbox-webapp`, `sandbox-extended-services`,
`sandbox-dev-deployment-base`, `sandbox-dev-deployment-dmn-form-webapp`,
`sandbox-dev-deployment-quarkus-blank-app`

## Credentials (for `--publish`)

| Script                                     | Required environment variables                                                                        |
| ------------------------------------------ | ----------------------------------------------------------------------------------------------------- |
| `release-npm-packages.sh`                  | `NPM_TOKEN`                                                                                           |
| `release-vscode.sh`                        | `VSCE_PAT`                                                                                            |
| `release-chrome-extensions.sh`             | `CHROME_CLIENT_ID`, `CHROME_CLIENT_SECRET`, `CHROME_REFRESH_TOKEN`, `CHROME_KIE_EDITORS_EXTENSION_ID` |
| `release-container-images.sh`              | `DOCKER_USERNAME`, `DOCKER_PASSWORD`                                                                  |
| `release-helm-charts.sh`                   | `HELM_REGISTRY`, optionally `HELM_USERNAME` / `HELM_PASSWORD`                                         |
| `release-github-pages.sh`                  | `GITHUB_TOKEN`                                                                                        |
| `release-kn-plugin-workflow.sh`            | `GITHUB_TOKEN`, `--upload-url`                                                                        |
| `release-dev-deployment-upload-service.sh` | `GITHUB_TOKEN`, `--upload-url`                                                                        |

See [`CHROME_STORE_SETUP.md`](CHROME_STORE_SETUP.md) for Chrome Web Store credential setup.

## Version Management

Before running release scripts, update the version throughout the repo:

```bash
pnpm update-version-to 10.2.0
pnpm update-kogito-version-to --maven 10.2.0
pnpm update-stream-name-to 10.2.0
```

## Troubleshooting

**Permission denied** — `chmod +x scripts/release/*.sh`

**pnpm not found** — `npm install -g pnpm`

**Build fails** — run `pnpm bootstrap` first

**NPM publish fails** — verify `NPM_TOKEN` is set and you have publish rights to `@kie-tools` packages

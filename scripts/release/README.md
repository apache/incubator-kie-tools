<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

# Release Scripts

Local-first scripts for releasing Apache KIE Tools. All components are released together under a single unified build.

## Available Scripts

| Script                     | What it does                                                                 |
| -------------------------- | ---------------------------------------------------------------------------- |
| `release.sh`               | **The** release script — builds all components and optionally publishes them |
| `create-source-tarball.sh` | Utility: creates an Apache-compliant source zip from the current `HEAD`      |

## Usage

```bash
./scripts/release/release.sh <version>             # build all components (dry run — nothing published)
./scripts/release/release.sh <version> --rc        # build + collect Apache RC artifacts
./scripts/release/release.sh <version> --publish   # build + publish to all public registries
```

### Optional flags

| Flag                 | Effect                                                                          |
| -------------------- | ------------------------------------------------------------------------------- |
| _(none)_             | Build everything locally — nothing is published or uploaded                     |
| `--rc`               | Build and collect all artifacts into `release-artifacts/` for an Apache RC vote |
| `--publish`          | Build and push/publish to all public registries and services                    |
| `--skip-build`       | Skip `pnpm build:prod` (use existing `dist/` output)                            |
| `--upload-url <url>` | GitHub Release upload URL (required by `--publish` for binary assets)           |
| `--registry <url>`   | Container registry (default: `docker.io/apache`)                                |

## What is released

`release.sh` covers all KIE Tools components in one build:

1. **NPM packages** — all public packages under `packages/`
2. **Chrome extensions** — `chrome-extension-pack-kogito-kie-editors`
3. **VSCode extensions** — all `.vsix` packages
4. **Container images** — all Kogito / sandbox images
5. **Helm charts** — sandbox and runtime-tools-consoles charts
6. **GitHub Pages / webapp** — online-editor, standalone editors, Quarkus accelerator
7. **kn-plugin-workflow** — cross-platform CLI binaries
8. **dev-deployment-upload-service** — cross-platform binaries
9. **Source tarball** — Apache-compliant source zip

## Prerequisites

- Node.js 22, pnpm, Go, Helm 3, Docker — depending on which components you need
- Repository bootstrapped: `pnpm bootstrap`
- Credentials set as environment variables (only needed for `--publish`)

## Local Testing

### Full RC dry-run from a fresh clone

```bash
# 1. Bootstrap once
pnpm bootstrap

# 2. Build and collect all RC artifacts (linters/tests/e2e suppressed automatically)
./scripts/release/release.sh 10.3.0 --rc

# 3. Inspect
ls -lh release-artifacts/
```

### Fast iteration (repo already built)

If you have already run a full build and just want to re-collect artifacts:

```bash
rm -rf release-artifacts/
./scripts/release/release.sh 10.3.0 --rc --skip-build
```

`--skip-build` skips the `pnpm build:prod` step and goes straight to zipping/copying
from the existing `dist/` folders. Artifact collection is identical to a full run.

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
apache-kie-<version>-incubating-sources.zip  (source zip — always created)
```

Container image names (`<image-name>`):
`kogito-base-builder`, `kogito-data-index-ephemeral`, `kogito-data-index-postgresql`,
`kogito-jit-runner`, `kogito-jobs-service-allinone`, `kogito-jobs-service-ephemeral`,
`kogito-jobs-service-postgresql`, `kogito-management-console`, `kogito-db-migrator-tool`,
`cors-proxy`, `sandbox-webapp`, `sandbox-extended-services`,
`sandbox-dev-deployment-base`, `sandbox-dev-deployment-dmn-form-webapp`,
`sandbox-dev-deployment-quarkus-blank-app`

## Credentials (for `--publish`)

| Component                     | Required environment variables                                                                        |
| ----------------------------- | ----------------------------------------------------------------------------------------------------- |
| NPM packages                  | `NPM_TOKEN`                                                                                           |
| VSCode extensions             | `VSCE_PAT`                                                                                            |
| Chrome extensions             | `CHROME_CLIENT_ID`, `CHROME_CLIENT_SECRET`, `CHROME_REFRESH_TOKEN`, `CHROME_KIE_EDITORS_EXTENSION_ID` |
| Container images              | `DOCKER_USERNAME`, `DOCKER_PASSWORD`                                                                  |
| Helm charts                   | `HELM_REGISTRY`, optionally `HELM_USERNAME` / `HELM_PASSWORD`                                         |
| GitHub Pages / webapp         | `GITHUB_TOKEN`                                                                                        |
| kn-plugin-workflow binaries   | `GITHUB_TOKEN`, `--upload-url`                                                                        |
| dev-deployment-upload-service | `GITHUB_TOKEN`, `--upload-url`                                                                        |

See [`CHROME_STORE_SETUP.md`](CHROME_STORE_SETUP.md) for Chrome Web Store credential setup.

## Version Management

Before running the release script, update the version throughout the repo:

```bash
pnpm update-version-to 10.3.0
pnpm update-kogito-version-to --maven 10.3.0
pnpm update-stream-name-to 10.3.0
```

## Troubleshooting

**Permission denied** — `chmod +x scripts/release/release.sh`

**pnpm not found** — `npm install -g pnpm`

**Build fails** — run `pnpm bootstrap` first

**NPM publish fails** — verify `NPM_TOKEN` is set and you have publish rights to `@kie-tools` packages

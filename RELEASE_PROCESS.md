Release workflow (post 0.13.0)

1. Update the `CHANGELOG.md` files and send a PR to `main`.
1. Create a `{version}-prerelease` branch from the commit you just made with the CHANGELOG updates.
1. Update version from `0.0.0` to `{version}` -- `yarn update-version-to {version}`.
1. Push `{version}-prerelease` branch to `origin`.
1. 🔨(automatic) WORKFLOW (`staging.yml`).
   - Create new draft release on GitHub.
   - Upload staging artifacts to draft release.
1. Perform sanity checks
   - ⚠️ Blocker found? Fix, cherry-pick to `main`, push to the `{version}-prerelease` branch, and go back to Step 5.
1. Update the release description with the release notes.
1. Remove the uploaded staging artifacts from the draft release.
1. Click on "Publish release"
1. 🔨(manual) WORKFLOW (`release.yml`)
   - Select what's going to be released on GitHub's UI
   - Trigger Workflow
   - ⚠️ Something failed? Delete the necessary artifacts from the release (for it to be uploaded again), and re-trigger only what failed.

#### STAGING ARTIFACTS

- [Phase 1] online-editor (GitHub release, GitHub Pages -> kiegroup.github.io/kogito-online-staging/{version}-prerelease)
- [Phase 1] chrome-extension (GitHub release)
- [Phase 1] desktop x3 (one for each OS) (GitHub release)
- [Phase 1] hub x3 (one for each OS) (GitHub release)
- [Phase 1] vscode-extension-dev (GitHub release)
- [Phase 1] vscode-extension-backend-dev (GitHub release)
-
- [Phase 2] dmn-dev-sandbox-deployment-base (Quay.io)
- [Phase 2] kie-tooling-extended-services x3 (one for each OS) (GitHub release, Dropbox? Drive?)
- [Phase 2] vscode-extension-bpmn-editor (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-dmn-editor (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-pmml-editor (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-kogito-bundle (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-redhat-business-automation-bundle (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
-
- [Phase 3] form-generation-tool (GitHub release)

#### PRODUCTION ARTIFACTS

- [Phase 1] online-editor (GitHub Pages -> kiegroup.github.io/kogito-online)
- [Phase 1] chrome-extension (GitHub release, Chrome Web Store)
- [Phase 1] vscode-extension-dev (GitHub release)
- [Phase 1] vscode-extension-backend-dev (GitHub release)
- [Phase 1] desktop x3 (one for each OS) (GitHub release)
- [Phase 1] hub x3 (one for each OS) (GitHub release)
- [Phase 1] NPM Packages (including Standalone Editors)
-
- [Phase 2] dmn-runner x3 (one for each OS) (GitHub release)
- [Phase 2] dmn-dev-sandbox-deployment-base (Quay.io)
- [Phase 2] vscode-extension-bpmn-editor (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-dmn-editor (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-pmml-editor (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-kogito-bundle (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
- [Phase 2] vscode-extension-redhat-business-automation-bundle (GitHub release -> Jenkins: VS Code Marketplace, OpenVSX)
-
- [Phase 3] Quarkus Dev UI (Maven central)
- [Phase 3] form-generation-tool (GitHub release)

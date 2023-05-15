Release workflow (post 0.13.0)

Make sure an associated tag to the release is created on:

- https://github.com/kiegroup/kie-samples
- https://github.com/kiegroup/kie-sandbox-quarkus-accelerator

---

1. Update the `CHANGELOG.md` files and send a PR to `main`.
1. Create a `{version}-prerelease` branch from the commit you just made with the CHANGELOG updates.
1. Update version from `0.0.0` to `{version}` -- `pnpm update-version-to {version}`.
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

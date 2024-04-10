Release workflow (post 0.13.0)

Make sure an associated tag to the release is created on:

- https://github.com/kiegroup/kie-samples @ {version}
- https://github.com/apache/incubator-kie-sandbox-quarkus-accelerator @ {version}
- https://github.com/kiegroup/serverless-logic-sandbox-deployment @ quarkus-accelerator-${version}

---

1. Update the `CHANGELOG.md` files and send a PR to `main`.
2. Create a `{version}-prerelease` branch from the commit you just made with the CHANGELOG updates.
3. Update version from `0.0.0` to `{version}` -- `pnpm update-version-to {version}`.
4. Push `{version}-prerelease` branch to `origin`.
5. 🔨(automatic) Jenkins job [Staging publish](https://ci-builds.apache.org/job/KIE/job/kie-tools/job/kie-tools-staging-publish).
   - Create new draft release on GitHub.
   - Upload staging artifacts to draft release.
6. 🔨(manual) WORKFLOW (`staging_publish_extended_services.yml`)
   - Upload Extended Services staging artifacts for MacOS and Windows to the draft release.
7. Perform sanity checks
   - ⚠️ Blocker found? Fix, cherry-pick to `main`, push to the `{version}-prerelease` branch, and go back to Step 5.
8. Update the release description with the release notes.
9. Remove the uploaded staging artifacts from the draft release.
10. Click on "Publish release"
11. 🔨(manual) Jenkins job [Release publish](https://ci-builds.apache.org/job/KIE/job/kie-tools/job/kie-tools-release-publish).

- Click on Build with Parameters
- Set the Tag and what is going to be released
- Click on build
- ⚠️ Something failed? Delete the necessary artifacts from the release (for it to be uploaded again), and re-trigger only what failed.

12. 🔨(manual) WORKFLOW (`release_publish_extended_services.yml`)

- Upload Extended Services release artifacts for MacOS and Windows to the release.

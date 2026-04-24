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

# Dependency Management Guide

This guide provides instructions for managing dependencies in the kie-tools monorepo and resolving `pnpm-lock.yaml` merge conflicts.

## Table of Contents

- [Understanding the Monorepo Structure](#understanding-the-monorepo-structure)
- [Adding or Updating Dependencies](#adding-or-updating-dependencies)
- [Resolving pnpm-lock.yaml Merge Conflicts](#resolving-pnpm-lockyaml-merge-conflicts)
- [Best Practices](#best-practices)
- [Troubleshooting](#troubleshooting)

## Understanding the Monorepo Structure

Start by reading the [Repository Manual](./MANUAL.md)!

The `pnpm-lock.yaml` file is a critical file that:

- Locks exact versions of all dependencies across the entire monorepo
- Ensures reproducible builds
- Contains complex internal references between packages
- **Should never be manually edited**

## Adding or Updating Dependencies

### For a Specific Package

1. Navigate to the package directory:

   ```bash
   cd packages/your-package-name
   ```

2. Add or update the dependency:

   ```bash
   # Add a new dependency
   pnpm add package-name

   # Add a dev dependency
   pnpm add -D package-name

   # Update an existing dependency
   pnpm update package-name

   # Update to a specific version
   pnpm add package-name@1.2.3
   ```

3. The `pnpm-lock.yaml` at the root will be automatically updated.

4. Commit both the `package.json` and `pnpm-lock.yaml`:
   ```bash
   git add packages/your-package-name/package.json pnpm-lock.yaml
   git commit -m "feat: add/update package-name in your-package-name"
   ```

### For the Root Workspace

1. From the repository root:

   ```bash
   # Add to root workspace
   pnpm add -w package-name

   # Add as dev dependency to root
   pnpm add -Dw package-name
   ```

2. Commit the changes:
   ```bash
   git add package.json pnpm-lock.yaml
   git commit -m "feat: add/update package-name in root workspace"
   ```

### Updating Multiple Packages

To update dependencies across multiple packages:

```bash
# Update a specific package everywhere
pnpm -r update package-name@<version>
```

### Updating Transitive Dependencies (CVE Fixes)

When a security vulnerability (CVE) is found in a transitive dependency (a dependency of your dependencies), follow these steps:

1. **Identify the vulnerable transitive dependency**:

   ```bash
   # Use pnpm to audit dependencies
   pnpm audit
   ```

2. **Find which first-level dependency includes it**:

   ```bash
   # Check the dependency tree
   pnpm why -r vulnerable-package-name
   ```

3. **Attempt to update the first-level dependency** (Preferred approach):

   ```bash
   # Try to update to a patched version (avoid major version upgrades if possible)
   pnpm update -r first-level-dependency@^1.2.3
   ```

4. **If the first-level dependency hasn't been patched yet, use pnpm overrides**:

   a. Open `pnpm-workspace.yaml` at the repository root

   b. Add an override in the `overrides` section with a justification comment:

   ```yaml
   overrides:
     # CVE-2024-XXXXX: Fix security vulnerability in <vulnerable-package>
     # Waiting for <first-level-dependency> to release patched version
     "vulnerable-package": "^<fixed.version>"
   ```

   Replace `vulnerable-package` with the name of the vulnerable package, `first-level-dependency` with the name of the first-level dependency that includes it, and `fixed.version` with the version of the vulnerable package that includes the fix.

   c. Run bootstrap to apply the override:

   ```bash
   pnpm bootstrap
   ```

   d. Verify the override worked:

   ```bash
   pnpm why vulnerable-package
   pnpm audit
   ```

5. **Commit the changes**:

   ```bash
   # If you updated a first-level dependency
   git add packages/your-package-name/package.json pnpm-lock.yaml
   git commit -m "fix: update first-level-dependency to resolve CVE-2024-XXXXX"

   # If you added an override
   git add pnpm-workspace.yaml pnpm-lock.yaml
   git commit -m "fix: override vulnerable-package to resolve CVE-2024-XXXXX"
   ```

**Important Notes:**

- Always prefer updating first-level dependencies over using overrides
- Avoid major version upgrades when possible to minimize breaking changes
- Always include the CVE number and justification in override comments
- Periodically review and remove overrides once first-level dependencies are patched
- Test thoroughly after applying overrides to ensure compatibility

## Resolving pnpm-lock.yaml Merge Conflicts

When you encounter a merge conflict in `pnpm-lock.yaml`, **DO NOT** manually edit the file or regenerate it. Follow these steps instead:

### Method 1: Using Git Merge Driver (Recommended)

This is the safest approach that preserves both branches' changes:

1. **Accept the incoming changes (main branch)**:

   ```bash
   git checkout --theirs pnpm-lock.yaml
   ```

2. **Verify and update the lockfile**:

   ```bash
   # This will validate and update the lockfile if needed
   pnpm bootstrap
   ```

3. **Stage all changes and complete the merge**:

   ```bash
   # Stage the lockfile (and any updates from bootstrap)
   git add pnpm-lock.yaml

   # Complete the merge
   git merge --continue
   ```

### Method 2: Using pnpm's Built-in Conflict Resolution

pnpm has some built-in capabilities to handle lockfile conflicts:

1. **Let pnpm attempt automatic resolution**:

   ```bash
   # Accept the conflicted state
   git add pnpm-lock.yaml

   # Run bootstrap to let pnpm resolve
   pnpm bootstrap

   # Stage the resolved lockfile
   git add pnpm-lock.yaml

   # Complete the merge
   git merge --continue
   ```

### What NOT to Do

- Don't manually edit `pnpm-lock.yaml` to resolve conflicts
- Don't delete `pnpm-lock.yaml` and regenerate it from scratch
- Don't use `git checkout --ours` without understanding the implications
- Don't commit a lockfile with merge conflict markers

## Best Practices

### Before Making Changes

1. **Always start from an updated main branch**:

   ```bash
   # Add upstream remote if not already added (HTTPS)
   git remote add upstream https://github.com/apache/incubator-kie-tools.git
   # OR using SSH
   git remote add upstream git@github.com:apache/incubator-kie-tools.git

   # Fetch latest changes from upstream
   git fetch upstream

   # Update your local main branch
   git checkout main
   git merge upstream/main

   # Push to your fork
   git push origin main

   # Create your feature branch
   git checkout -b your-feature-branch
   ```

2. **Ensure your lockfile is clean**:
   ```bash
   pnpm bootstrap
   ```

### During Development

1. **Make atomic commits**: Commit `package.json` and `pnpm-lock.yaml` together
2. **Keep lockfile changes minimal**: Only update what's necessary
3. **Test after dependency changes**: Run relevant tests to ensure compatibility

### Before Merging

1. **Merge latest main into your branch**:

   ```bash
   # Fetch latest changes from upstream
   git fetch upstream

   # Merge upstream main into your branch
   git merge upstream/main
   ```

2. **Verify lockfile integrity**:
   ```bash
   pnpm bootstrap
   ```

### Working with Workspace Dependencies

When adding dependencies between packages in the monorepo:

```bash
# Use workspace protocol
pnpm add @kie-tools/package-name@workspace:*
```

This ensures the local version is used during development.

## Troubleshooting

### Lockfile is Out of Sync

If you see errors about the lockfile being out of sync:

```bash
# Update the lockfile
pnpm bootstrap

# Commit the changes
git add pnpm-lock.yaml
git commit -m "chore: update pnpm-lock.yaml"
```

### Corrupted Lockfile

If the lockfile appears corrupted after a bad merge:

1. **Stash your package.json changes**:

   ```bash
   git stash
   ```

2. **Get a clean lockfile from main**:

   ```bash
   git checkout main -- pnpm-lock.yaml
   ```

3. **Restore your changes**:

   ```bash
   git stash pop
   ```

4. **Let pnpm update the lockfile**:

   ```bash
   pnpm bootstrap
   ```

5. **Commit the result**:
   ```bash
   git add pnpm-lock.yaml
   git commit -m "fix: resolve lockfile conflicts"
   ```

### CI/CD Failures

If CI fails with lockfile issues:

1. Ensure you're using the correct pnpm version
2. Verify `pnpm bootstrap` works locally
3. Check that all `package.json` changes are reflected in the lockfile

## Additional Resources

- [pnpm Documentation](https://pnpm.io/)
- [pnpm Workspaces](https://pnpm.io/workspaces)
- [pnpm CLI](https://pnpm.io/cli/add)
- [Repository Manual](./MANUAL.md)

## Getting Help

If you encounter issues not covered in this guide:

1. Check existing issues in the repository
2. Ask in the project's communication channels
3. Review recent PRs that modified dependencies for examples

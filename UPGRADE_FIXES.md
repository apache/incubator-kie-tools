# Manual fixes after `pnpm -r update`

This document records the manual, non-obvious steps taken to get the repository back into a
working state after running a repo-wide `pnpm -r update`. It's meant as a reference for why
these specific changes exist, since none of them are derivable from the diff alone.

## 1. Patches in `patches/` no longer matched installed versions

`pnpm -r update` bumped several patched dependencies within their `package.json` semver ranges,
which made the pinned patch versions in `patchedDependencies` stop matching and broke the install
with `ERR_PNPM_UNUSED_PATCH`.

Each patch was checked against the new resolved version to see if it was still needed:

- **`patches/immer@10.0.3.patch`** — removed. It added `export` to the `WritableDraft` type
  because immer didn't export it in 10.0.3. As of the new resolved version (`10.2.0`), immer
  natively exports `WritableDraft` from its `export { ... }` list. Re-applying the old patch on
  top of `10.2.0` causes a duplicate-export TypeScript error
  (`TS2484: Export declaration conflicts with exported declaration of 'WritableDraft'`).
- **`patches/karma-firefox-launcher@2.1.2.patch`** — removed. It fixed a wrong macOS binary
  suffix (`firefox-bin` → `firefox`). Upstream fixed the same bug in `2.1.3`, the new resolved
  version, so the patch is now redundant (and would fail to apply, since the "buggy" text it
  targets no longer exists).
- **`patches/zustand@4.4.2.patch`** → renamed to **`patches/zustand@4.5.7.patch`**. The patched
  file (`middleware/immer.d.ts`) is byte-for-byte identical between `4.4.2` and the new resolved
  version `4.5.7`, so the patch content is untouched — only the version in the filename and in
  `patchedDependencies` needed updating.
- **`patches/run-script-os@1.1.6.patch`** — untouched. No newer version exists within the
  `^1.1.6` range declared anywhere in the repo, so nothing changed here.

`package.json`'s `pnpm.patchedDependencies` was updated to match.

## 2. `prettier@3.9.0+` corrupts `package.json` files

### Symptom

Any `package.json` script value containing an embedded double quote (e.g. the helm chart
packages' `build:prod:win32`/`build:prod:linux:darwin` scripts, which wrap an inner
`"helm package ..."` argument in escaped quotes) got rewritten with **single-quoted** values,
which is not valid JSON at all. This happened whenever a package's own `install` lifecycle ran
`prettier --write .` on itself (`packages/kie-sandbox-helm-chart` and
`packages/runtime-tools-consoles-helm-chart` both do this), and also on a full repo-wide
`pnpm format`.

### Root cause

Bisected directly (by swapping specific `prettier` and `@nice-move/prettier-plugin-package-json`
builds against the same input) to a regression in **`prettier` itself, introduced in `3.9.0`**
(release notes: "Preserve number and string representation in the `json-stringify` parser").
Confirmed `prettier@3.8.5` is always valid regardless of plugin version, and `prettier@3.9.0+` is
always broken regardless of plugin version — the `@nice-move/prettier-plugin-package-json` bump
that shipped alongside it was a red herring.

### Fix

Pinned `prettier` to the exact known-good version `3.8.5` (not `^3.8.5`, so a future
`pnpm update` can't silently drift back into the broken `3.9.x` range). This has to be done
everywhere `prettier` is independently declared, since several workspace packages carry their own
`prettier` devDependency separate from root's:

- `package.json` (root)
- `scripts/bootstrap/package.json`
- `packages/kie-sandbox-helm-chart/package.json`
- `packages/bpmn-editor-standalone/package.json`
- `packages/runtime-tools-consoles-helm-chart/package.json`
- `packages/dmn-editor-standalone/package.json`

Verified by running a full repo-wide `pnpm format` and checking every `package.json` in the repo
parses as valid JSON afterwards, and that a second run is a no-op (idempotent).

### Side effect: one-time repo-wide reformat

Running `pnpm format` after the `prettier` bump (from `3.3.2`/`3.9.6` down to the pinned `3.8.5`)
applies a one-time reformatting diff across ~196 files that had never been formatted with this
exact prettier version before:

- ~119 `package.json` files get their keys re-sorted by
  `@nice-move/prettier-plugin-package-json`.
- ~75 `.ts`/`.tsx`/`.css`/`.md` files pick up minor `3.8.5` formatting rule changes (most visibly,
  parenthesizing `??` when it sits next to a ternary, e.g. `a ? b : (c ?? d)`).

This diff was intentionally applied and kept (not a bug — see the `pnpm format` verification
step above).

## 3. `chrome-extension-test-helper` type errors from `@types/selenium-webdriver` bump

`@types/selenium-webdriver` moved from `4.1.27` → `4.35.6` as part of the update. The new types
changed `WebElement.getAttribute()`'s return type from `Promise<string>` to
`Promise<string | null>`, breaking two call sites that declared a non-nullable `Promise<string>`
return type:

- `packages/chrome-extension-test-helper/src/framework/Element.ts` — `getAttribute()`
- `packages/chrome-extension-test-helper/src/framework/LocatorWaitAction.ts` — `value()`

Fixed by coalescing the possibly-`null` result with `?? ""` at each call site.

## 4. Repo-wide build failure: duplicate webpack instances (dual package hazard)

### Symptom

Any package whose own `webpack.config.js`/`webpack.config.ts` directly imports `webpack` (to
build `EnvironmentPlugin`, `ProvidePlugin`, etc.) could fail with:

```
TypeError: The 'compilation' argument must be an instance of Compilation
    at DefinePlugin.getCompilationHooks (.../webpack/lib/util/createHooksRegistry.js:22:10)
```

First seen on `runtime-tools-process-dev-ui-webapp`, then also hit on
`extended-services-vscode-extension` and `online-editor`, confirming this is systemic rather than
isolated to one package.

### Root cause

**Not a version mismatch** — every webpack copy involved was the exact same version,
`webpack@5.108.4`, and `webpack-cli@5.1.4`'s peer requirement (`webpack: "5.x.x"`) was satisfied
by all of them. The real cause is a pnpm "dual package hazard": webpack 5.108's bundled default
minifier is a separate package, `minimizer-webpack-plugin`, which declares `@swc/core` as an
**optional peer with no version range** (it only appears in `peerDependenciesMeta`, not in
`peerDependencies`). Since `@swc/core` happens to be reachable elsewhere in a package's own
dependency tree (via `jest` / `ts-node` / `@graphql-codegen/cli`, etc.), pnpm's per-branch peer
resolution can link `@swc/core` into _one_ of the two paths that resolve `webpack` (the package's
own top-level `webpack` devDependency) but not the other (the copy nested inside `webpack-cli`'s
own dependencies) — producing two separate physical module instances of the identical version.
Any `webpack.EnvironmentPlugin`/`ProvidePlugin`/etc. constructed from one instance and applied
against a `Compiler`/`Compilation` created by the _other_ instance trips webpack's internal
`instanceof Compilation` checks across the two module realms.

This is a known, currently **unresolved upstream issue**:
[pnpm/pnpm#9427](https://github.com/pnpm/pnpm/issues/9427) (open as of pnpm 10.8.0), with the
exact same stack trace shape. From that thread:

- The `instanceof Compilation` check that turns this from silent duplication into a hard crash
  was added in **webpack 5.96.0**.
- Aligning webpack versions project-wide does not reliably fix it (confirmed independently here
  too — all copies were already the same version).
- No pnpm maintainer fix has landed; the divergence depends on subtle, per-package dependency
  graph shape that can shift with unrelated dependency changes.

### What did _not_ reliably fix it (ruled out, for reference)

- **Flipping `.npmrc`'s `dedupe-peer-dependents` to `true`** — this pnpm setting collapses
  peer-resolved duplicates back into a single instance, but only when the peer sets are
  _identical_. The diverged `webpack` copies have genuinely different peer sets (with vs. without
  `@swc/core`), so this setting doesn't apply. Also risky to flip repo-wide regardless, since it's
  deliberately disabled to work around a separate, long-standing pnpm bug affecting
  `--filter`-scoped installs ([pnpm/pnpm#6300](https://github.com/pnpm/pnpm/issues/6300)).
- **Adding `@swc/core` as an explicit devDependency of the affected package** — converged the
  instance for some packages in some test runs, but was **not reproducible** under a clean
  reinstall; a rerun of the exact same change on the exact same package failed to converge and the
  build still crashed. The apparent earlier success was leftover state from prior experiments, not
  a real fix.
- **Adding `@swc/core` as a devDependency of the shared `@kie-tools-core/webpack-base` package**
  — didn't converge the one package that actually needed it (`runtime-tools-process-dev-ui-webapp`
  stayed diverged), because pnpm's peer computation for `webpack-cli`'s own _nested_ `webpack`
  dependency isn't influenced by a sibling devDependency of a workspace package it depends on.
- **`pnpm.packageExtensions` forcing `webpack-cli` itself to depend on `@swc/core`** — converged
  roughly half of the 13 affected packages (6 of 13) and left the rest diverged, including the
  original `runtime-tools-process-dev-ui-webapp`. `pnpm why @swc/core` confirmed `webpack-cli`'s
  own nested `webpack` resolution still had no path to `@swc/core` even after this change for the
  packages that didn't converge — pnpm doesn't always re-thread an already-resolved nested
  dependency chain through a newly added sibling.
- Combining the two above (packageExtensions on `webpack-cli` **and** a direct `@swc/core`
  devDependency on the package) did converge the one package tested this way, but given the
  inconsistency of every other config-only attempt, this wasn't trusted as reliable across all
  affected packages and wasn't applied repo-wide.
- **Bumping `webpack`/`webpack-cli`** — wouldn't help either, since this is an architectural
  choice in webpack's own bundled minifier (present in any webpack version that ships it), not a
  version incompatibility between webpack and webpack-cli.

### Fix

Resolve `webpack` through `webpack-cli`'s own module scope in each affected config file, instead
of relying on the package's own top-level `webpack` resolution. This guarantees the plugin
instances built in the config come from the exact same physical copy `webpack-cli` uses to run
the Compiler, regardless of how pnpm's peer graph happens to fork things:

```js
const webpack = require(
  require.resolve("webpack", { paths: [path.dirname(require.resolve("webpack-cli/package.json"))] })
);
// or, for destructured imports:
const { EnvironmentPlugin, ProvidePlugin } = require(
  require.resolve("webpack", { paths: [path.dirname(require.resolve("webpack-cli/package.json"))] })
);
```

This is deterministic (doesn't depend on pnpm peer-graph topology) and is the standard mitigation
pattern for Node.js "dual package hazard" issues generally, not a repo-specific hack.

Applied to every config file in the repo that directly imports `webpack` and constructs a plugin
instance from it — found by grepping all `webpack*.config.js` **and** `webpack*.config.ts` files
(the first pass missed the `.ts` ones, which is how `online-editor` and
`dev-deployment-dmn-form-webapp` were caught later, after the original 13 were already fixed):

- `packages/chrome-extension-pack-kogito-kie-editors/webpack.config.js`
- `packages/bpmn-vscode-extension/webpack.config.js`
- `packages/bpmn-editor-standalone/webpack.config.js`
- `packages/pmml-vscode-extension/webpack.config.js`
- `packages/kie-editors-dev-vscode-extension/webpack.config.js`
- `examples/micro-frontends-multiplying-architecture-base64png-editor-chrome-extension/webpack.config.js`
- `packages/dmn-editor-standalone/webpack.config.js`
- `packages/runtime-tools-process-dev-ui-webapp/webpack.config.js`
- `packages/extended-services-vscode-extension/webpack.config.js`
- `packages/monaco-editor/webpack.config.js`
- `packages/runtime-tools-management-console-webapp/webpack.config.js`
- `packages/dmn-vscode-extension/webpack.config.js`
- `packages/feel-input-component/showcase/webpack.config.js`
- `packages/online-editor/webpack.config.ts`
- `packages/dev-deployment-dmn-form-webapp/webpack.config.ts`

Verified every one of the 15 either builds clean (with dependencies built first via pnpm's
`<pkg>...` filter pattern, e.g. `pnpm --filter @kie-tools/bpmn-editor-standalone... run build:dev`)
or fails only on pre-existing, unrelated errors (implicit-any TS strictness in one showcase file;
one example's build gated off by an environment condition) — never the `instanceof Compilation`
crash. A full repo-wide sweep of all `webpack*.config.{js,ts}` files confirms no remaining
unpatched file directly imports `webpack`.

# KIE Tools :: Repository Manual

This document contains all information related to the `kie-tools` as a Git repository. It describes its directory
structure, concepts like "packages" and specifics on important tools like Maven, TypeScript, and Container images.

### Directory structure

| File                                                                                                                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |     |     |     |
| ------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | --- | --- | --- |
| `packages/` <br> `examples/`                                                                                                   | **Where the source code is**. There’s no distinction between these two dirs, except from the name and the convention as to where to put stuff. No dangling files are allowed inside these dirs, only other dirs containing packages. There is no package hierarchy. Both dirs have a flat structure.                                                                                                                                                                                                                      |     |     |     |
| `repo/` <br> `scripts/`                                                                                                        | Related to the monorepo itself. `repo` contains files describing the monorepo structure (I.e., packages DAG, for enabling partial clones with sparse checkout). `scripts` contains code to make the monorepo run smoothly. (E.g., `update-version-to` or `bootstrap`).                                                                                                                                                                                                                                                    |     |     |     |
| `docs/` <br> `gifs/`                                                                                                           | Files referenced externally in READMEs, for instance. Could be unified into a single directory only, probably with a better name.                                                                                                                                                                                                                                                                                                                                                                                         |     |     |     |
| `package.json` <br> `pnpm-workspace.yaml`                                                                                      | Together they define the monorepo structure and make some commands available at the root dir. The root `package.json` acts like the glue holding everything together. It declares dependencies that are necessary for the monorepo to operate. Packages inside `scripts` are part of these dependencies.                                                                                                                                                                                                                  |     |     |     |
| `.envrc` <br> `devbox.lock` <br> `devbox.json`                                                                                 | Direnv and Devbox configuration files. See these instructions to set it up. See [./../../NIX_DEV_ENV.md](./../../NIX_DEV_ENV.md)                                                                                                                                                                                                                                                                                                                                                                                          |     |     |     |
| `.ci/`                                                                                                                         | Jeknins configuration for Apache release jobs                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |     |     |     |
| `.github/`                                                                                                                     | GitHub configuration                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |     |     |     |
| `.asf.yaml`                                                                                                                    | Apache Software Foundation (ASF) YAML for configuring GitHub features, as no one has access to the Settings tab of repos under the Apache organization.                                                                                                                                                                                                                                                                                                                                                                   |     |     |     |
| `.vscode/` <br> `_intellij-project/`                                                                                           | IDE configuration                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |     |     |     |
| `.gitattributes` <br> `.gitignore` <br> `packages/*/.gitignore` <br> `examples/*/.gitignore`                                   | Git stuff. `.gitignore` files must either by at the root of the repo, or at the root of a package.                                                                                                                                                                                                                                                                                                                                                                                                                        |     |     |     |
| `.husky/` <br> `prettier.config.js` <br> `.prettierignore` <br> `packages/*/.prettierignore` <br> `examples/*/.prettierignore` | Code formatters configuration. <br> **Done**: Prettier defaults, package.json, XML <br> **To do**: Java, Go, Containerfiles, Python                                                                                                                                                                                                                                                                                                                                                                                       |     |     |     |
| `.npmrc` <br> `.syncpackrc.json` <br> `patches/` <br> `pnpm-lock.yaml`                                                         | Related to NPM dependencies. .npmrc addresses https://github.com/pnpm/pnpm/issues/6300 and prevents 3rd party package binaries inside `node_modules` to access packages they don’t declare as dependencies via extend-note-path=false. Syncpack makes sure all `package.json` files declare the same version of dependencies. `patches` contains manual patches we do to NPM dependencies on `node_modules/.pnpm` `pnpm-lock.yaml` keeps NPM dependencies pinned to a specific version to make the monorepo future-proof. |     |     |     |
| `.build-env-root`                                                                                                              | Marker file used to not go looking for `build-env` configurations outside of the monorepo. More details about `build-env` below.                                                                                                                                                                                                                                                                                                                                                                                          |     |     |     |

### Concepts

The monorepo is built on top of the `Package` abstraction, defined by `package.json` files. This is borrowed from NPM, and in fact, important for using `pnpm` as a script runner. A package is the smallest unit present in the monorepo. Configuration should always be done through environment variables, which can be read and consumed by virtually anything. All build configurations and parameters are defined as Environment Variables and accessed exclusively through `build-env`, a small CLI tool we built.

**Packages**

- The `package.json` file
  - `$schema` **(required)**
    - Always points to `<root>/repo/package.json.schema.json`
    - Allows for better IDE support for properties on this file.
  - `name` **(required)**
    - Type: string
    - Can be plain (my-package) or scoped (@my-scope/my-package). Usually separated by dashes.
    - See https://docs.npmjs.com/cli/v10/configuring-npm/package-json#name for more details.
  - `version` **(required)**
    - Type: string
    - In SemVer format.
    - Should never be edited manually. It is `0.0.0` on `main`.
    - To update it, run this command at the root:
    - pnpm update-version-to [new-version] [pnpm filter?]
  - `private` (optional)
    - Type: boolean
    - Whether this package should be published to NPM.
  - `scripts → build:dev` **(required)**
    - Type: string (`bash` and/or `cmd` commands)
    - Builds skipping everything. Used to build upstream.
  - `scripts → build:prod` **(required)**
    - Type: string (`bash` and/or `cmd` commands)
    - Builds targeting a release.
  - `scripts → install` (optional)
    - Type: string (`bash` and/or `cmd` commands)
    - Runs when the root bootstrap script installs dependencies.
  - `kieTools → requiredPreinstalledCliCommands` **(required)**
    - Type: string[]
    - List of commands necessary to build this package.
  - `dependencies` (optional)
    - Type: Map<string, string>
    - Lists the RUNTIME dependencies of this package.
    - Dependencies from inside the monorepo have version `”workspace:*”`. This is a `pnpm` feature.
    - Dependencies from outside the monorepo have fixed versions.
    - Dependencies do come transitively when users do `npm install @my-scope/my-package`.
  - `devDependencies` (optional)
    - Type: Map<string, string>
    - Lists the DEVELOPMENT / BUILD dependencies of this package.
    - Dependencies from inside the monorepo have version `”workspace:*”`. This is a `pnpm` feature.
    - Dependencies from outside the monorepo have fixed versions.
    - devDependencies don’t come transitively when users do `npm install @my-scope/my-package`.
  - Any other property accepted by NPM
    - Only relevant for NPM packages, of course.
    - See https://docs.npmjs.com/cli/v10/configuring-npm/package-json
- **`env/index.js`**
  - Use if you need to configure `build:dev` and `build:prod` scripts. (E.g., skipping tests or changing the port of an app)
  - Mapping of environment variables to JSON
  - We never read the environment variables directly. Always through the JSON defined by `env/index.js`
  - Scoped per-package. Can’t access other packages by mistake.
- **`dist*` directories**
  - Where build results usually end up.
- **…and whatever else the package needs!**
  - Other than the `package.`json and the correct place to put test reports in JUnit XML format, packages are free to structure themselves in any way they need.

**Environment variables**

- Everything is configurable through environment variables
  - Platform-agnostic
  - CI-friendly
- `build-env` is a utility script for easily maintaining the set of used environment variables by each package
  - `build-env myPackage.myEnvVar` will print the value mapped by `env/index.js`.
  - It goes up recursively on the directory structure looking for `env/index.js` files.
  - Once it finds one, or the `.build-env-root` marker file, it stops.
  - There’s no automatic merging. Combining `env/index.js` files is done manually. Composition over inheritance!

### Conventions

1. **0.0.0 is the development version at `main`**
   - SemVer-compatible.
   - Will absolutely never be resolved as “higher” than any published version.
1. **`package.json → version` is the source of truth for a package version**
   - It is always in SemVer format.
   - All language/tool-specific files must be derived from whatever version is defined on `package.json`. This ensures that the `update-version` script works as intended, as it simply updates the `package.json` version and runs a `bootstrap`.
   - Defining an `install` script that reads from `package.json` is the best way to configure language/tool-specific files that contain a version.
   - Maven packages, for example, rely on the Maven recommended CI-friendly setup. This is also compatible with the upcoming Maven 4.
   - If your technology needs a version in another format different from SemVer, you’ll need to derive it from the SemVer string present on `package.json → version`
1. **`build:dev` and `build:prod` scripts must produce everything that dependent packages might need**
   - The difference between them is the amount of expensive checks/optimizations they do, but they must be able to produce a full version of the package equally.
   - An example is running linters or triggering compiler optimizations. In the very beginning, `build:dev` used to be called `build:fast`, so that kind of gives an idea of why there are two scripts.
   - Build:prod should be understood as a “full build”, where everything you possibly want to do when building a package is done.
   - It’s not uncommon for build:dev and build:prod scripts to be identical.
1. **`build:dev` and `build:prod` must be “thread-safe”**
   - Building packages in parallel is one of the things that makes the monorepo fast. Always assume other packages are building at the same time as your package.
   - If you follow the rules above, you’ll hardly hit a problem, unless you’re using a tool/command that can’t be run in parallel. In this case, finding an alternative way to achieve the results you want is going to be necessary, as we can’t assume a package will be built in isolation.
   - As of April 2024, the monorepo CI runs on the free tier of GitHub Actions, where machines have 2 available cores. Due to memory constraints, we run a sequential build. Locally, however, everyone has multiple cores available, and usually a lot of memory, so most people do `pnpm -r build:dev` locally after bootstrapping.
1. **Environment variables are exclusively read through `builld-env`, never directly.**
   - This ensures encapsulation and good reports during `pnpm bootstrap`.
1. **There are no nested packages. Each package has one, and one only, `package.json` file.**
   - The package structure does not allow nested packages.
   - The monorepo has a flat structure architecture.
1. **Packages can’t reference other monorepo files outside of their directories using `..` on paths**
1. **Packages should only have knowledge about its own directory structure, never upwards.**
   - Use `node_modules`, don’t ever do `../my-other-package/some/nested/dir`. Do `./node_modules/my-other-package/some/nested/dir`, or `./node_modules/@my-scope/my-package/some/nested/dir`.
   - This ensures packages only have access to what they declare as dependencies on their `package.json` files.
1. **Packages can’t mutate the monorepo’s file system outside of its directory structure**
   - The `node_modules` directory has a lot of symbolic links to directories that are shared between all packages. So changing it can potentially pollute the global scope and result in unpredictable behavior.
   - As a general rule, see the `node_modules` dir as read-only.
1. **Generated code is either ignored or versioned on Git.**
   - If files are produced either during `bootstrap` or `build`, they must be either ignored by Git, or committed. Running `pnpm bootstrap` on a clean clone must never change any versioned file.
1. **Test results must be reported in JUnit XML format**
   - JUnit XMLs became the de-facto standard for test result reporting.
   - Having every test case reported using this format means we’ll always get good introspection and compatibility with test management tools, like Buildkite or TestRail.

### General good practices

1. Define the `package.json → kieTools → requiredPreinstalledCliCommands` property with what your package needs to be built.
   - This won’t fail any of the steps, but there’s a nice report on the `bootstrap` script that helps users understand their environment.
1. Don’t download stuff on scripts other than `install`
   - Ideally, any external resource you might need for the build should be fetched during the `bootstrap` script. Packages can hook into the root-level `bootstrap` script by defining an `install` script on their package.json file.
   - This allows the monorepo build to fail faster when there’s a problem fetching an external resource.
1. Make sure your package is buildable and testable on Windows, macOS, and Linux
   - macOS x86 is being discontinued, but GitHub Actions still uses it, and unfortunately we can’t use the new Arm-based runners, as they’re not compatible with Docker yet.
1. Follow the environment variables naming convention.
   - As of April 2024, the convention is `PACKAGE_NAME__configurationName`; but
   - In the future, it will be better to change this to `KIE_TOOLS__packageName__configurationName`.
1. Use the same name for the package directory and `package.json → name`
   - In practice, the name of the directory doesn’t have meaning, but helps us navigate the repo.
1. Don’t use mutable versions anywhere
   - “Latest”, or “dev”, or “SNAPSHOT” versions change with time, and can make the build break unexpectedly.
   - More than that, when building an older tag or commit, pointing to “latest” will likely not work.
   - Beware of unspecified versions too, as they often default to “latest”.
1. Configure dependencies to have the same single version available to all packages
   - Pretty much like https://opensource.google/documentation/reference/thirdparty/oneversion
   - Try to keep the version string in a single place, or at least enforce it to be exactly the same as other declarations, like we do with Syncpack in the CI for NPM dependencies.
1. Keep packages small
   - Smaller packages can be installed, built, tested, and packaged much faster, and contribute positively to parallelization when running tasks
1. Make sure `package.json → build:dev` and `package.json → start` are friendly to debugging.
   - Check if source maps are available.
   - Check if obfuscation is turned off.

asd

-

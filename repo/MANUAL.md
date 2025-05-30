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

# KIE Tools :: Manual

This document contains all information related to the `kie-tools` as a Git repository. It describes its directory
structure, concepts like "packages" and specifics on important tools like Maven, TypeScript, and Container images.

---

### Directory structure

| File                                                                                                                           | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| ------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `packages/` <br> `examples/`                                                                                                   | **Where the source code is**. There’s no distinction between these two dirs, except from the name and the convention as to where to put stuff. No dangling files are allowed inside these dirs, only other dirs containing packages. There is no package hierarchy. Both dirs have a flat structure.                                                                                                                                                                                                                      |
| `repo/` <br> `scripts/`                                                                                                        | Related to the monorepo itself. `repo` contains files describing the monorepo structure (I.e., packages DAG, for enabling partial clones with sparse checkout). `scripts` contains code to make the monorepo run smoothly. (E.g., `update-version-to` or `bootstrap`).                                                                                                                                                                                                                                                    |
| `docs/` <br> `gifs/`                                                                                                           | Files referenced externally in READMEs, for instance. Could be unified into a single directory only, probably with a better name.                                                                                                                                                                                                                                                                                                                                                                                         |
| `package.json` <br> `pnpm-workspace.yaml`                                                                                      | Together they define the monorepo structure and make some commands available at the root dir. The root `package.json` acts like the glue holding everything together. It declares dependencies that are necessary for the monorepo to operate. Packages inside `scripts` are part of these dependencies.                                                                                                                                                                                                                  |
| `.envrc` <br> `devbox.lock` <br> `devbox.json`                                                                                 | Direnv and Devbox configuration files. See these instructions to set it up. See [NIX_DEV_ENV.md](./NIX_DEV_ENV.md)                                                                                                                                                                                                                                                                                                                                                                                                        |
| `.ci/`                                                                                                                         | Jeknins configuration for Apache release jobs                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| `.github/`                                                                                                                     | GitHub configuration                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| `.asf.yaml`                                                                                                                    | Apache Software Foundation (ASF) YAML for configuring GitHub features, as no one has access to the Settings tab of repos under the Apache organization.                                                                                                                                                                                                                                                                                                                                                                   |
| `.vscode/` <br> `_intellij-project/`                                                                                           | IDE configuration                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| `.gitattributes` <br> `.gitignore` <br> `packages/*/.gitignore` <br> `examples/*/.gitignore`                                   | Git stuff. `.gitignore` files must either by at the root of the repo, or at the root of a package.                                                                                                                                                                                                                                                                                                                                                                                                                        |
| `.husky/` <br> `prettier.config.js` <br> `.prettierignore` <br> `packages/*/.prettierignore` <br> `examples/*/.prettierignore` | Code formatters configuration. <br> **Done**: Prettier defaults, package.json, XML <br> **To do**: Java, Go, Containerfiles, Python                                                                                                                                                                                                                                                                                                                                                                                       |
| `.npmrc` <br> `.syncpackrc.json` <br> `patches/` <br> `pnpm-lock.yaml`                                                         | Related to NPM dependencies. .npmrc addresses https://github.com/pnpm/pnpm/issues/6300 and prevents 3rd party package binaries inside `node_modules` to access packages they don’t declare as dependencies via extend-note-path=false. Syncpack makes sure all `package.json` files declare the same version of dependencies. `patches` contains manual patches we do to NPM dependencies on `node_modules/.pnpm` `pnpm-lock.yaml` keeps NPM dependencies pinned to a specific version to make the monorepo future-proof. |
| `.build-env-root`                                                                                                              | Marker file used to not go looking for `build-env` configurations outside of the monorepo. More details about `build-env` below.                                                                                                                                                                                                                                                                                                                                                                                          |

---

### Concepts

The monorepo is built on top of the `Package` abstraction, defined by `package.json` files. This is borrowed from NPM, and in fact, important for using `pnpm` as a script runner. A package is the smallest unit present in the monorepo. Configuration should always be done through environment variables, which can be read and consumed by virtually anything. All build configurations and parameters are defined as Environment Variables and accessed exclusively through `build-env`, a small CLI tool we built.

**Packages**

- `package.json`
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
    - Dependencies from inside the monorepo have version `"workspace:*"`. This is a `pnpm` feature.
    - Dependencies from outside the monorepo have fixed versions.
    - Dependencies do come transitively when users do `npm install @my-scope/my-package`.
  - `devDependencies` (optional)
    - Type: Map<string, string>
    - Lists the DEVELOPMENT / BUILD dependencies of this package.
    - Dependencies from inside the monorepo have version `"workspace:*"`. This is a `pnpm` feature.
    - Dependencies from outside the monorepo have fixed versions.
    - devDependencies don’t come transitively when users do `npm install @my-scope/my-package`.
  - Any other property accepted by NPM
    - Only relevant for NPM packages, of course.
    - See https://docs.npmjs.com/cli/v10/configuring-npm/package-json
- `env/index.js`
  - Use if you need to configure `build:dev` and `build:prod` scripts. (E.g., skipping tests or changing the port of an app)
  - Mapping of environment variables to JSON
  - We never read the environment variables directly. Always through the JSON defined by `env/index.js`
  - Scoped per-package. Can’t access other packages by mistake.
- `dist*` directories
  - Where build results usually end up.
- …and whatever else the package needs
  - Other than the `package.json` and the correct place to put test reports in JUnit XML format, packages are free to structure themselves in any way they need.
  - Refer to [Conventions](#Conventions) to know more about how packages are structured.

**Environment variables**

- Everything is configurable through environment variables
  - Platform-agnostic
  - CI-friendly
- `build-env` is a utility script for easily maintaining the set of used environment variables by each package
  - `build-env myPackage.myEnvVar` will print the value mapped by `env/index.js`.
  - It goes up recursively on the directory structure looking for `env/index.js` files.
  - Once it finds one, or the `.build-env-root` marker file, it stops.
  - There’s no automatic merging. Combining `env/index.js` files is done manually. Composition over inheritance!

---

### Conventions

1. **`0.0.0` is the development version at `main`**
   - SemVer-compatible.
   - Will absolutely never be resolved as "higher" than any published version.
   - Other branches will follow their stream name, with `999` replacing the variable part. E.g., `10.0.x` branch has version `10.0.999`.
1. **`package.json → version` is the source of truth for a package version**
   - It is always in SemVer format.
   - All language/tool-specific files must be derived from whatever version is defined on `package.json`. This ensures that the `update-version` script works as intended, as it simply updates the `package.json` version and runs a `bootstrap`.
   - Defining an `install` script that reads from `package.json` is the best way to configure language/tool-specific files that contain a version.
   - Maven packages, for example, rely on the Maven recommended CI-friendly setup. This is also compatible with the upcoming Maven 4.
   - If your technology needs a version in another format different from SemVer, you’ll need to derive it from the SemVer string present on `package.json → version`
1. **`build:dev` and `build:prod` scripts must produce everything that dependent packages might need**
   - The difference between them is the amount of expensive checks/optimizations they do, but they must be able to produce a full version of the package equally.
   - An example is running linters or triggering compiler optimizations. In the very beginning, `build:dev` used to be called `build:fast`, so that kind of gives an idea of why there are two scripts.
   - Build:prod should be understood as a "full build", where everything you possibly want to do when building a package is done.
   - It’s not uncommon for build:dev and build:prod scripts to be identical.
1. **`build:dev` and `build:prod` must be "thread-safe"**
   - Building packages in parallel is one of the things that makes the monorepo fast. Always assume other packages are building at the same time as your package.
   - If you follow the rules above, you’ll hardly hit a problem, unless you’re using a tool/command that can’t be run in parallel. In this case, finding an alternative way to achieve the results you want is going to be necessary, as we can’t assume a package will be built in isolation.
   - As of April 2024, the monorepo CI runs on the free tier of GitHub Actions, where machines have 2 available cores. Due to memory constraints, we run a sequential build. Locally, however, everyone has multiple cores available, and usually a lot of memory, so most people do `pnpm -r build:dev` locally after bootstrapping.
1. **Environment variables are exclusively read through `build-env`, never directly.**
   - This ensures encapsulation and good reports during `pnpm bootstrap`.
1. **There are no nested packages. Each package has one, and one only, `package.json` file.**
   - The package structure does not allow nested packages.
   - The monorepo has a flat structure architecture.
1. **Packages can’t reference other monorepo files outside of their directories using `..` on paths**
   - Packages should only have knowledge about its own directory structure, never upwards.
   - Use `node_modules`, don’t ever do `../my-other-package/some/nested/dir`. Do `./node_modules/my-other-package/some/nested/dir`, or `./node_modules/@my-scope/my-package/some/nested/dir`.
   - This ensures packages only have access to what they declare as dependencies on their `package.json` files.
1. **Packages can’t mutate the monorepo’s file system outside of its directory structure**
   - The `node_modules` directory has a lot of symbolic links to directories that are shared between all packages. So changing it can potentially pollute the global scope and result in unpredictable behavior.
   - As a general rule, see the `node_modules` dir as read-only.
1. **Generated code is either ignored or versioned on Git.**
   - If files are produced either during `bootstrap`, they must be either ignored by Git, or committed. Running `pnpm bootstrap` on a clean clone must never change any versioned file. If files are generated during `build`, they should not be versio in Git.
1. **Test results must be reported in JUnit XML format**
   - JUnit XMLs became the de-facto standard for test result reporting.
   - Having every test case reported using this format means we’ll always get good introspection and compatibility with test management tools, like Buildkite or TestRail.

---

### General good practices

1. Define the `package.json → kieTools → requiredPreinstalledCliCommands` property with what your package needs to be built.
   - This won’t fail any of the steps, but there’s a nice report on the `bootstrap` script that helps users understand their environment.
1. Prefer downloading stuff on scripts other than `install`
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
   - `latest`, `dev`, or `-SNAPSHOT` versions change with time, and can make the build break unexpectedly.
   - More than that, when building an older tag or commit, pointing to `latest` will likely not work.
   - Beware of unspecified versions too, as they often default to `latest`.
1. Configure dependencies to have the same single version available to all packages
   - Pretty much like https://opensource.google/documentation/reference/thirdparty/oneversion
   - Try to keep the version string in a single place, or at least enforce it to be exactly the same as other declarations, like we do with Syncpack in the CI for NPM dependencies.
1. Keep packages small
   - Smaller packages can be installed, built, tested, and packaged much faster, and contribute positively to parallelization when running tasks
1. Make sure `package.json → build:dev` and `package.json → start` are friendly to debugging.
   - Check if source maps are available.
   - Check if obfuscation is turned off.

---

### Scripts

A few scripts are available for general purpose usage on `kie-tools`. They're built prior to everything else and are available in the top-level `node_modules` dir, meaning any script can access them. They don't need to be declared as dependencies too since they're already a dependency of the root `package.json`. All of them can be invoked with `pnpm [script-name]`.

- Used for everyday development
  - [bootstrap](../scripts/bootstrap/README.md): Runs when we execute `pnpm bootstrap`.
  - [build-env](../scripts/build-env/README.md): Environment variables management.
  - [run-script-if](../scripts/run-script-if/README.md): Shell-friendly conditional command execution
- Used when cloning `kie-tools`
  - [sparse-checkout](../scripts/sparse-checkout/README.md): Partially clone `kie-tools` and work on a subset of its packages.
- Mostly used by our automations
  - [check-junit-report-results](../scripts/check-junit-report-results/README.md): Checks JUnit XML files to see if tests failed. Used on our CI.
  - [update-kogito-version](../scripts/update-kogito-version/README.md): Updates the version of Kogito Maven dependencies. (E.g., `999-20250511-SNAPSHOT`, `999-SNAPSHOT` or `10.0.0`)
  - [update-stream-name](../scripts/update-stream-name/README.md): Updates this repo's stream name. (E.g., `main` or `10.0.x`)
  - [update-version](../scripts/update-version/README.md): Updates this repo's version (E.g., `0.0.0` or `10.0.999`)

---

### Specifics # Maven

To comply with the repository's conventions and concepts, like Packages, Maven usage has been mapped and documented here for reference.

#### @kie-tools/maven-base

Foundational package for other Maven-based packages to base themselves on.

Used for writing `.mvn/maven.config` with `-Drevision`, `-Dmaven.repo.local.tail` and other necessary properties.
And for centralized `<dependencyManagement>` and other necessary standard configurations

All Maven-based packages should declare it as a `dependency` on their `package.json` files and its `pom.xml` as parent.

```xml
<parent>
  <groupId>org.kie</groupId>
  <artifactId>kie-tools-maven-base</artifactId>
  <version>${revision}</version>
  <relativePath>./node_modules/@kie-tools/maven-base/pom.xml</relativePath>
</parent>
```

#### flatten-maven-plugin

Due to CI friendly versions as well. Present on `maven-base` already, no need to configure it individually.
https://www.mojohaus.org/flatten-maven-plugin/

#### package.json → scripts → install:

Has to be at least `node install.js`, using `@kie-tools/maven-base` to set up properties like `-Drevision`, `-Dmaven.repo.local.tail`.

#### package.json → scripts → build:dev:

Has to be at least `mvn clean install -DskipTests=$(build-env tests.run --not) -Dmaven.test.failure.ignore=$(build-env tests.ignoreFailures)`
Needs to install so that other Maven repos can reference it.

#### package.json → scripts → build:prod:

Has to be at least `mvn clean deploy -DdeployAtEnd -Dmaven.deploy.skip=$(build-env maven.deploy.skip) -DskipTests=$(build-env tests.run --not) -Dmaven.test.failure.ignore=$(build-env tests.ignoreFailures)`
This is important for the Release jobs to correctly deploy it. Of course, deploy is skipped by default, so it acts like `mvn clean install ...`.

#### package.json → dependencies:

If your Maven package depends on other Maven packages, you need to declare those in the `dependencies` section, not on `devDependencies`.
This ensures the release scripts know what other Maven packages need to be published alongside yours, and makes it easier to configure `-Dmaven.repo.local.tail` via `buildTailFromPackageJsonDependencies()`

### Development

This section contains relevant topics about developing packages hosted on KIE Tools.

#### Setting up your environment

- Nix.dev, Devbox, and `direnv` _**(recommended!)**_
  - See [NIX_DEV_ENV.md](./NIX_DEV_ENV.md)
- Traditional
  - See [the top-level README.md](../../README.md#step-0-install-the-necessary-tools)

#### Recommended IDEs

- IntelliJ IDEA
  - See [the top-level IntelliJ IDEA project](./../../_intellij-project/README.md)
- VS Code
  - // TODO

#### Running

Usually, packages that can be developed individually (E.g., apps of any kind, or libraries with dedicated development apps) define a script called `start` on their `package.json`, for example, developing the DMN Editor can be done in these steps:

1. `pnpm bootstrap -F dmn-editor...`
1. `pnpm -F dmn-editor^... build:dev`
1. `pnpm -F dmn-editor start`

#### Changing libraries

Packages that don’t contain an app (so-called libraries) can be developed in conjunction with apps that can be run. When developing libraries, they need to be rebuilt so that apps can be re-run with the changed version of the libraries you’re developing, and you can see the changes in effect. To do that, you can keep track of the libraries you’re building, and simply rebuild them with:

`pnpm -F my-library build:dev`

If you’re changing multiple libraries and don’t want to rebuild one by one, you can do:

`pnpm -F my-app^... build:dev`

This is much less error-prone, as you won’t forget to build one of the libraries that you might’ve changed.

If your app doesn’t support live-reloading, you’ll need to re-run it too. If it does, rebuilding the libraries is enough.

#### Multi-package live reloading

Some webpack-based apps can be run with a special --env live parameter, so that rebuilding libraries is not necessary. This is great for developing the Boxed Expression Editor using the `dmn-editor` Storybook,

`pnpm -F dmn-editor start --env live`

#### (A) Adding a new package

Simply add a directory with a `package.json` inside it, give it the properties you need and run `pnpm bootstrap` at the root dir. It is going to be part of the build now.

#### (D) Removing a package

If this package is public (I.e., doesn’t define "private": "true" on its `package.json`), it means it is published to the NPM registry, and there might be people depending on it. We should always publish a last version with an updated README telling people that the package was deleted, and what they should do about it.

Ideally, we should deprecate packages before completely removing them.

Private packages are never consumed directly, and can be removed without any additional process. The build will fail if there are references to the removed package that weren’t removed too.

#### (M) Renaming a package

Should be treated as removing and adding it. Don’t forget to rename the package directory AND `package.json  → name`.

If this package is public, consider leaving behind a package with the old name containing deprecation notices, and pointing to the new name.

Adding a dependency to an existing package

Change `package.json` manually and run `pnpm bootstrap` at the root directory.

#### Formatting the code

In practice, you don’t need to worry about this, as a pre-commit hook will format the changed files when you do `git commit`.

The CI will also check if your code is properly formatted.

#### Skipping tests

- Conventionally, `build:dev` doesn’t run tests.
- If you really need to do `build:prod` locally, you can use the following environment variables to skip tests, as they usually take a while to run:
  - `export KIE_TOOLS_BUILD__runTests=false` or
  - `export KIE_TOOLS_BUILD__runEndToEndTests=false`
    - Tests won’t run at all.
  - `export KIE_TOOLS_BUILD__ignoreTestFailures=true` or
  - `export KIE_TOOLS_BUILD__ignoreEndToEndTestFailures=true`
    - Tests will run, but a failing test won’t fail the build.

#### Understanding a package’s relationship with other packages

- `pnpm list --depth Infinity --only-projects [pnpm filter?]`
  - Will display the relationship the filtered packages have with other packages inside the repo.
  - This is very similar to `mvn dependency:tree`.

---

### Dependency management & security

It’s really important to understand that the software toolchain has flaws, and we’re all exposed to downloading malicious code from time to time.

There are, however, some things we can do to prevent ourselves from being in a dangerous situation.

- Dependency management tools, like Maven, `pnpm`, or `pip`, have mechanisms to help us be safer.
  - Lock files
  - Commit SHA references
  - Fixed versions
  - Checksums
- When adding a dependency to the monorepo, be thoughtful about who’s behind that dependency and what methods you’re using to download it.
- Prefer projects that are very widely known, active and have a healthy community around it.
- Think about the scope of this dependency.
  - Is this going to be used for development; or
  - Will users need to install it to be able to run our packages?
- Licenses
  - See https://www.apache.org/licenses/

---

Feel free to proposed changes to this manual by sending a PR directly to this file.

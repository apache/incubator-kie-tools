## @kie-tools-scripts/build-env

Helps packages inside a monorepo to define environment variables and consume them easily.

This package allows packages to consume environment variables in a cross-platform way, while maintaining a documented list of variables that are defined.

Environment variables are not directly consumed, but rather used to create properties with a more comprehensive structure.

Env definition files are plain JavaScript, allowing for better flexibility and use through the CLI or direct imports on other JavaScript files, such as Webpack configuration files.

### Usage

- `build-env --print-vars`

  - Prints the env variables in JSON format.

- `build-env --print-env`

  - Prints the env properties in JSON format.

- `build-env {dot.separated.property}`
  - Returns the value of a property. See available properties with `build-env --print-env`.
    - e.g. `build-env root.version` prints `0.0.0`.
  - It's possible to negate boolean values with the `--not flag.`
    - e.g. If `build-env build.runTests` prints `true`, then e.g. `build-env build.runTests --not` prints `false`

### Configuration

- `build-env` will recursively scan for an `env` directory with an `index.js` file inside, starting from the current directory until `env/index.js` (or a file called `.build-env-root`) is found.

- `env/index.js` files should export `vars` and `env`. See the example below.

- To compose an env with another one, use the `composeEnv` function.

### Example

Given the following package structure in a monorepo:

- packages/a/env/index.js

```js
const { varsWithName, getOrDefault } = require("@kie-tools-scripts/build-env");

module.exports = {
  vars: varsWithName({
    MY_VAR: {
      default: `foo`,
      description: "My env var",
    },
  }),
  get env() {
    return {
      myProperty: getOrDefault(this.vars.MY_VAR),
    };
  },
};
```

- packages/b/env/index.js

```js
const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("a/env")], {
  vars: varsWithName({
    MY_OTHER_VAR: {
      default: `bar`,
      description: "My other env var",
    },
  }),
  get env() {
    return {
      myOtherProperty: getOrDefault(this.vars.MY_OTHER_VAR),
    };
  },
});
```

Executing build-env will give you these results:

```bash
# With default values:
$ cd ~/[my-repo]/packages/a
$ build-env myProperty
foo
$ build-env myOtherProperty
[build-env] Env property 'myOtherProperty' not found.
[build-env] See all env properties with 'build-env --print-env'

$ cd ~/[my-repo]/packages/b
$ build-env myProperty
foo
$ build-env myOtherProperty
bar

# With custom value:
$ export MY_VAR='fooz'
$ cd ~/[my-repo]/packages/a
$ build-env myProperty
fooz
$ cd ~/[my-repo]/packages/b
$ build-env myProperty
fooz
```

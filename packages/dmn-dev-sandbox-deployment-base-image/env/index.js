const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools/build-env");

module.exports = composeEnv([require("@kie-tools/build-env/env")], {
  vars: varsWithName({}),
  get env() {
    return {};
  },
});

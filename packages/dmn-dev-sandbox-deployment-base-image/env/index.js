const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools/build-env");

const buildEnv = require("@kie-tools/build-env/env");

module.exports = composeEnv([buildEnv], {
  vars: varsWithName({}),
  get env() {
    return {};
  },
});

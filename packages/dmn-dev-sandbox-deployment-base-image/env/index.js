const { envVars, getOrDefault, compositeEnv } = require("@kie-tools/build-env");

const buildEnv = require("@kie-tools/build-env/env");

module.exports = compositeEnv([buildEnv], {
  vars: envVars({}),
  get env() {
    return {};
  },
});

const { envVars, getOrDefault, compositeEnv } = require("@kie-tools/build-env");

const buildEnv = require("@kie-tools/build-env/env");

module.exports = compositeEnv([buildEnv], {
  vars: envVars({
    DMN_DEV_SANDBOX__gtmId: {
      default: undefined,
      description: "",
    },
  }),
  get env() {
    return {
      dmnDevSandboxFormWebapp: {
        gtmId: getOrDefault(this.vars.DMN_DEV_SANDBOX__gtmId),
        dev: {
          port: 9008,
        },
      },
    };
  },
});

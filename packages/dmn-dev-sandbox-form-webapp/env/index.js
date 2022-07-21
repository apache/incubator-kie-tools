const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools/build-env");

const buildEnv = require("@kie-tools/build-env/env");

module.exports = composeEnv([buildEnv], {
  vars: varsWithName({
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

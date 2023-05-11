const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    KIE_SANDBOX_DISTRIBUITION__kieSandboxImageTag: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUITION__kieSandboxPort: {
      default: "9090",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUITION__extendedServicesImageTag: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUITION__extendedServicesPort: {
      default: "21345",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUITION__gitCorsProxyImageTag: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUITION__gitCorsProxyPort: {
      default: "7081",
      description: "",
    },
  }),
  get env() {
    return {
      kieSandboxDistribution: {
        kieSandbox: {
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUITION__kieSandboxImageTag),
          port: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUITION__kieSandboxPort),
        },
        extendedServices: {
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUITION__extendedServicesImageTag),
          port: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUITION__extendedServicesPort),
        },
        gitCorsProxy: {
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUITION__gitCorsProxyImageTag),
          port: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUITION__gitCorsProxyPort),
        },
      },
    };
  },
});

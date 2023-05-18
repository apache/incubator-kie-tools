const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageRegistry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageAccount: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageName: {
      default: "kie-sandbox-image",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageTag: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxPort: {
      default: "9090",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName: {
      default: "kie-sandbox-extended-services-image",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesPort: {
      default: "21345",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageRegistry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageAccount: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageName: {
      default: "git-cors-proxy-image",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageTag: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__gitCorsProxyPort: {
      default: "7081",
      description: "",
    },
  }),
  get env() {
    return {
      kieSandboxDistribution: {
        kieSandbox: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxImageTag),
          port: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxPort),
        },
        extendedServices: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag),
          port: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesPort),
        },
        gitCorsProxy: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__gitCorsProxyImageTag),
          port: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__gitCorsProxyPort),
        },
      },
    };
  },
});

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

const buildEnv = require("@kie-tools/root-env/env");
const extendedServicesImageEnv = require("@kie-tools/kie-sandbox-extended-services-image/env");
const corsProxyImageEnv = require("@kie-tools/cors-proxy-image/env");
const kieSandboxImageEnv = require("@kie-tools/kie-sandbox-image/env");

module.exports = composeEnv([buildEnv, extendedServicesImageEnv, corsProxyImageEnv, kieSandboxImageEnv], {
  vars: varsWithName({
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageRegistry: {
      default: kieSandboxImageEnv.env.kieSandbox.image.registry,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageAccount: {
      default: kieSandboxImageEnv.env.kieSandbox.image.account,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageName: {
      default: kieSandboxImageEnv.env.kieSandbox.image.name,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxImageTag: {
      default: kieSandboxImageEnv.env.kieSandbox.image.buildTags.split(" ")[0],
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxContainerPort: {
      default: "8080",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__kieSandboxExposedPort: {
      default: "9090",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry: {
      default: extendedServicesImageEnv.env.extendedServicesImage.registry,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount: {
      default: extendedServicesImageEnv.env.extendedServicesImage.account,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName: {
      default: extendedServicesImageEnv.env.extendedServicesImage.name,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag: {
      default: extendedServicesImageEnv.env.extendedServicesImage.buildTags.split(" ")[0],
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesContainerPort: {
      default: "21345",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__extendedServicesExposedPort: {
      default: "21345",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageRegistry: {
      default: corsProxyImageEnv.env.corsProxyImage.image.registry,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageAccount: {
      default: corsProxyImageEnv.env.corsProxyImage.image.account,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageName: {
      default: corsProxyImageEnv.env.corsProxyImage.image.name,
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyImageTag: {
      default: corsProxyImageEnv.env.corsProxyImage.image.buildTags.split(" ")[0],
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyContainerPort: {
      default: "8080",
      description: "",
    },
    KIE_SANDBOX_DISTRIBUTION__corsProxyExposedPort: {
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
          containerPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxContainerPort),
          exposedPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__kieSandboxExposedPort),
        },
        extendedServices: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesImageTag),
          containerPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesContainerPort),
          exposedPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__extendedServicesExposedPort),
        },
        corsProxy: {
          imageRegistry: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageRegistry),
          imageAccount: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageAccount),
          imageName: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageName),
          imageTag: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyImageTag),
          containerPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyContainerPort),
          exposedPort: getOrDefault(this.vars.KIE_SANDBOX_DISTRIBUTION__corsProxyExposedPort),
        },
      },
    };
  },
});

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__registry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__account: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__name: {
      default: "kie_sandbox_extended_service",
      description: "",
    },
    KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__tag: {
      default: require("../package.json").version,
      description: "",
    },
  }),
  get env() {
    return {
      kieExtendedServicesHelmChart: {
        registry: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__registry),
        account: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__account),
        name: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__name),
        tag: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES_HELM_CHART__tag),
      },
    };
  },
});

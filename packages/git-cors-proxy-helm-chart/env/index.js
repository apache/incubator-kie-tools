const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    GIT_CORS_PROXY_HELM_CHART__registry: {
      default: "quay.io",
      description: "",
    },
    GIT_CORS_PROXY_HELM_CHART__account: {
      default: "kie-tools",
      description: "",
    },
    GIT_CORS_PROXY_HELM_CHART__name: {
      default: "git_cors_proxy",
      description: "",
    },
    GIT_CORS_PROXY_HELM_CHART__tag: {
      default: require("../package.json").version,
      description: "",
    },
  }),
  get env() {
    return {
      gitCorsProxyHelmChart: {
        registry: getOrDefault(this.vars.GIT_CORS_PROXY_HELM_CHART__registry),
        account: getOrDefault(this.vars.GIT_CORS_PROXY_HELM_CHART__account),
        name: getOrDefault(this.vars.GIT_CORS_PROXY_HELM_CHART__name),
        tag: getOrDefault(this.vars.GIT_CORS_PROXY_HELM_CHART__tag),
      },
    };
  },
});

const buildEnv = require("./env");
const yaml = require("js-yaml");
const fs = require("fs");

const file = "src/Chart.yaml";
const doc = yaml.load(fs.readFileSync(file, "utf8"));
doc.version = buildEnv.env.kieSandboxHelmChart.tag;
if (doc.dependencies != null) {
  for (let dependency of doc.dependencies) {
    if (
      dependency.name == "git-cors-proxy-helm-chart" ||
      dependency.name == "kie-sandbox-extended-services-helm-chart"
    ) {
      dependency.version = buildEnv.env.kieSandboxHelmChart.tag;
    }
  }
}
fs.writeFileSync(file, yaml.dump(doc), "utf8");

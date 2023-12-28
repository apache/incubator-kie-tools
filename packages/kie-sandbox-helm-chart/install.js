const buildEnv = require("./env");
const yaml = require("js-yaml");
const fs = require("fs");

// Set version for the Chart (and its dependencies) and Subcharts
const files = [
  "src/Chart.yaml",
  "src/charts/extended_services/Chart.yaml",
  "src/charts/cors_proxy/Chart.yaml",
  "src/charts/kie_sandbox/Chart.yaml",
];
files.forEach((file) => {
  const doc = yaml.load(fs.readFileSync(file, "utf8"));
  doc.version = buildEnv.env.kieSandboxHelmChart.tag;
  doc.appVersion = buildEnv.env.kieSandboxHelmChart.tag;
  if (doc.dependencies) {
    doc.dependencies = doc.dependencies.map((dep) => {
      if (["extended_services", "cors_proxy", "kie_sandbox"].includes(dep.name)) {
        return { ...dep, version: buildEnv.env.kieSandboxHelmChart.tag };
      }
      return dep;
    });
  }
  console.log(doc);
  fs.writeFileSync(file, yaml.dump(doc), "utf8");
});

// Update Values table on README
const readme = fs.readFileSync("./README.md").toString();
const chartReadme = fs.readFileSync("./src/README.md").toString();
const chartReadmeSections = chartReadme.split("## Values");
const readmeSections = readme.split("<!-- CHART_VALUES_README -->");
readmeSections[1] = chartReadmeSections[1];
const newContent = readmeSections.join("<!-- CHART_VALUES_README -->");
fs.writeFileSync("./README.md", newContent);

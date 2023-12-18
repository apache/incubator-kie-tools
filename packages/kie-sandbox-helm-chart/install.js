const buildEnv = require("./env");
const yaml = require("js-yaml");
const fs = require("fs");
const prettier = require("prettier");

const file = "src/Chart.yaml";
const doc = yaml.load(fs.readFileSync(file, "utf8"));
doc.version = buildEnv.env.kieSandboxHelmChart.tag;
fs.writeFileSync(file, yaml.dump(doc), "utf8");

const readme = fs.readFileSync("./README.md").toString();
const chartReadme = fs.readFileSync("./src/README.md").toString();
const chartReadmeSections = chartReadme.split("## Values");
const readmeSections = readme.split("<!-- CHART_VALUES_README -->");
readmeSections[1] = chartReadmeSections[1];

const newContent = prettier.format(readmeSections.join("<!-- CHART_VALUES_README -->"), {
  parser: "markdown",
  printWidth: 120,
});
fs.writeFileSync("./README.md", newContent);

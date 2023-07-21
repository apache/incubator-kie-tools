const buildEnv = require("./env");
const yaml = require("js-yaml");
const fs = require("fs");

try {
  const file = "src/Chart.yaml";
  const doc = yaml.load(fs.readFileSync(file, "utf8"));
  doc.version = buildEnv.env.gitCorsProxyHelmChart.tag;
  fs.writeFileSync(file, yaml.dump(doc), "utf8");
} catch (e) {
  console.log(e);
}

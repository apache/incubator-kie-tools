const { execSync } = require("child_process");
const path = require("path");
const fs = require("fs");

// Install helm-docs if not installed
console.log("[kie-sandbox-helm-chart scripts/update-readmes.js] Installing helm-docs...");
if (process.platform === "win32") {
  execSync("where /q helm-docs || go install github.com/norwoodj/helm-docs/cmd/helm-docs@v1.13.1");
} else {
  execSync("which helm-docs || go install github.com/norwoodj/helm-docs/cmd/helm-docs@v1.13.1");
}

const packageRootPath = path.join(__dirname, "..");
const srcPath = path.join(packageRootPath, "./src");

console.log(srcPath);

console.log("[kie-sandbox-helm-chart scripts/update-readmes.js] Updating Charts README.md files...");
execSync(`cd ${srcPath} && helm-docs --document-dependency-values=true -t templates/README.md.gotmpl`);

console.log("[kie-sandbox-helm-chart scripts/update-readmes.js] Updating package README.md file");
const srcReadmePath = path.resolve(path.join(packageRootPath, "./src/README.md"));
const rootReadmePath = path.resolve(path.join(packageRootPath, "./README.md"));
const chartReadmeSections = fs.readFileSync(srcReadmePath).toString().split("## Values");
const readmeSections = fs.readFileSync(rootReadmePath).toString().split("<!-- CHART_VALUES_README -->");
readmeSections[1] = chartReadmeSections[1];
const newContent = readmeSections.join("<!-- CHART_VALUES_README -->");
fs.writeFileSync(rootReadmePath, newContent);

console.log("[kie-sandbox-helm-chart scripts/update-readmes.js] Done!");

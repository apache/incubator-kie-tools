const { execSync } = require("child_process");
const path = require("path");

// Install helm-docs if not installed
if (process.platform === "win32") {
  execSync("where /q helm-docs || go install github.com/norwoodj/helm-docs/cmd/helm-docs@v1.13.1");
} else {
  execSync("which helm-docs || go install github.com/norwoodj/helm-docs/cmd/helm-docs@v1.13.1");
}

const srcPath = path.resolve(path.join(__dirname, "../src"));
const readmeTemplatePath = path.resolve(path.join(__dirname, "../src/templates/README.md.gotmpl"));

console.log(srcPath);

execSync(`helm-docs --document-dependency-values=true -c ${srcPath} -t ${readmeTemplatePath}`);

const fs = require("fs");
const path = require("path");

const inputVersion = process.argv[2];

const MANIFEST_FILE = path.resolve("vscode-java-code-completion-extension-plugin-core/META-INF/MANIFEST.MF");
console.info(
  "[vscode-java-code-completion-extension-plugin-install] Verifying manifest file..." +
    MANIFEST_FILE +
    " for version " +
    inputVersion
);

const manifestFile = fs.readFileSync(MANIFEST_FILE, "utf-8");

manifestFile.split("\n").forEach((line) => {
  if (line.startsWith(`Bundle-Version: `) && line.slice(16) != inputVersion) {
    throw new Error("version mis-match for " + MANIFEST_FILE);
  }
});

const MAVEN_CONFIG_FILE = path.resolve(".mvn/maven.config");
const mavenConfigFile = fs.readFileSync(MAVEN_CONFIG_FILE, "utf-8");

console.info(
  "[vscode-java-code-completion-extension-plugin-install] Verifying maven config file..." +
    MAVEN_CONFIG_FILE +
    " for version " +
    inputVersion
);

mavenConfigFile.split("\n").forEach((line) => {
  if (line.startsWith(`-Drevision=`) && line.slice(11) != inputVersion) {
    throw new Error("version mis-match for " + MAVEN_CONFIG_FILE);
  }
});

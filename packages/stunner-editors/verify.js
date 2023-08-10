const fs = require("fs");
const path = require("path");

const inputVersion = process.argv[2];

const MAVEN_CONFIG_FILE = path.resolve(".mvn/maven.config");
const mavenConfigFile = fs.readFileSync(MAVEN_CONFIG_FILE, "utf-8");

console.info("[stunner-editors] Verifying maven config file..." + MAVEN_CONFIG_FILE + " for version " + inputVersion);

mavenConfigFile.split("\n").forEach((line) => {
  if (line.startsWith(`-Drevision=`) && line.slice(11) != inputVersion) {
    throw new Error("version mis-match for " + MAVEN_CONFIG_FILE);
  }
});

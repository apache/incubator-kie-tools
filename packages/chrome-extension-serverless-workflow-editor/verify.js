const fs = require("fs");
const path = require("path");

const inputVersion = process.argv[2];

const MANIFEST_DEV_FILE = path.resolve("manifest.dev.json");
const MANIFEST_PROD_FILE = path.resolve("manifest.prod.json");

console.info(
  "[chrome-extension-serverless-workflow-editor-install] Verifying manifest files..." + " for version " + inputVersion
);

async function verifyManifestFile(file) {
  let json = JSON.parse(await fs.readFileSync(file));
  if (json.version != inputVersion) {
    throw new Error("version mis-match for " + file);
  }
}
verifyManifestFile(MANIFEST_DEV_FILE);
verifyManifestFile(MANIFEST_PROD_FILE);

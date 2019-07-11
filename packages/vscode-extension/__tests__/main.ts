import * as __path from "path";
import * as vscodeTest from "vscode-test";

async function main() {
  try {
    const extensionDevelopmentPath = __path.resolve(__dirname, "../../");
    const extensionTestsPath = __path.resolve(__dirname, "./suite");
    const workspace = __path.resolve(__dirname, "../test-workspace");

    await vscodeTest.runTests({
      extensionDevelopmentPath,
      extensionTestsPath,
      launchArgs: [workspace, "--disableExtensions"]
    });
  } catch (e) {
    console.error("Failed to run integration tests");
    process.exit(1);
  }
}

main();

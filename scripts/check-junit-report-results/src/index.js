////////////////////////////////////////////////////////////////////////////////////////////
// NOTE:
// This is a `github-script`. Meant to be run by the `actions/github-script` GitHub Action.
////////////////////////////////////////////////////////////////////////////////////////////

const parseFile = require("./parseFile");

module.exports = async ({ core, glob, patterns }) => {
  console.log("JUnit Report patterns:");
  console.log(patterns);
  console.log("----");
  console.log("\n\n");

  const failedTestCases = [];
  const passedTestCases = [];

  const globber = await glob.create(patterns.join("\n"), { followSymbolicLinks: false });
  for await (const filePath of globber.globGenerator()) {
    if (filePath.includes("/node_modules/")) {
      continue;
    }

    parseFile(filePath, failedTestCases, passedTestCases);
  }

  console.log("\n\n");

  if (failedTestCases.length > 0) {
    for (const failedTestCase of failedTestCases) {
      console.log(`TEST FAILED: ${failedTestCase["@_name"]}`);
      console.log((failedTestCase["failure"] ?? failedTestCase["error"])["#text"]);
      console.log("-------------------------------------------------");
      console.log("\n\n");
    }

    core.setFailed(
      `❌ There are ${failedTestCases.length} test failures. ${passedTestCases.length} tests succedded, though :)`
    );
  } else {
    console.log(`✅ All ${passedTestCases.length} tests passed!`);
  }

  console.log("Done.");
};

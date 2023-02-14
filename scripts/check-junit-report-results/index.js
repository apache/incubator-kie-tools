////////////////////////////////////////////////////////////////////////////////////////////
// NOTE:
// This is a `github-script`. Meant to be run by the `actions/github-script` GitHub Action.
////////////////////////////////////////////////////////////////////////////////////////////

const fs = require("fs");
const { XMLParser } = require("fast-xml-parser");

module.exports = async ({ core, glob, patterns }) => {
  const parser = new XMLParser({ ignoreAttributes: false });

  console.log("JUnit Report patterns:");
  console.log(patterns);
  console.log("----");
  console.log("\n\n");

  const failedTestCases = [];
  const passedTestCases = [];

  const globber = await glob.create(patterns.join("\n"), { followSymbolicLinks: false });
  for await (const file of globber.globGenerator()) {
    if (file.includes("/node_modules/")) {
      continue;
    }

    console.log(`Processing '${file}'....`);

    const junitReport = parser.parse(fs.readFileSync(file, "utf-8"));

    const testSuites = junitReport["testsuites"];
    if (typeof testSuites !== "object") {
      throw new Error("Can't parse 'testsuites'");
    }

    for (const testSuiteKey in testSuites) {
      if (testSuiteKey.startsWith("@_")) {
        continue;
      }

      const testSuiteContents = testSuites[testSuiteKey];
      if (!Array.isArray(testSuiteContents)) {
        continue;
      }

      for (const testSuite of testSuiteContents) {
        if (typeof testSuite !== "object") {
          continue;
        }

        const testCases = testSuite["testcase"];
        if (!Array.isArray(testCases)) {
          continue;
        }

        for (const testCase of testCases) {
          if (typeof testCase !== "object") {
            continue;
          }

          const failure = testCase["failure"] ?? testCase["error"];
          if (!failure) {
            passedTestCases.push(testCase);
            continue;
          }

          if (typeof failure === "object") {
            failedTestCases.push(testCase);
          } else if (typeof failure === "string") {
            failedTestCases.push({ ...testCase, failure: { "#text": failure } });
          } else {
            throw Error(`Can't parse 'failure': ${JSON.stringify(failure, null, 2)}.`);
          }
        }
      }
    }
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

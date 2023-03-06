const { XMLParser } = require("fast-xml-parser");

const fs = require("fs");

const arrays = new Set(["testsuites.testsuite", "testsuites.testsuite.testcase", "testsuite.testcase"]);

const parser = new XMLParser({
  ignoreAttributes: false,
  isArray: (tagName, jPath) => arrays.has(jPath),
});

module.exports = (filePath, failedTestCases, passedTestCases) => {
  console.log(`Processing '${filePath}'....`);
  const junitReport = parser.parse(fs.readFileSync(filePath, "utf-8"));

  const testSuites = junitReport["testsuites"] ?? { testsuite: [junitReport["testsuite"]] };
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
      if (!testCases) {
        continue;
      }

      if (!Array.isArray(testCases)) {
        throw new Error("Can't parse 'testcase' array");
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
};

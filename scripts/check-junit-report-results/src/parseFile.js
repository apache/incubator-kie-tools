/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

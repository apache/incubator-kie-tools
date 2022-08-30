/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

module.exports = {
  globals: {
    "ts-jest": {
      tsconfig: "<rootDir>/tsconfig.json",
    },
  },
  reporters: [
    "default",
    [
      "jest-junit",
      {
        suiteName: "Chrome Extension for BPMN and DMN",
        outputFile: "./dist-it-tests/junit-report.xml",
        classNameTemplate: "Chrome Extension for BPMN and DMN ::",
        titleTemplate: "{title}",
        ancestorSeparator: " :: ",
        usePathForSuiteName: "true",
        addFileAttribute: "true",
      },
    ],
  ],
  transform: {
    "^.+\\.(ts)$": "ts-jest",
  },
  testRegex: ["it-tests/tests/.*Test.ts"],
  testTimeout: 100000,
};

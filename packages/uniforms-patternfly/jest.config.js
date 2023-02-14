/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
      tsconfig: "<rootDir>/tsconfig.esm.json",
    },
  },
  reporters: ["default", ["jest-junit", { outputFile: "./dist-tests/junit-report.xml" }]],
  setupFilesAfterEnv: ["./src/__tests__/jest.setup.ts"],
  moduleDirectories: ["node_modules"],
  testRegex: "src/__tests__/.*\\.test\\.(jsx?|tsx?)$",
  transform: {
    "^.+\\.jsx?$": ["babel-jest", { presets: [["@babel/env", { targets: { node: "current" } }], "@babel/react"] }],
    "^.+\\.tsx?$": "ts-jest",
  },
  moduleNameMapper: {
    "\\.(css|less|sass|scss)$": "<rootDir>/__mocks__/styleMock.js",
  },
};

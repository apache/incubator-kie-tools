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

/** @type {import('jest').Config} */
const config = {
  preset: "ts-jest",
  reporters: ["default", ["jest-junit", { outputFile: "./dist-tests/junit-report.xml" }]],
  moduleDirectories: ["node_modules"],
  moduleFileExtensions: ["js", "jsx", "ts", "tsx"],
  testRegex: "/tests/.*\\.test\\.(jsx?|tsx?)$",
};

/**
 * Jest setup file should be located on `tests/jest.setup.ts`
 */
const jestSetupPath = "<rootDir>/tests/jest.setup.ts";

/**
 * Style mock should be located on `__mocks_/styleMock.js`
 */
const styleMock = {
  "\\.(css|less|sass|scss)$": "<rootDir>/tests/__mocks__/styleMock.js",
};

/**
 * Monaco mock should be located on `__mocks_/monacoMock.js`
 */
const monacoMock = {
  "@kie-tools-core/monaco-editor": "<rootDir>/tests/__mocks__/monacoMock.js",
};

const babelTransform = {
  "^.+\\.jsx?$": ["babel-jest", { presets: [["@babel/env", { targets: { node: "current" } }]] }],
};

const typescriptTransform = {
  "^.+\\.tsx?$": ["ts-jest", { tsconfig: "./tsconfig.test.json" }],
};

module.exports = {
  babelTransform,
  config,
  jestSetupPath,
  monacoMock,
  styleMock,
  typescriptTransform,
};

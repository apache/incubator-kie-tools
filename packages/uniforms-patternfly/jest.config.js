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
  collectCoverageFrom: ["src/*.{ts,tsx}"],
  moduleNameMapper: {
    "\\.(css|less)$": "<rootDir>/__mocks__/styleMock.js",
    "^uniforms$": "<rootDir>/node_modules/uniforms/src",
    "^uniforms/es5$": "<rootDir>/node_modules/uniforms/src",
    "^uniforms-bridge-simple-schema-2$": "<rootDir>/node_modules/uniforms-bridge-simple-schema-2/src",
    "^uniforms-patternfly$": "<rootDir>/src",
  },
  setupFiles: ["<rootDir>/setupEnzyme.js"],
  testMatch: ["**/__tests__/**/!(_)*.{ts,tsx}", "!**/*.d.ts", "!**/helpers/*.ts"],
  moduleDirectories: ["node_modules", "<rootDir>/src"],
  preset: "ts-jest",
  transformIgnorePatterns: ["node_modules/(?!uniforms)"],
  transform: {
    "^.+\\.(js|ts|tsx)$": "./transform.js",
  },
};

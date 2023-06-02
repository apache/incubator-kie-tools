/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const { varsWithName, getOrDefault, str2bool, composeEnv } = require("@kie-tools-scripts/build-env");
const rootPackageJson = require("../../../package.json");

module.exports = composeEnv([], {
  vars: varsWithName({
    KIE_TOOLS_BUILD__runLinters: {
      default: `${true}`,
      description: "Enables/disables running linters during the build.",
    },
    KIE_TOOLS_BUILD__runTests: {
      default: `${true}`,
      description: "Enables/disables running tests during the build.",
    },
    KIE_TOOLS_BUILD__ignoreTestFailures: {
      default: `${false}`,
      description: "Ignores failures on tests and continues with the build until the end.",
    },
    KIE_TOOLS_BUILD__runIntegrationTests: {
      default: `${false}`,
      description: "Enables/disables running integration tests during the build.",
    },
    KIE_TOOLS_BUILD__ignoreIntegrationTestFailures: {
      default: `${false}`,
      description: "Ignores failures on integration tests and continues with the build until the end.",
    },
    KIE_TOOLS_BUILD__buildContainerImages: {
      default: `${false}`,
      description: "Enables/disables building container images during the build.",
    },
    KIE_TOOLS_BUILD__buildExamples: {
      default: `${false}`,
      description: "Enables/disables building example packages during the build.",
    },
    QUARKUS_PLATFORM_version: {
      default: "2.16.7.Final",
      description: "Quarkus version to be used on dependency declaration.",
    },
    KOGITO_RUNTIME_version: {
      default: "1.39.0.Final",
      description: "Kogito version to be used on dependency declaration.",
    },
  }),
  get env() {
    return {
      root: {
        version: rootPackageJson.version,
      },
      tests: {
        run: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__runTests)),
        ignoreFailures: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__ignoreTestFailures)),
      },
      integrationTests: {
        run: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__runIntegrationTests)),
        ignoreFailures: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__ignoreIntegrationTestFailures)),
      },
      linters: {
        run: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__runLinters)),
      },
      containerImages: {
        build: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__buildContainerImages)),
      },
      examples: {
        build: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__buildExamples)),
      },
      kogitoRuntime: {
        version: getOrDefault(this.vars.KOGITO_RUNTIME_version),
      },
      quarkusPlatform: {
        version: getOrDefault(this.vars.QUARKUS_PLATFORM_version),
      },
    };
  },
});

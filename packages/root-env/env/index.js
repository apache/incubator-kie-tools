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
    KIE_TOOLS_BUILD__runScriptsTests: {
      default: `${false}`,
      description: "Enables/disables running scripts/ tests during bootstrap.",
    },
    KIE_TOOLS_BUILD__ignoreScriptTestFailures: {
      default: `${false}`,
      description: "Ignores failures on scripts/ tests and continues with bootstrap until the end.",
    },
    KIE_TOOLS_BUILD__runEndToEndTests: {
      default: `${false}`,
      description: "Enables/disables running end-to-end tests during the build.",
    },
    KIE_TOOLS_BUILD__containerizedEndToEndTests: {
      default: `${true}`,
      description: "Enables/disables running end-to-end tests inside a container during the test execution.",
    },
    KIE_TOOLS_BUILD__ignoreEndToEndTestFailures: {
      default: `${false}`,
      description: "Ignores failures on end-to-end tests and continues with the build until the end.",
    },
    KIE_TOOLS_BUILD__buildContainerImages: {
      default: `${false}`,
      description: "Enables/disables building container images during the build.",
    },
    KIE_TOOLS_BUILD__buildExamples: {
      default: `${false}`,
      description: "Enables/disables building example packages during the build.",
    },
    /* (begin) This part of the file is referenced in `scripts/update-stream-name` */
    KIE_TOOLS_BUILD__streamName: {
      default: "main",
      description: `Name of the release/development stream. E.g, 'main', or '10.0.x' etc. Useful for publishing "nightly" or "snapshot" artifacts. Created to reflect the Git branch name without coupling with Git itself.`,
    },
    /* (end) */
    QUARKUS_PLATFORM_version: {
      default: "3.15.3.1",
      description: "Quarkus version to be used on dependency declaration.",
    },
    /* (begin) This part of the file is referenced in `scripts/update-kogito-version` */
    KOGITO_RUNTIME_version: {
      default: "999-20250330-SNAPSHOT",
      description: "Kogito version to be used on dependency declaration.",
    },
    /* (end) */
  }),
  get env() {
    return {
      root: {
        version: rootPackageJson.version,
        streamName: getOrDefault(this.vars.KIE_TOOLS_BUILD__streamName),
      },
      tests: {
        run: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__runTests)),
        ignoreFailures: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__ignoreTestFailures)),
      },
      scriptTests: {
        run: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__runScriptsTests)),
        ignoreFailures: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__ignoreScriptTestFailures)),
      },
      endToEndTests: {
        run: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__runEndToEndTests)),
        ignoreFailures: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__ignoreEndToEndTestFailures)),
        containerized: str2bool(getOrDefault(this.vars.KIE_TOOLS_BUILD__containerizedEndToEndTests)),
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
      versions: {
        kogito: getOrDefault(this.vars.KOGITO_RUNTIME_version),
        quarkus: getOrDefault(this.vars.QUARKUS_PLATFORM_version),
      },
    };
  },
});

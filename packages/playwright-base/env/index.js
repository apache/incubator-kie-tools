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

const { varsWithName, composeEnv, str2bool, getOrDefault } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    PLAYWRIGHT_BASE__installDeps: {
      default: "false",
      description: "Toggles the installation of Playwright dependencies. Can be `true` or `false`.",
    },
    PLAYWRIGHT_BASE__enableGoogleChromeTestsForAppleSilicon: {
      default: "true",
      description: "Enable Google Chrome tests for ARM OSs. Overrides PLAYWRIGHT_BASE__enableGoogleChromeProject.",
    },
    PLAYWRIGHT_BASE__enableChromiumProject: {
      default: "true",
      description: "Enable or disable Chromium project",
    },
    PLAYWRIGHT_BASE__enableGoogleChromeProject: {
      default: "true",
      description: "Enable or disable Google Chrome project",
    },
    PLAYWRIGHT_BASE__enableWebkitProject: {
      default: "true",
      description: "Enable or disable Webkit project",
    },
    PLAYWRIGHT_BASE__projectTimeout: {
      default: "90000",
      description: "The timeout of a test in the project (Chromium, Google Chrome and Webkit)",
    },
    PLAYWRIGHT_BASE__expectTimeout: {
      default: "30000",
      description: "The timeout of an expect assertion",
    },
    PLAYWRIGHT_BASE__maxDiffPixelRatio: {
      default: "0.001",
      description: "The threshold of the maximum difference on screenshot comparisons (in percentage)",
    },
    PLAYWRIGHT_BASE__retries: {
      default: "0",
      description: "The amount of times a test will re-run if it fails",
    },
    PLAYWRIGHT_BASE__workers: {
      default: "2",
      description: "The number of workers that will be used to run the tests",
    },
  }),
  get env() {
    return {
      playwrightBase: {
        installDeps: str2bool(getOrDefault(this.vars.PLAYWRIGHT_BASE__installDeps)),
        enableGoogleChromeTestsForAppleSilicon: str2bool(
          getOrDefault(this.vars.PLAYWRIGHT_BASE__enableGoogleChromeTestsForAppleSilicon)
        ),
        enableChromiumProject: str2bool(getOrDefault(this.vars.PLAYWRIGHT_BASE__enableChromiumProject)),
        enableGoogleChromeProject: str2bool(getOrDefault(this.vars.PLAYWRIGHT_BASE__enableGoogleChromeProject)),
        enableWebkitProject: str2bool(getOrDefault(this.vars.PLAYWRIGHT_BASE__enableWebkitProject)),
        projectTimeout: getOrDefault(this.vars.PLAYWRIGHT_BASE__projectTimeout),
        expectTimeout: getOrDefault(this.vars.PLAYWRIGHT_BASE__expectTimeout),
        maxDiffPixelRatio: getOrDefault(this.vars.PLAYWRIGHT_BASE__maxDiffPixelRatio),
        retries: getOrDefault(this.vars.PLAYWRIGHT_BASE__retries),
        workers: getOrDefault(this.vars.PLAYWRIGHT_BASE__workers),
      },
    };
  },
});

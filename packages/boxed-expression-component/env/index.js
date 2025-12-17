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

module.exports = composeEnv(
  [
    require("@kie-tools/root-env/env"),
    require("@kie-tools-core/webpack-base/env"),
    require("@kie-tools/playwright-base/env"),
  ],
  {
    vars: varsWithName({
      BOXED_EXPRESSION_COMPONENT_PLAYWRIGHT__enableGoogleChromeTestsForAppleSilicon: {
        default: "true",
        description: "Enable Google Chrome tests for ARM OSs. Overrides PLAYWRIGHT_BASE__enableGoogleChromeProject.",
      },
    }),
    get env() {
      return {
        boxedExpressionComponent: {
          playwright: {
            enableGoogleChromeTestsForAppleSilicon: str2bool(
              getOrDefault(this.vars.BOXED_EXPRESSION_COMPONENT_PLAYWRIGHT__enableGoogleChromeTestsForAppleSilicon)
            ),
          },
          storybook: {
            port: "9900",
          },
        },
      };
    },
  }
);

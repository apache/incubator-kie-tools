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
    PLAYWRIGHT_BASE__host: {
      default: "localhost",
      description:
        'The app host Playwright is testing. When running inside a container in macOS use "host.docker.internal"',
    },
  }),
  get env() {
    return {
      playwrightBase: {
        installDeps: str2bool(getOrDefault(this.vars.PLAYWRIGHT_BASE__installDeps)),
        host: getOrDefault(this.vars.PLAYWRIGHT_BASE__host),
      },
    };
  },
});

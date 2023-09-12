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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    CHROME_EXTENSION__routerTargetOrigin: {
      default: "https://localhost:9000",
      description: "",
    },
    CHROME_EXTENSION__routerRelativePath: {
      default: "",
      description: "",
    },
    CHROME_EXTENSION__onlineEditorUrl: {
      default: "https://localhost:9001",
      description: "",
    },
    CHROME_EXTENSION__manifestFile: {
      default: "manifest.dev.json",
      description: "",
    },
  }),
  get env() {
    return {
      chromeExtension: {
        dev: {
          port: 9000,
        },
        version: require("../package.json").version,
        routerTargetOrigin: getOrDefault(this.vars.CHROME_EXTENSION__routerTargetOrigin),
        routerRelativePath: getOrDefault(this.vars.CHROME_EXTENSION__routerRelativePath),
        onlineEditorUrl: getOrDefault(this.vars.CHROME_EXTENSION__onlineEditorUrl),
        manifestFile: getOrDefault(this.vars.CHROME_EXTENSION__manifestFile),
      },
    };
  },
});

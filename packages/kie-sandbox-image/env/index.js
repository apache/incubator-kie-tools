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
    KIE_SANDBOX__imageRegistry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX__imageAccount: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX__imageName: {
      default: "kie-sandbox-image",
      description: "",
    },
    KIE_SANDBOX__imageBuildTags: {
      default: "latest",
      description: "",
    },
    KIE_SANDBOX__imagePort: {
      default: "8080",
      description: "",
    },
  }),
  get env() {
    return {
      kieSandbox: {
        image: {
          registry: getOrDefault(this.vars.KIE_SANDBOX__imageRegistry),
          account: getOrDefault(this.vars.KIE_SANDBOX__imageAccount),
          name: getOrDefault(this.vars.KIE_SANDBOX__imageName),
          buildTags: getOrDefault(this.vars.KIE_SANDBOX__imageBuildTags),
          port: getOrDefault(this.vars.KIE_SANDBOX__imagePort),
        },
      },
    };
  },
});

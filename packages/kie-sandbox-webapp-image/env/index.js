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

const rootEnv = require("@kie-tools/root-env/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    KIE_SANDBOX_WEBAPP_IMAGE__imageRegistry: {
      default: "docker.io",
      description: "",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imageAccount: {
      default: "apache",
      description: "",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imageName: {
      default: "incubator-kie-sandbox-webapp",
      description: "",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imageBuildTags: {
      default: rootEnv.env.root.streamName,
      description: "",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imagePort: {
      default: "8080",
      description: "",
    },
  }),
  get env() {
    return {
      kieSandboxWebappImage: {
        registry: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageRegistry),
        account: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageAccount),
        name: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageName),
        buildTags: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageBuildTags),
        port: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imagePort),
      },
    };
  },
});

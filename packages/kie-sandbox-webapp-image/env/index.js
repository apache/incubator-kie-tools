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
      description: "E.g., `docker.io` or `quay.io`.",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imageAccount: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imageName: {
      default: "incubator-kie-sandbox-webapp",
      description: "Name of the image itself.",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imageBuildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    KIE_SANDBOX_WEBAPP_IMAGE__imagePort: {
      default: "8080",
      description: "The internal container port.",
    },
  }),
  get env() {
    return {
      kieSandboxWebappImage: {
        registry: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageRegistry),
        account: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageAccount),
        name: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageName),
        buildTag: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imageBuildTag),
        port: getOrDefault(this.vars.KIE_SANDBOX_WEBAPP_IMAGE__imagePort),
      },
    };
  },
});

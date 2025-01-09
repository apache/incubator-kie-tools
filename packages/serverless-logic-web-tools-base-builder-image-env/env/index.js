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
    SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageRegistry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageAccount: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageName: {
      default: "incubator-kie-serverless-logic-web-tools-base-builder",
      description: "Name of the image itself.",
    },
    SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageBuildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
  }),
  get env() {
    return {
      slwtBaseBuilderImageEnv: {
        registry: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageRegistry),
        account: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageAccount),
        name: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageName),
        buildTag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__baseBuilderImageBuildTag),
      },
    };
  },
});

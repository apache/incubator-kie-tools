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
    SONATAFLOW_MANAGEMENT_CONSOLE__registry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    SONATAFLOW_MANAGEMENT_CONSOLE__account: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    SONATAFLOW_MANAGEMENT_CONSOLE__name: {
      default: "incubator-kie-sonataflow-management-console",
      description: "Name of the image itself.",
    },
    SONATAFLOW_MANAGEMENT_CONSOLE__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    SONATAFLOW_MANAGEMENT_CONSOLE__port: {
      default: 8080,
      description: "The internal container port.",
    },
  }),
  get env() {
    return {
      sonataflowManagementConsoleImageEnv: {
        registry: getOrDefault(this.vars.SONATAFLOW_MANAGEMENT_CONSOLE__registry),
        account: getOrDefault(this.vars.SONATAFLOW_MANAGEMENT_CONSOLE__account),
        name: getOrDefault(this.vars.SONATAFLOW_MANAGEMENT_CONSOLE__name),
        buildTag: getOrDefault(this.vars.SONATAFLOW_MANAGEMENT_CONSOLE__buildTag),
        port: getOrDefault(this.vars.SONATAFLOW_MANAGEMENT_CONSOLE__port),
      },
    };
  },
});

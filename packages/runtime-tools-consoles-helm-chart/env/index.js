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
    RUNTIME_TOOLS_CONSOLES_HELM_CHART__registry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    RUNTIME_TOOLS_CONSOLES_HELM_CHART__account: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    RUNTIME_TOOLS_CONSOLES_HELM_CHART__name: {
      default: "incubator-kie-runtime-tools-consoles-helm-chart",
      description: "Name of the chart itself.",
    },
    RUNTIME_TOOLS_CONSOLES_HELM_CHART__tag: {
      default: require("../package.json").version, // Needs to be SemVer, so we can't use rootEnv.env.root.streamName.
      description: "Version of the Helm Chart. Needs to be SemVer-compatible.",
    },
  }),
  get env() {
    return {
      runtimeToolsConsolesHelmChart: {
        registry: getOrDefault(this.vars.RUNTIME_TOOLS_CONSOLES_HELM_CHART__registry),
        account: getOrDefault(this.vars.RUNTIME_TOOLS_CONSOLES_HELM_CHART__account),
        name: getOrDefault(this.vars.RUNTIME_TOOLS_CONSOLES_HELM_CHART__name),
        tag: getOrDefault(this.vars.RUNTIME_TOOLS_CONSOLES_HELM_CHART__tag),
      },
    };
  },
});

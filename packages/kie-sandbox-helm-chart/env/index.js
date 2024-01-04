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
    KIE_SANDBOX_HELM_CHART__registry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX_HELM_CHART__account: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX_HELM_CHART__name: {
      default: "kie-sandbox",
      description: "",
    },
    KIE_SANDBOX_HELM_CHART__tag: {
      default: require("../package.json").version,
      description: "",
    },
  }),
  get env() {
    return {
      kieSandboxHelmChart: {
        registry: getOrDefault(this.vars.KIE_SANDBOX_HELM_CHART__registry),
        account: getOrDefault(this.vars.KIE_SANDBOX_HELM_CHART__account),
        name: getOrDefault(this.vars.KIE_SANDBOX_HELM_CHART__name),
        tag: getOrDefault(this.vars.KIE_SANDBOX_HELM_CHART__tag),
      },
    };
  },
});

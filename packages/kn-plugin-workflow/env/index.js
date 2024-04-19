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
const packageJson = require("@kie-tools/kn-plugin-workflow/package.json");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    KN_PLUGIN_WORKFLOW__version: {
      name: "KN_PLUGIN_WORKFLOW__version",
      default: packageJson.version,
      description: "Knative SonataFlow plugin version",
    },
    KN_PLUGIN_WORKFLOW__quarkusPlatformGroupId: {
      name: "KN_PLUGIN_WORKFLOW__quarkusPlatformGroupId",
      default: "io.quarkus.platform",
      description: "Quarkus group to be used when creating the SonataFlow project",
    },
    KN_PLUGIN_WORKFLOW__devModeImage: {
      name: "KN_PLUGIN_WORKFLOW__devModeImage",
      default: "quay.io/kiegroup/kogito-swf-devmode-nightly:999-20240417",
      description: "SonataFlow dev mode image (used on cli run)",
    },
  }),
  get env() {
    return {
      knPluginWorkflow: {
        version: getOrDefault(this.vars.KN_PLUGIN_WORKFLOW__version),
        quarkusPlatformGroupId: getOrDefault(this.vars.KN_PLUGIN_WORKFLOW__quarkusPlatformGroupId),
        devModeImage: getOrDefault(this.vars.KN_PLUGIN_WORKFLOW__devModeImage),
      },
    };
  },
});

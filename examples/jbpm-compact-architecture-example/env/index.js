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

const { varsWithName, composeEnv, getOrDefault } = require("@kie-tools-scripts/build-env");

const {
  env: { kogitoManagementConsole: kogitoManagementConsoleEnv },
} = require("@kie-tools/kogito-management-console/env");

const {
  env: { kogitoTaskConsole: kogitoTaskConsoleEnv },
} = require("@kie-tools/kogito-task-console/env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    JBPM_COMPACT_ARCHITECTURE_EXAMPLE__managementConsoleImage: {
      default: `${kogitoManagementConsoleEnv.registry}/${kogitoManagementConsoleEnv.account}/${kogitoManagementConsoleEnv.name}:${kogitoManagementConsoleEnv.buildTag}`,
      description: "The image for the Kogito Management Console Image.",
    },
    JBPM_COMPACT_ARCHITECTURE_EXAMPLE__taskConsoleImage: {
      default: `${kogitoTaskConsoleEnv.registry}/${kogitoTaskConsoleEnv.account}/${kogitoTaskConsoleEnv.name}:${kogitoTaskConsoleEnv.buildTag}`,
      description: "The image for the Kogito Task Console Image.",
    },
  }),
  get env() {
    return {
      jbpmCompactArchitectureExample: {
        kogitoManagementConsoleImage: getOrDefault(this.vars.JBPM_COMPACT_ARCHITECTURE_EXAMPLE__managementConsoleImage),
        kogitoTaskConsoleImage: getOrDefault(this.vars.JBPM_COMPACT_ARCHITECTURE_EXAMPLE__taskConsoleImage),
        version: require("../package.json").version,
      },
    };
  },
});

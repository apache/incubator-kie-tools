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
    KOGITO_IMAGES_CEKIT_MODULES__quarkusGroupId: {
      default: "io.quarkus.platform",
      description: "Quarkus platform group id.",
    },
    KOGITO_IMAGES_CEKIT_MODULES_quarkusRegistries: {
      default: "registry.quarkus.io",
      description:
        "Quarkus Registry URLs comma-separated to add to the .quarkus/config.yaml file. Variables substitutions are done during bootstrap phase.",
      example: "registry.com,registry2.com",
    },
  }),
  get env() {
    return {
      kogitoImagesCekitModules: {
        quarkusGroupId: getOrDefault(this.vars.KOGITO_IMAGES_CEKIT_MODULES__quarkusGroupId),
        quarkusRegistries: getOrDefault(this.vars.KOGITO_IMAGES_CEKIT_MODULES_quarkusRegistries),
      },
    };
  },
});

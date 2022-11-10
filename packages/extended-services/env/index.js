/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const { getOrDefault, varsWithName, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    EXTENDED_SERVICES__modeler: {
      default: "https://sandbox.kie.org/#/",
      description: "Modeler",
    },
    EXTENDED_SERVICES__version: {
      default: require("../package.json").version,
      description: "Extended Services version",
    },
    EXTENDED_SERVICES__serverIp: {
      default: "0.0.0.0",
      description: "Extended Services IP",
    },
    EXTENDED_SERVICES__serverPort: {
      default: "21345",
      description: "Extended Services port",
    },
  }),
  get env() {
    return {
      extendedServices: {
        version: getOrDefault(this.vars.EXTENDED_SERVICES__version),
        modeler: getOrDefault(this.vars.EXTENDED_SERVICES__modeler),
        serverIp: getOrDefault(this.vars.EXTENDED_SERVICES__serverIp),
        serverPort: getOrDefault(this.vars.EXTENDED_SERVICES__serverPort),
      },
    };
  },
});

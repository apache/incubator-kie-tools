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

module.exports = composeEnv([require("@kie-tools/root-env/env"), require("@kie-tools/extended-services-java/env")], {
  vars: varsWithName({
    KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry: {
      default: "quay.io",
      description: "",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageAccount: {
      default: "kie-tools",
      description: "",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageName: {
      default: "kie-sandbox-extended-services-image",
      description: "",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTags: {
      default: "latest",
      description: "",
    },
  }),
  get env() {
    return {
      extendedServicesImage: {
        registry: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry),
        account: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageAccount),
        name: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageName),
        buildTags: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTags),
      },
    };
  },
});

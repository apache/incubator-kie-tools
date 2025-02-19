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

const {
  env: { extendedServicesJava: extendedServicesJavaEnv },
} = require("@kie-tools/extended-services-java/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    KIE_SANDBOX_EXTENDED_SERVICES__builderImage: {
      default: "registry.access.redhat.com/ubi9/openjdk-17:1.21",
      description: "The image used in the FROM import.",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageAccount: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageName: {
      default: "incubator-kie-sandbox-extended-services",
      description: "Name of the image itself.",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    KIE_SANDBOX_EXTENDED_SERVICES__imagePort: {
      default: extendedServicesJavaEnv.port,
      description: "Internal HTTP port of the Extended Services app.",
    },
  }),
  get env() {
    return {
      extendedServicesImage: {
        builderImage: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__builderImage),
        registry: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageRegistry),
        account: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageAccount),
        name: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageName),
        buildTag: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imageBuildTag),
        port: getOrDefault(this.vars.KIE_SANDBOX_EXTENDED_SERVICES__imagePort),
      },
    };
  },
});

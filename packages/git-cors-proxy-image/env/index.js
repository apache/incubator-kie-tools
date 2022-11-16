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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    GIT_CORS_PROXY__imageRegistry: {
      default: "quay.io",
      description: "",
    },
    GIT_CORS_PROXY__imageAccount: {
      default: "kie-tools",
      description: "",
    },
    GIT_CORS_PROXY__imageName: {
      default: "git-cors-proxy-image",
      description: "",
    },
    GIT_CORS_PROXY__imageBuildTags: {
      default: "latest",
      description: "",
    },
  }),
  get env() {
    return {
      gitCorsProxy: {
        image: {
          registry: getOrDefault(this.vars.GIT_CORS_PROXY__imageRegistry),
          account: getOrDefault(this.vars.GIT_CORS_PROXY__imageAccount),
          name: getOrDefault(this.vars.GIT_CORS_PROXY__imageName),
          buildTags: getOrDefault(this.vars.GIT_CORS_PROXY__imageBuildTags),
        },
        dev: {
          port: 3000,
        },
      },
    };
  },
});

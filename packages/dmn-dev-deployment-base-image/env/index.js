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
    DMN_DEV_DEPLOYMENT_BASE_IMAGE__registry: {
      default: "quay.io",
      description: "The image registry.",
    },
    DMN_DEV_DEPLOYMENT_BASE_IMAGE__account: {
      default: "kie-tools",
      description: "The image registry account.",
    },
    DMN_DEV_DEPLOYMENT_BASE_IMAGE__name: {
      default: "dmn-dev-deployment-base-image",
      description: "The image name.",
    },
    DMN_DEV_DEPLOYMENT_BASE_IMAGE__buildTags: {
      default: "daily-dev",
      description: "The image tag.",
    },
  }),
  get env() {
    return {
      dmnDevDeploymentBaseImage: {
        registry: getOrDefault(this.vars.DMN_DEV_DEPLOYMENT_BASE_IMAGE__registry),
        account: getOrDefault(this.vars.DMN_DEV_DEPLOYMENT_BASE_IMAGE__account),
        name: getOrDefault(this.vars.DMN_DEV_DEPLOYMENT_BASE_IMAGE__name),
        tags: getOrDefault(this.vars.DMN_DEV_DEPLOYMENT_BASE_IMAGE__buildTags),
      },
    };
  },
});

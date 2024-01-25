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

const buildEnv = require("@kie-tools/root-env/env");
const devDeploymentBaseImageEnv = require("@kie-tools/dev-deployment-base-image/env");

module.exports = composeEnv([buildEnv, devDeploymentBaseImageEnv], {
  vars: varsWithName({
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__builderImage: {
      default: `${devDeploymentBaseImageEnv.env.devDeploymentBaseImage.registry}/${
        devDeploymentBaseImageEnv.env.devDeploymentBaseImage.account
      }/${devDeploymentBaseImageEnv.env.devDeploymentBaseImage.name}:${
        devDeploymentBaseImageEnv.env.devDeploymentBaseImage.tags.split(" ")[0]
      }`,
      description: "The image used in the FROM import.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__registry: {
      default: "quay.io",
      description: "The image registry.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__account: {
      default: "kie-tools",
      description: "The image registry account.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__name: {
      default: "dev-deployment-kogito-quarkus-blank-app-image",
      description: "The image name.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__buildTags: {
      default: "daily-dev",
      description: "The image tag.",
    },
  }),
  get env() {
    return {
      devDeploymentKogitoQuarkusBlankAppImage: {
        builderImage: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__builderImage),
        registry: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__registry),
        account: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__account),
        name: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__name),
        tags: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__buildTags),
        version: require("../package.json").version,
      },
    };
  },
});

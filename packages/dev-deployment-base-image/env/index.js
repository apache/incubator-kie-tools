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

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    DEV_DEPLOYMENT_BASE_IMAGE__builderImage: {
      default: "registry.access.redhat.com/ubi9/openjdk-17:1.18",
      description: "The image used in the FROM import.",
    },
    DEV_DEPLOYMENT_BASE_IMAGE__userId: {
      default: 185,
      description: "The container User ID.",
    },
    DEV_DEPLOYMENT_BASE_IMAGE__homePath: {
      default: "/home",
      description: "The container Home Path.",
    },
    DEV_DEPLOYMENT_BASE_IMAGE__registry: {
      default: "quay.io",
      description: "The image registry.",
    },
    DEV_DEPLOYMENT_BASE_IMAGE__account: {
      default: "kie-tools",
      description: "The image registry account.",
    },
    DEV_DEPLOYMENT_BASE_IMAGE__name: {
      default: "dev-deployment-base-image",
      description: "The image name.",
    },
    DEV_DEPLOYMENT_BASE_IMAGE__buildTags: {
      default: "daily-dev",
      description: "The image tag.",
    },
  }),
  get env() {
    return {
      devDeploymentBaseImage: {
        builderImage: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__builderImage),
        userId: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__userId),
        homePath: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__homePath),
        registry: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__registry),
        account: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__account),
        name: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__name),
        tags: getOrDefault(this.vars.DEV_DEPLOYMENT_BASE_IMAGE__buildTags),
        version: require("../package.json").version,
      },
    };
  },
});

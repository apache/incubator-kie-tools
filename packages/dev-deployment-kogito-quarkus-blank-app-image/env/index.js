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

const rootEnv = require("@kie-tools/root-env/env");

const {
  env: { devDeploymentBaseImage: devDeploymentBaseImageEnv },
} = require("@kie-tools/dev-deployment-base-image/env");

const {
  env: { mavenM2RepoViaHttpImage: mavenM2RepoViaHttpImageEnv },
} = require("@kie-tools/maven-m2-repo-via-http-image/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__builderImage: {
      default: `${devDeploymentBaseImageEnv.registry}/${devDeploymentBaseImageEnv.account}/${devDeploymentBaseImageEnv.name}:${devDeploymentBaseImageEnv.buildTag}`,
      description: "The image used in the FROM import.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__registry: {
      default: "docker.io",
      description: "The image registry.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__account: {
      default: "apache",
      description: "The image registry account.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__name: {
      default: "incubator-kie-sandbox-dev-deployment-kogito-quarkus-blank-app",
      description: "The image name.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "The image tag.",
    },
    DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__mavenM2RepoViaHttpImage: {
      default: `${mavenM2RepoViaHttpImageEnv.registry}/${mavenM2RepoViaHttpImageEnv.account}/${mavenM2RepoViaHttpImageEnv.name}:${mavenM2RepoViaHttpImageEnv.tag}`,
      description: "The image tag for the Maven M2 Repo via HTTP. Used during the build only.",
    },
  }),
  get env() {
    return {
      devDeploymentKogitoQuarkusBlankAppImage: {
        builderImage: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__builderImage),
        registry: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__registry),
        account: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__account),
        name: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__name),
        buildTag: getOrDefault(this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__buildTag),
        version: require("../package.json").version,
        dev: {
          mavenM2RepoViaHttpImage: getOrDefault(
            this.vars.DEV_DEPLOYMENT_KOGITO_QUARKUS_BLANK_APP_IMAGE__mavenM2RepoViaHttpImage
          ),
        },
      },
    };
  },
});

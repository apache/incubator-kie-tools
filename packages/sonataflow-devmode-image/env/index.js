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
const sonataFlowQuarkusDevUiEnv = require("@kie-tools/sonataflow-quarkus-devui/env");

const {
  env: { mavenM2RepoViaHttpImage: mavenM2RepoViaHttpImageEnv },
} = require("@kie-tools/maven-m2-repo-via-http-image/env");

const rootEnv = require("@kie-tools/root-env/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    SONATAFLOW_DEVMODE_IMAGE__registry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    SONATAFLOW_DEVMODE_IMAGE__account: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    SONATAFLOW_DEVMODE_IMAGE__name: {
      default: "incubator-kie-sonataflow-devmode",
      description: "Name of the image itself.",
    },
    SONATAFLOW_DEVMODE_IMAGE__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
    SONATAFLOW_DEVMODE_IMAGE__sonataflowQuarkusDevUiVersion: {
      default: sonataFlowQuarkusDevUiEnv.env.sonataflowQuarkusDevuiExtension.version,
      description: "SonataFlow Quarkus Dev UI version",
    },
    SONATAFLOW_DEVMODE_IMAGE__mavenM2RepoViaHttpImage: {
      default: `${mavenM2RepoViaHttpImageEnv.registry}/${mavenM2RepoViaHttpImageEnv.account}/${mavenM2RepoViaHttpImageEnv.name}:${mavenM2RepoViaHttpImageEnv.tag}`,
      description: "The image tag for the Maven M2 Repo via HTTP. Used during the build only.",
    },
  }),
  get env() {
    return {
      sonataflowDevModeImage: {
        registry: getOrDefault(this.vars.SONATAFLOW_DEVMODE_IMAGE__registry),
        account: getOrDefault(this.vars.SONATAFLOW_DEVMODE_IMAGE__account),
        name: getOrDefault(this.vars.SONATAFLOW_DEVMODE_IMAGE__name),
        tag: getOrDefault(this.vars.SONATAFLOW_DEVMODE_IMAGE__buildTag),
        version: require("../package.json").version,
        sonataflowQuarkusDevUiVersion: getOrDefault(this.vars.SONATAFLOW_DEVMODE_IMAGE__sonataflowQuarkusDevUiVersion),
        dev: {
          mavenM2RepoViaHttpImage: getOrDefault(this.vars.SONATAFLOW_DEVMODE_IMAGE__mavenM2RepoViaHttpImage),
        },
      },
    };
  },
});

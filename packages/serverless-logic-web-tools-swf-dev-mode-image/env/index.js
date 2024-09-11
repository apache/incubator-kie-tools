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
  env: { mavenM2RepoViaHttpImage: mavenM2RepoViaHttpImageEnv },
} = require("@kie-tools/maven-m2-repo-via-http-image/env");

module.exports = composeEnv([rootEnv, require("@kie-tools/serverless-logic-web-tools-swf-dev-mode-image-env/env")], {
  vars: varsWithName({
    /* (begin) This part of the file is referenced in `scripts/update-kogito-version` */
    SERVERLESS_LOGIC_WEB_TOOLS_DEVMODE_IMAGE__kogitoBaseBuilderImageTag: {
      default: "10.0.999-20240717",
      description: "",
    },
    /* end */
    SERVERLESS_LOGIC_WEB_TOOLS_DEVMODE_IMAGE__mavenM2RepoViaHttpImage: {
      default: `${mavenM2RepoViaHttpImageEnv.registry}/${mavenM2RepoViaHttpImageEnv.account}/${mavenM2RepoViaHttpImageEnv.name}:${mavenM2RepoViaHttpImageEnv.tag}`,
      description: "The image tag for the Maven M2 Repo via HTTP. Used during the build only.",
    },
  }),
  get env() {
    return {
      swfDevModeImage: {
        version: require("../package.json").version,
        kogitoImageTag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS_DEVMODE_IMAGE__kogitoBaseBuilderImageTag),
        dev: {
          mavenM2RepoViaHttpImage: getOrDefault(
            this.vars.SERVERLESS_LOGIC_WEB_TOOLS_DEVMODE_IMAGE__mavenM2RepoViaHttpImage
          ),
        },
      },
    };
  },
});

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

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    MAVEN_M2_REPO_VIA_HTTP_IMAGE__registry: {
      default: "docker.io",
      description: "E.g., `docker.io` or `quay.io`.",
    },
    MAVEN_M2_REPO_VIA_HTTP_IMAGE__account: {
      default: "apache",
      description: "E.g,. `apache` or `kie-tools-bot`",
    },
    MAVEN_M2_REPO_VIA_HTTP_IMAGE__name: {
      default: "incubator-kie-tools-maven-m2-repo-via-http",
      description: "Name of the image itself.",
    },
    MAVEN_M2_REPO_VIA_HTTP_IMAGE__buildTag: {
      default: rootEnv.env.root.streamName,
      description: "Tag version of this image. E.g., `main` or `10.0.x` or `10.0.0",
    },
  }),
  get env() {
    return {
      mavenM2RepoViaHttpImage: {
        registry: getOrDefault(this.vars.MAVEN_M2_REPO_VIA_HTTP_IMAGE__registry),
        account: getOrDefault(this.vars.MAVEN_M2_REPO_VIA_HTTP_IMAGE__account),
        name: getOrDefault(this.vars.MAVEN_M2_REPO_VIA_HTTP_IMAGE__name),
        buildTag: getOrDefault(this.vars.MAVEN_M2_REPO_VIA_HTTP_IMAGE__buildTag),
      },
    };
  },
});

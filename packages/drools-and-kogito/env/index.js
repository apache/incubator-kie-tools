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

const { varsWithName, composeEnv, getOrDefault, str2bool } = require("@kie-tools-scripts/build-env");

const rootEnv = require("@kie-tools/root-env/env");

module.exports = composeEnv([rootEnv], {
  vars: varsWithName({
    DROOLS_AND_KOGITO__droolsRepoUrl: {
      default: "https://github.com/apache/incubator-kie-drools",
      description: "",
    },
    DROOLS_AND_KOGITO__droolsRepoGitRef: {
      default: "878fe13a914a8fe274dd0fcc5dda40290e2c0e2b",
      description: "",
    },
    DROOLS_AND_KOGITO__optaplannerRepoUrl: {
      default: "https://github.com/apache/incubator-kie-optaplanner",
      description: "",
    },
    DROOLS_AND_KOGITO__optaplannerRepoGitRef: {
      default: "55b583cd4dfaf9185c316e12a94dc159b36e2708",
      description: "",
    },
    DROOLS_AND_KOGITO__kogitoRuntimesRepoUrl: {
      default: "https://github.com/apache/incubator-kie-kogito-runtimes",
      description: "",
    },
    DROOLS_AND_KOGITO__kogitoRuntimesRepoGitRef: {
      default: "74159c722b1b4eb4a6f5ab70a7981bb5df93f136",
      description: "",
    },
    DROOLS_AND_KOGITO__kogitoAppsRepoUrl: {
      default: "https://github.com/apache/incubator-kie-kogito-apps",
      description: "",
    },
    DROOLS_AND_KOGITO__kogitoAppsRepoGitRef: {
      default: "0500270f74822858c717ee60e50c07dc18983fcb",
      description: "",
    },
    DROOLS_AND_KOGITO__skip: {
      default: `${false}`,
      description:
        "Whether or not to skip the entire package. No artifacts will be downloaded nor built when this flag is 'true'. Useful for building tags.",
    },
    DROOLS_AND_KOGITO__forceBuild: {
      default: `${false}`,
      description: "Will always build, even if there's a cached build available.",
    },
    DROOLS_AND_KOGITO__cacheDownloadCommand: {
      default: `gh run download --name drools-and-kogito-cached-build-for-${rootEnv.env.versions.kogito} --dir dist`,
      description: "",
    },
    DROOLS_AND_KOGITO__cacheUploadCommand: {
      default: "",
      description: "",
    },
  }),
  get env() {
    return {
      droolsAndKogito: {
        skip: str2bool(getOrDefault(this.vars.DROOLS_AND_KOGITO__skip)),
        forceBuild: str2bool(getOrDefault(this.vars.DROOLS_AND_KOGITO__forceBuild)),
        repos: {
          drools: {
            url: getOrDefault(this.vars.DROOLS_AND_KOGITO__droolsRepoUrl),
            gitRef: getOrDefault(this.vars.DROOLS_AND_KOGITO__droolsRepoGitRef),
          },
          optaplanner: {
            url: getOrDefault(this.vars.DROOLS_AND_KOGITO__optaplannerRepoUrl),
            gitRef: getOrDefault(this.vars.DROOLS_AND_KOGITO__optaplannerRepoGitRef),
          },
          kogitoRuntimes: {
            url: getOrDefault(this.vars.DROOLS_AND_KOGITO__kogitoRuntimesRepoUrl),
            gitRef: getOrDefault(this.vars.DROOLS_AND_KOGITO__kogitoRuntimesRepoGitRef),
          },
          kogitoApps: {
            url: getOrDefault(this.vars.DROOLS_AND_KOGITO__kogitoAppsRepoUrl),
            gitRef: getOrDefault(this.vars.DROOLS_AND_KOGITO__kogitoAppsRepoGitRef),
          },
        },
        cache: {
          commands: {
            download: getOrDefault(this.vars.DROOLS_AND_KOGITO__cacheDownloadCommand),
            upload: getOrDefault(this.vars.DROOLS_AND_KOGITO__cacheUploadCommand),
          },
        },
      },
    };
  },
});

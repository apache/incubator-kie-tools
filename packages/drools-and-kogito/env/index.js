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
      description: "Git repository URL for Drools",
    },
    DROOLS_AND_KOGITO__droolsRepoGitRef: {
      default: "55ff044e20d3cc2222a525616152a9ee33725490",
      description: "Git ref for the Drools repository (SHA, branch, or tag)",
    },
    DROOLS_AND_KOGITO__optaplannerRepoUrl: {
      default: "https://github.com/apache/incubator-kie-optaplanner",
      description: "Git repository URL for OptaPlanner",
    },
    DROOLS_AND_KOGITO__optaplannerRepoGitRef: {
      default: "c857b9532752d5066de3daa7585a90a4e0105fe0",
      description: "Git ref for the OptaPlanner repository (SHA, branch, or tag)",
    },
    DROOLS_AND_KOGITO__kogitoRuntimesRepoUrl: {
      default: "https://github.com/apache/incubator-kie-kogito-runtimes",
      description: "Git repository URL for Kogito Runtimes",
    },
    DROOLS_AND_KOGITO__kogitoRuntimesRepoGitRef: {
      default: "c1e86c1d51e701df6434963a32b1127712a86f49",
      description: "Git ref for the Kogito Runtimes repository (SHA, branch, or tag)",
    },
    DROOLS_AND_KOGITO__kogitoAppsRepoUrl: {
      default: "https://github.com/apache/incubator-kie-kogito-apps",
      description: "Git repository URL for Kogito Apps",
    },
    DROOLS_AND_KOGITO__kogitoAppsRepoGitRef: {
      default: "b58453161115d5b7d232c51aacce76dbcd5f43b4",
      description: "Git ref for the Kogito Apps repository (SHA, branch, or tag)",
    },
    DROOLS_AND_KOGITO__skip: {
      default: `${false}`,
      description:
        "Whether or not to skip the entire package. No artifacts will be downloaded nor built when this flag is 'true'.",
    },
    DROOLS_AND_KOGITO__forceBuild: {
      default: `${false}`,
      description: "Will always build, even if there's a cached build available.",
    },
    DROOLS_AND_KOGITO__cacheDownloadCommand: {
      default: `gh run download --name drools-and-kogito-cached-build-for-${rootEnv.env.versions.kogito} --dir dist`,
      description:
        "Shell command that will try to restore a cached build matching the build info supplied via this package's env vars.",
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
          download: getOrDefault(this.vars.DROOLS_AND_KOGITO__cacheDownloadCommand),
        },
      },
    };
  },
});

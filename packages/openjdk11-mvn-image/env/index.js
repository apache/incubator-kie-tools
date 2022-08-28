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

const { varsWithName, getOrDefault, composeEnv } = require("@kie-tools-build/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env"), require("@kie-tools/openjdk11-mvn-image-env/env")], {
  vars: varsWithName({
    SERVERLESS_LOGIC_SANDBOX__openJdk11MvnImageBuildTags: {
      default: "latest",
      description: "",
    },
    SERVERLESS_LOGIC_SANDBOX__openJdk11MvnOkdVersion: {
      default: "4.10.0-0.okd-2022-06-24-212905",
      description: "",
    },
  }),
  get env() {
    return {
      openJdk11MvnImage: {
        buildTags: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__openJdk11MvnImageBuildTags),
        okdVersion: getOrDefault(this.vars.SERVERLESS_LOGIC_SANDBOX__openJdk11MvnOkdVersion),
      },
    };
  },
});

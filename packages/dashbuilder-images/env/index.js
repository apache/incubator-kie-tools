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

module.exports = composeEnv([require("@kie-tools-build/root/env")], {
  vars: varsWithName({
    DASHBUILDER__baseImageRegistry: {
      default: "quay.io",
      description: "",
    },
    DASHBUILDER__baseImageAccount: {
      default: "kie-tools",
      description: "",
    },
    DASHBUILDER_RUNTIME__baseImageName: {
      default: "dashbuilder-runtime",
      description: "",
    },
    DASHBUILDER_AUTHORING__baseImageName: {
      default: "dashbuilder-authoring",
      description: "",
    },
    DASHBUILDER__baseImageTag: {
      default: "latest",
      description: "",
    },
    DASHBUILDER__baseImageBuildTags: {
      default: "latest",
      description: "",
    },
  }),
  get env() {
    return {
      dashbuilder: {
        baseImage: {
          registry: getOrDefault(this.vars.DASHBUILDER__baseImageRegistry),
          account: getOrDefault(this.vars.DASHBUILDER__baseImageAccount),
          runtimeName: getOrDefault(this.vars.DASHBUILDER_RUNTIME__baseImageName),
          authoringName: getOrDefault(this.vars.DASHBUILDER_AUTHORING__baseImageName),
          tag: getOrDefault(this.vars.DASHBUILDER__baseImageTag),
          buildTags: getOrDefault(this.vars.DASHBUILDER__baseImageBuildTags),
        },
      },
    };
  },
});

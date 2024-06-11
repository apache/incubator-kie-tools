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

module.exports = composeEnv([rootEnv, require("@kie-tools/serverless-logic-web-tools-swf-dev-mode-image-env/env")], {
  vars: varsWithName({
    SERVERLESS_LOGIC_WEB_TOOLS__swfDevModeImageBuildTags: {
      default: rootEnv.env.root.streamName,
      description: "",
    },
    /* (begin) This part of the file is referenced in `scripts/update-kogito-version` */
    SERVERLESS_LOGIC_WEB_TOOLS_DEVMODE_IMAGE__kogitoBaseBuilderImageTag: {
      default: "999-20240509",
      description: "",
    },
    /* end */
  }),
  get env() {
    return {
      swfDevModeImage: {
        version: require("../package.json").version,
        buildTags: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__swfDevModeImageBuildTags),
        kogitoImageTag: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS_DEVMODE_IMAGE__kogitoBaseBuilderImageTag),
      },
    };
  },
});

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
const serverlessLogicWebToolsSwfBuilderImageEnv = require("@kie-tools/serverless-logic-web-tools-swf-builder-image-env/env");
const sonataflowBuilderImageEnv = require("@kie-tools/sonataflow-builder-image/env");

module.exports = composeEnv([rootEnv, serverlessLogicWebToolsSwfBuilderImageEnv, sonataflowBuilderImageEnv], {
  vars: varsWithName({
    SERVERLESS_LOGIC_WEB_TOOLS__swfBuilderImageBuildTags: {
      default: rootEnv.env.root.streamName,
      description: "",
    },
    SERVERLESS_LOGIC_WEB_TOOLS_SWF_BUILDER_IMAGE__baseImageUrl: {
      default: `${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.registry}/${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.account}/${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.name}:${sonataflowBuilderImageEnv.env.sonataflowBuilderImage.tag}`,
      description: "The image used in the FROM import.",
    },
  }),
  get env() {
    return {
      swfBuilderImage: {
        buildTags: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS__swfBuilderImageBuildTags),
        baseImageUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS_SWF_BUILDER_IMAGE__baseImageUrl),
      },
    };
  },
});

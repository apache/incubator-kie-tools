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

module.exports = composeEnv([rootEnv, serverlessLogicWebToolsSwfBuilderImageEnv], {
  vars: varsWithName({
    SERVERLESS_LOGIC_WEB_TOOLS_SWF_BUILDER_IMAGE__baseImageUrl: {
      default: `quay.io/kiegroup/kogito-swf-builder:9.99.1.CR1`, // TODO: Replace with v10.0.0 release from kogito-images
      description: "The image used in the FROM import.",
    },
  }),
  get env() {
    return {
      swfBuilderImage: {
        baseImageUrl: getOrDefault(this.vars.SERVERLESS_LOGIC_WEB_TOOLS_SWF_BUILDER_IMAGE__baseImageUrl),
      },
    };
  },
});

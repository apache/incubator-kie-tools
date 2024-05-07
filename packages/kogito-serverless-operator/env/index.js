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

const kogitoSwfBuilderEnv = require("@kie-tools/kogito-swf-builder/env");
const kogitoSwfDevModeEnv = require("@kie-tools/kogito-swf-devmode/env");
const rootEnv = require("@kie-tools/root-env/env");

module.exports = composeEnv([rootEnv, kogitoSwfBuilderEnv, kogitoSwfDevModeEnv], {
  vars: varsWithName({
    KOGITO_SERVERLESS_OPERATOR__registry: {
      default: "quay.io",
      description: "The image registry.",
    },
    KOGITO_SERVERLESS_OPERATOR__account: {
      default: "kiegroup",
      description: "The image registry account.",
    },
    KOGITO_SERVERLESS_OPERATOR__name: {
      default: "kogito-serverless-operator-nightly",
      description: "The image name.",
    },
    KOGITO_SERVERLESS_OPERATOR__buildTag: {
      default: "latest",
      description: "The image tag",
    },
    KOGITO_SERVERLESS_OPERATOR__kogitoSwfBuilderImage: {
      default: `${kogitoSwfBuilderEnv.env.kogitoSwfBuilder.registry}/${kogitoSwfBuilderEnv.env.kogitoSwfBuilder.account}/${kogitoSwfBuilderEnv.env.kogitoSwfBuilder.name}:${kogitoSwfBuilderEnv.env.kogitoSwfBuilder.tag}`,
      description: "Kogito SWF Builder image",
    },
    KOGITO_SERVERLESS_OPERATOR__kogitoSwfDevModeImage: {
      default: `${kogitoSwfDevModeEnv.env.kogitoSwfDevMode.registry}/${kogitoSwfDevModeEnv.env.kogitoSwfDevMode.account}/${kogitoSwfDevModeEnv.env.kogitoSwfDevMode.name}:${kogitoSwfDevModeEnv.env.kogitoSwfDevMode.tag}`,
      description: "Kogito SWF DevMode image",
    },
  }),
  get env() {
    return {
      kogitoServerlessOperator: {
        registry: getOrDefault(this.vars.KOGITO_SERVERLESS_OPERATOR__registry),
        account: getOrDefault(this.vars.KOGITO_SERVERLESS_OPERATOR__account),
        name: getOrDefault(this.vars.KOGITO_SERVERLESS_OPERATOR__name),
        tag: getOrDefault(this.vars.KOGITO_SERVERLESS_OPERATOR__buildTag),
        version: require("../package.json").version,
        kogitoSwfBuilderImage: getOrDefault(this.vars.KOGITO_SERVERLESS_OPERATOR__kogitoSwfBuilderImage),
        kogitoSwfDevModeImage: getOrDefault(this.vars.KOGITO_SERVERLESS_OPERATOR__kogitoSwfDevModeImage),
      },
    };
  },
});

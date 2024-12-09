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

const { getOrDefault, varsWithName, composeEnv } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    KIE_TOOLS_EXAMPLE__KIE_SANDBOX_COMMIT_MESSAGE_VALIDATION_SERVICE__port: {
      default: "8090",
      description: "HTTP server port where the service will run at.",
    },
    KIE_TOOLS_EXAMPLE__KIE_SANDBOX_COMMIT_MESSAGE_VALIDATION_SERVICE__validators: {
      default: "Length:5-72;IssuePrefix:kie-issues#*",
      description:
        "Enables and configures validators. The value is a list of `;` separated validators that are parameterized with anything after `:`.",
    },
  }),
  get env() {
    return {
      kieSandboxCommitMessageValidationServiceExample: {
        port: getOrDefault(this.vars.KIE_TOOLS_EXAMPLE__KIE_SANDBOX_COMMIT_MESSAGE_VALIDATION_SERVICE__port),
        validators: getOrDefault(
          this.vars.KIE_TOOLS_EXAMPLE__KIE_SANDBOX_COMMIT_MESSAGE_VALIDATION_SERVICE__validators
        ),
      },
    };
  },
});

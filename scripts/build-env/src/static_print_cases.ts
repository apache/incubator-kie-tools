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

import { LOGS } from "./console_logs";

export function treatStaticPrintCases({ opt }: { opt: string }) {
  //help
  if (opt === "--help") {
    console.log(
      `
build-env is a tool to manage environment variables inside packages. 
It maps environment variables into a JSON object and makes them accessible via JSON paths byparsing 'env/index.js' files exporting an object of type 'EnvAndVarsWithName'.

Usage:
        build-env my.custom.property
            Prints a value.

        build-env my.custom.bool --not
            Prints a negated boolean. Will error out if value is different than 'true' or 'false'.
or
        build-env --print-vars
            Prints the env var names.
        build-env --print-vars:self
            Same as above, but ignores composition with other envs.

        build-env --print-env-json
            Prints the JSON object for this env.
        build-env --print-env-json:self
            Same as above, but ignores composition with other envs.

        build-env --print-dotenv
            Prints variables assigned to values in .env format.
        build-env --print-dotenv:self
            Same as above, but ignores composition with other envs.

        build-env --generate-empty-env-index-js
            Generates an empty env/index.js file with the correct structure and imports.

        build-env --help
            Prints this message.
`.trim()
    );
    process.exit(0);
  }

  // generate empty env/index.js
  if (opt === "--generate-empty-env-index-js") {
    console.log(
      `
const { varsWithName, composeEnv, getOrDefault, str2bool } = require("@kie-tools-scripts/build-env");

module.exports = composeEnv([require("@kie-tools/root-env/env")], {
  vars: varsWithName({
    MY_CUSTOM_PROPERTY: {
      default: "true",
      description: "My custom property"
    }
  }),
  get env() {
    return { my: { custom: { property: str2bool(getOrDefault(this.vars.MY_CUSTOM_PROPERTY)) } } };
  },
});
`.trim()
    );
    process.exit(0);
  }
}

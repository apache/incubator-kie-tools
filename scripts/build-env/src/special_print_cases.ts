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

import { flattenObj, parseVarsForDotEnvPrint } from "./lib";
import { EnvAndVarsWithName } from "./types";

export function treatSpecialPrintCases({
  opt,
  vars,
  self,
  env,
}: {
  opt: string;
  vars: EnvAndVarsWithName<any>["vars"];
  self: EnvAndVarsWithName<any>["self"];
  env: EnvAndVarsWithName<any>["env"];
}) {
  // vars
  if (opt === "--print-vars") {
    console.log(Object.keys(vars ?? {}).join("\n"));
    process.exit(0);
  } else if (opt === "--print-vars:self") {
    console.log(Object.keys(self.vars ?? {}).join("\n"));
    process.exit(0);
  }

  // env json
  if (opt === "--print-env-json") {
    console.log(JSON.stringify(flattenObj(env), undefined, 2));
    process.exit(0);
  } else if (opt === "--print-env-json:self") {
    console.log(JSON.stringify(flattenObj(self.env), undefined, 2));
    process.exit(0);
  }

  // dotenv
  if (opt === "--print-dotenv") {
    const flattenedParsedVars = parseVarsForDotEnvPrint(vars);
    let envFile = "";
    for (const key of Object.keys(flattenedParsedVars)) {
      envFile += `${key}=${flattenedParsedVars[key]}\n`;
    }
    console.log(envFile);
    process.exit(0);
  } else if (opt === "--print-dotenv:self") {
    const flattenedParsedVars = parseVarsForDotEnvPrint(self.vars);
    let envFile = "";
    for (const key of Object.keys(flattenedParsedVars)) {
      envFile += `${key}=${flattenedParsedVars[key]}\n`;
    }
    console.log(envFile);
    process.exit(0);
  }
}

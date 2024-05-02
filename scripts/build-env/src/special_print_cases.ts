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

import { env } from "process";
import { flattenObj, parseVars } from "./lib";
import { EnvAndVarsWithName } from "./types";

export function treatSpecialPrintCases({
  opt,
  vars,
  self,
}: {
  opt: string;
  vars: EnvAndVarsWithName<any>["vars"];
  self: EnvAndVarsWithName<any>["self"];
}) {
  if (opt === "--print-vars") {
    console.log(JSON.stringify(flattenObj(parseVars(vars)), undefined, 2));
    process.exit(0);
  }

  if (opt === "--print-env") {
    console.log(JSON.stringify(flattenObj(env), undefined, 2));
    process.exit(0);
  }

  if (opt === "--print-env-file") {
    const flattenedParsedVars = flattenObj(parseVars(vars));
    let envFile = "";
    for (const key of Object.keys(flattenedParsedVars)) {
      envFile += `${key}=${flattenedParsedVars[key]}\n`;
    }
    console.log(envFile);
    process.exit(0);
  }

  if (opt === "--print-vars:self") {
    console.log(JSON.stringify(flattenObj(parseVars(self.vars)), undefined, 2));
    process.exit(0);
  }

  if (opt === "--print-env:self") {
    console.log(JSON.stringify(flattenObj(self.env), undefined, 2));
    process.exit(0);
  }

  if (opt === "--print-env-file:self") {
    const flattenedParsedVars = flattenObj(parseVars(self.vars));
    let envFile = "";
    for (const key of Object.keys(flattenedParsedVars)) {
      envFile += `${key}=${flattenedParsedVars[key]}\n`;
    }
    console.log(envFile);
    process.exit(0);
  }
}

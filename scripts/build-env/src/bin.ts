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

import * as path from "path";

import { findEnv, flattenObj, logs, parseVars } from "./index";

async function main() {
  const { env, vars, self } = await findEnv(path.resolve("."), path.resolve("."));

  const opt = process.argv[2];
  const flags = process.argv[3];

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

  const propertyPath = opt;
  if (!propertyPath) {
    console.error(logs.pleaseProvideEnvPropertyPath());
    console.error(logs.seeAllEnvProperties());
    process.exit(1);
  }

  let envPropertyValue: any = env;
  for (const p of propertyPath.split(".")) {
    envPropertyValue = envPropertyValue[p];
    if (envPropertyValue === undefined) {
      console.error(logs.propertyNotFound(propertyPath));
      console.error(logs.seeAllEnvProperties());
      process.exit(1);
    }
  }

  if (
    typeof envPropertyValue !== "string" &&
    typeof envPropertyValue !== "boolean" &&
    typeof envPropertyValue !== "number"
  ) {
    console.error(logs.cantReturnNonString(propertyPath, typeof envPropertyValue));
    console.error(envPropertyValue);
    process.exit(1);
  }

  if (flags === "--not") {
    const isBoolean = `${envPropertyValue}` === "true" || `${envPropertyValue}` === "false";
    if (isBoolean) {
      console.log(!(`${envPropertyValue}` === "true"));
      process.exit(0);
    } else {
      console.error(logs.cantNegateNonBoolean(envPropertyValue));
      process.exit(1);
    }
  }

  console.log(envPropertyValue);
}

main();

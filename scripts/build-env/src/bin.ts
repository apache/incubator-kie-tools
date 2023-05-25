/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as fs from "fs";
import * as path from "path";

import { EnvAndVarsWithName, treatVarToPrint, VarWithName } from "./index";

const BUILD_ENV_RECURSION_STOP_FILE_NAME = ".build-env-root";

const logs = {
  envNotFound: (envPath: string) => {
    return `[build-env] env not found at '${envPath}'`;
  },
  envFound: (envPath: string) => {
    return `[build-env] found env at '${envPath}'`;
  },
  envLoadingError: (envPath: string) => {
    return `[build-env] error loading env at '${envPath}'`;
  },
  envRecursionStopped: (startDir: string, curDir: string, envRecursionStopPath: string) => {
    return `[build-env] Couldn't load env from '${startDir}' to '${curDir}'. Stopped at '${envRecursionStopPath}'`;
  },
  cantNegateNonBoolean(envPropertyValue: string | boolean | number) {
    return `[build-env] Cannot negate non-boolean value '${envPropertyValue}'`;
  },
  cantReturnNonString(propertyPath: string, propertyType: string) {
    return `[build-env] Env property '${propertyPath}' is not of type "string", "number", or "boolean". Found "${propertyType}":`;
  },
  pleaseProvideEnvPropertyPath() {
    return `[build-env] Please provide an env property path.`;
  },
  seeAllEnvProperties() {
    return `[build-env] See all env properties with 'build-env --print-env'`;
  },
  propertyNotFound(propertyPath: string) {
    return `[build-env] Env property '${propertyPath}' not found.`;
  },
};

async function requireEnv(curDir: string): Promise<EnvAndVarsWithName<any> | undefined> {
  const envPathJS = path.resolve(curDir, "env", "index.js");
  const envPathCJS = path.resolve(curDir, "env", "index.cjs");
  const envPathJSExist = fs.existsSync(envPathJS);
  const envPathCJSExist = fs.existsSync(envPathCJS);
  const envPath = envPathJSExist ? envPathJS : envPathCJS;

  if (!envPathJSExist && !envPathCJSExist) {
    // console.debug(logs.envNotFound(envPath));
    return undefined;
  }

  // console.debug(logs.envFound(envPath));

  try {
    return (await import(envPath)) as EnvAndVarsWithName<any>;
  } catch (e) {
    console.info(logs.envLoadingError(envPath));
    throw e;
  }
}

async function findEnv(startDir: string, curDir: string): Promise<EnvAndVarsWithName<any>> {
  const env = await requireEnv(curDir);
  if (env) {
    return env;
  }

  const envRecursionStopPath = path.resolve(curDir, BUILD_ENV_RECURSION_STOP_FILE_NAME);
  if (fs.existsSync(envRecursionStopPath)) {
    console.info(logs.envRecursionStopped(startDir, curDir, envRecursionStopPath));
    process.exit(1);
  }

  return findEnv(startDir, path.dirname(curDir));
}

//

function parseVars<T>(vars: { [K in keyof T]: VarWithName }) {
  const result: Record<string, string | undefined> = {};
  for (const v in vars) {
    result[v] = treatVarToPrint(vars[v]);
  }
  return result;
}

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

function flattenObj(obj: any, parent: any = undefined, res: any = {}): any {
  for (const key in obj) {
    const propName = parent ? parent + "." + key : key;
    if (typeof obj[key] == "object") {
      flattenObj(obj[key], propName, res);
    } else {
      res[propName] = obj[key];
    }
  }
  return res;
}

main();

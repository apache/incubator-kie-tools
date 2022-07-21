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

import { EnvAndVarsWithName, getOrDefault } from "./index";

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
  cantNegateNonBoolean(configPropertyValue: string) {
    return `[build-env] Cannot negate non-boolean value '${configPropertyValue}'`;
  },
  pleaseProvideConfigPropertyPath() {
    return `[build-env] Please provide a config property path.`;
  },
  seeAllConfigs() {
    return `[build-env] See all config properties with 'build-env --print-config'`;
  },
  propertyNotFound(propertyPath: string) {
    return `[build-env] Config property '${propertyPath}' not found.`;
  },
  printEnvTitle() {
    return "[build-env] CLI-accessible env:";
  },
  printVarsTitle() {
    return "[build-env] Environment variables:";
  },
};

async function requireEnv(curDir: string): Promise<EnvAndVarsWithName<any> | undefined> {
  const envPath = path.resolve(curDir, "env", "index.js");

  if (!fs.existsSync(envPath)) {
    console.debug(logs.envNotFound(envPath));
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

async function main() {
  const { env, vars } = await findEnv(path.resolve("."), path.resolve("."));

  const opt = process.argv[2];
  const flags = process.argv[3];

  if (opt === "--print-vars") {
    const result: Record<string, string | undefined> = {};

    for (const v in vars) {
      result[v] = getOrDefault(vars[v]);
      if (vars[v].default === undefined && result[v]) {
        result[v] += " <- CHANGED 👀️ ";
      } else if (result[v] === undefined) {
        result[v] = "[unset] Default value may vary ⚠️ ";
      } else if (result[v] !== vars[v].default) {
        result[v] += " <- CHANGED 👀️ ";
      }
    }

    console.log(logs.printVarsTitle());
    console.log(JSON.stringify(flattenObj(result), undefined, 2));
    process.exit(0);
  }

  if (opt === "--print-env") {
    console.log(logs.printEnvTitle());
    console.log(JSON.stringify(flattenObj(env), undefined, 2));
    process.exit(0);
  }

  const propertyPath = opt;
  if (!propertyPath) {
    console.error(logs.pleaseProvideConfigPropertyPath());
    console.error(logs.seeAllConfigs());
    process.exit(1);
  }

  let configPropertyValue: any = env;
  for (const p of propertyPath.split(".")) {
    configPropertyValue = configPropertyValue[p];
    if (configPropertyValue === undefined || typeof configPropertyValue === "function") {
      console.error(logs.propertyNotFound(propertyPath));
      console.error(logs.seeAllConfigs());
      process.exit(1);
    }
  }

  if (flags === "--not") {
    const isBoolean = `${configPropertyValue}` === "true" || `${configPropertyValue}` === "false";
    if (isBoolean) {
      console.log(!(`${configPropertyValue}` === "true"));
      process.exit(0);
    } else {
      console.error(logs.cantNegateNonBoolean(configPropertyValue));
      process.exit(0);
    }
  }

  console.log(configPropertyValue);
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

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

import { EnvAndVarsWithName, VarWithName } from "./types";
import { getOrDefault } from "./index";
import { LOGS } from "./console_logs";

import * as path from "path";
import * as fs from "fs";

export const ERROR_ACCESS_LOG_FILE_ABSOLUTE_PATH_ENV_VAR_NAME = "BUILD_ENV__accessErrorsLogFileAbsolutePath";
export const BUILD_ENV_RECURSION_STOP_FILE_NAME = ".build-env-root";

export async function requireEnv(curDir: string): Promise<EnvAndVarsWithName<any> | undefined> {
  const envPathJs = path.resolve(curDir, "env", "index.js");
  const envPathCjs = path.resolve(curDir, "env", "index.cjs");

  const envPathJsExists = fs.existsSync(envPathJs);
  const envPathCjsExists = fs.existsSync(envPathCjs);
  if (!envPathJsExists && !envPathCjsExists) {
    // console.debug(logs.error.envNotFound({envPath}));
    return undefined;
  }

  const envFilePath = envPathJsExists ? envPathJs : envPathCjs;
  // console.debug(logs.debug.envFound({envPath}));

  try {
    return (await import(envFilePath)) as EnvAndVarsWithName<any>;
  } catch (e) {
    console.error(LOGS.error.envFileLoadingError({ envFilePath }));
    console.error(e);
    throw new Error("[build-env] env-loading-error");
  }
}

export async function findEnv(startDir: string, curDir: string): Promise<EnvAndVarsWithName<any>> {
  const env = await requireEnv(curDir);
  if (env) {
    return env;
  }

  const envRecursionStopPath = path.resolve(curDir, BUILD_ENV_RECURSION_STOP_FILE_NAME);
  if (fs.existsSync(envRecursionStopPath)) {
    console.error(LOGS.error.findEnvRecursionStopped({ startDir, curDir, envRecursionStopPath }));
    throw new Error("[build-env] env-loading-error");
  }

  return findEnv(startDir, path.dirname(curDir));
}

export function treatVarToPrint<T>(varr: VarWithName) {
  let value = getOrDefault(varr);
  if (varr.default === undefined && value) {
    value += " <- CHANGED 👀️ ";
  } else if (value === undefined) {
    value = "[unset] Default value may vary ⚠️ ";
  } else if (value !== varr.default) {
    value += " <- CHANGED 👀️ ";
  }
  return value;
}

export function parseVars<T>(vars: { [K in keyof T]: VarWithName }) {
  const result: Record<string, string | undefined> = {};
  for (const v in vars) {
    result[v] = treatVarToPrint(vars[v]);
  }
  return result;
}

export function flattenObj(
  obj: Record<string, any>,
  parent: any = undefined,
  res: Record<string, any> = {}
): Record<string, any> {
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

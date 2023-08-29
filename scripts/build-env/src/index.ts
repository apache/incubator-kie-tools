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

import * as fs from "fs";
import * as path from "path";

export type EnvAndVarsWithName<T> = { vars: { [K in keyof T]: VarWithName }; env: object; self: EnvAndVarsWithName<T> };

export type Var = {
  default: string | undefined;
  description: string;
};

export type VarWithName = Var & { name: string };

//

export function str2bool(str: string | undefined) {
  return str === "true";
}

export function get(envVar: VarWithName) {
  return process.env[envVar.name];
}

export function getOrDefault(envVar: VarWithName) {
  return get(envVar) ?? envVar.default;
}

export function varsWithName<T>(obj: { [K in keyof T]: Var }) {
  [...Object.keys(obj)].forEach((key) => {
    (obj[key as keyof T] as VarWithName)["name"] = key;
  });

  return obj as { [K in keyof T]: VarWithName };
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

function detectConflictingProps<T>(acc: object, d: object | EnvAndVarsWithName<T>) {
  const accProps = new Set(Object.keys(acc));
  return Object.keys(d).filter((a) => accProps.has(a));
}

export function composeEnv<T>(deps: EnvAndVarsWithName<any>[], self: EnvAndVarsWithName<T>) {
  // this avoids transitive dependencies coming from env deps
  // and respects the order of "import"
  const selfEnvs = [...deps.map((s) => s.self), self];

  return {
    vars: selfEnvs.reduce((acc, d) => {
      const conflictingProps = detectConflictingProps(acc, d.vars);
      if (conflictingProps.length > 0) {
        console.error("[build-env] ERROR: Found conflicting vars.");
        console.error(conflictingProps);
        console.error("[build-env] Done.");
        process.exit(1);
      }
      return { ...acc, ...d.vars };
    }, {}),
    env: selfEnvs.reduce((acc, d) => {
      const conflictingProps = detectConflictingProps(acc, d.env);
      if (conflictingProps.length > 0) {
        console.error("[build-env] ERROR: Found conflicting root properties.");
        console.error(conflictingProps);
        console.error("[build-env] Done.");
        process.exit(1);
      }

      return { ...acc, ...d.env };
    }, {}),
    self,
  };
}

const BUILD_ENV_RECURSION_STOP_FILE_NAME = ".build-env-root";

export const logs = {
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

export async function requireEnv(curDir: string): Promise<EnvAndVarsWithName<any> | undefined> {
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

export async function findEnv(startDir: string, curDir: string): Promise<EnvAndVarsWithName<any>> {
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

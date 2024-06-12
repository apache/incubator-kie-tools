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
import { VarWithName, Var, EnvAndVarsWithName } from "./types";

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

export function composeEnv<T>(deps: EnvAndVarsWithName<any>[], self: EnvAndVarsWithName<T>) {
  // this avoids transitive dependencies coming from env deps
  // and respects the order of "import"
  const selfEnvs = [...deps.map((s) => s.self), self];

  return {
    vars: selfEnvs.reduce((acc, d) => {
      const conflictingProps = detectConflictingProps(acc, d.vars);
      if (conflictingProps.length > 0) {
        console.error(LOGS.error.foundConflictingVars());
        console.error(conflictingProps);
        throw new Error("[build-env] conflicting-vars-error");
      }
      return { ...acc, ...d.vars };
    }, {}),
    env: selfEnvs.reduce((acc, d) => {
      const conflictingProps = detectConflictingProps(acc, d.env);
      if (conflictingProps.length > 0) {
        console.error(LOGS.error.foundConflictingRootProps());
        console.error(conflictingProps);
        throw new Error("[build-env] conflicting-root-props-error");
      }

      return { ...acc, ...d.env };
    }, {}),
    self,
  };
}

function detectConflictingProps<T>(acc: object, d: object | EnvAndVarsWithName<T>) {
  const accProps = new Set(Object.keys(acc));
  return Object.keys(d).filter((a) => accProps.has(a));
}

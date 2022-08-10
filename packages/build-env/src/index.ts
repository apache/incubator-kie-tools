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

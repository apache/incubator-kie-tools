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

export type EnvAndVarsWithName<T> = { vars: { [K in keyof T]: VarWithName }; env: object };

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

export function envVars<T>(obj: { [K in keyof T]: Var }) {
  [...Object.keys(obj)].forEach((key) => {
    (obj[key as keyof T] as VarWithName)["name"] = key;
  });

  return obj as { [K in keyof T]: VarWithName };
}

export function compositeEnv<T>(deps: EnvAndVarsWithName<any>[], obj: EnvAndVarsWithName<T>) {
  return {
    vars: { ...obj.vars, ...deps.reduce((acc, d) => ({ ...acc, ...d.vars }), {}) },
    env: { ...obj.env, ...deps.reduce((acc, d) => ({ ...acc, ...d.env }), {}) },
  };
}

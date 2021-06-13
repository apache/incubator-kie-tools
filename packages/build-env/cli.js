#!/usr/bin/env node

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const buildEnv = require("./index");

function main() {
  const path = process.argv[2];
  const opt = process.argv[2];

  if (opt === "--print-vars") {
    const result = {};
    const vars = buildEnv.vars().ENV_VARS;

    for (const v in vars) {
      result[vars[v].name] = buildEnv.vars().getOrDefault(vars[v]);
      if (result[vars[v].name] === undefined) {
        result[vars[v].name] = "[unset] ⚠️ ";
      } else if (result[vars[v].name] !== vars[v].default) {
        result[vars[v].name] += " <- CHANGED 👀️ ";
      }
    }

    console.log("[build-env] Environment variables:");
    console.log(JSON.stringify(flattenObj(result), undefined, 2));
    process.exit(0);
  }

  if (opt === "--print-config") {
    console.log("[build-env] CLI-accessible config:");
    console.log(JSON.stringify(flattenObj(buildEnv), undefined, 2));
    process.exit(0);
  }

  if (!path) {
    console.error("Please an option.");
    process.exit(1);
  }

  let prop = buildEnv;
  for (const p of path.split(".")) {
    prop = prop[p];
    if (prop === undefined || typeof prop === "function") {
      console.error(`Property '${path}' not found.`);
      process.exit(1);
    }
  }

  console.log(prop);
}

function flattenObj(obj, parent, res = {}) {
  for (const key in obj) {
    let propName = parent ? parent + "." + key : key;
    if (typeof obj[key] == "object") {
      flattenObj(obj[key], propName, res);
    } else {
      res[propName] = obj[key];
    }
  }
  return res;
}

main();

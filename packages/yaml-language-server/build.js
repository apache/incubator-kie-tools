/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { fileURLToPath } from "url";
import { build } from "esbuild";

/* TODO: Remove the esbuild dependency in yaml-language-server (https://issues.redhat.com/browse/KOGITO-8221) */
await build({
  entryPoints: ["src/index.ts"],
  bundle: true,
  external: [],
  logLevel: "info",
  outdir: "./dist",
  sourcemap: true,
  format: "esm",
  target: "es6",
  plugins: [
    {
      name: "alias",
      setup({ onResolve, resolve }) {
        onResolve({ filter: /^vscode-languageserver.*/ }, (args) => ({
          path: args.path,
          external: true,
          sideEffects: false,
        }));
        onResolve({ filter: /^ajv$/ }, () => ({
          path: fileURLToPath(new URL("src/ajv.ts", import.meta.url)),
        }));
        onResolve({ filter: /^path$/ }, () => ({
          path: "path-browserify",
          external: true,
          sideEffects: false,
        }));
        onResolve({ filter: /^prettier/ }, (args) => ({
          path: args.path,
          external: true,
          sideEffects: false,
        }));
        onResolve({ filter: /\/umd\// }, ({ path, ...options }) => resolve(path.replace(/\/umd\//, "/esm/"), options));
        onResolve({ filter: /.*/ }, () => ({ sideEffects: false }));
      },
    },
  ],
});

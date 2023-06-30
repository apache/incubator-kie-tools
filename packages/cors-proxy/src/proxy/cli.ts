/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import yargs from "yargs";
import { hideBin } from "yargs/helpers";
import { startServer } from "./server";

export const run = async () => {
  const bins = hideBin(process.argv);
  const argv = await yargs(bins)
    .version(false)
    .scriptName("")
    .usage("Usage index.js -p [port] -o [origin string] -v")
    .options({
      p: {
        alias: "port",
        type: "number",
        default: 8080,
        describe: "Port to listen to",
        nargs: 1,
      },
      o: {
        alias: "origin",
        type: "string",
        default: "*",
        describe: "Origin to use in the 'Access-Control-Allow-Origin' http header.",
      },
      v: {
        alias: "verbose",
        type: "boolean",
        default: false,
        describe: "Verbose mode",
        nargs: 0,
      },
    })
    .showHelpOnFail(false)
    .strict()
    .check((argv, env) => {
      if (!argv.p) {
        throw new Error("Port argument (-p) must be a number");
      }
      if (typeof argv.v !== "boolean") {
        throw new Error("Verbose argument (-v) must be a boolean");
      }
      return true;
    }).argv;

  startServer({
    port: argv.p,
    origin: argv.o,
    verbose: argv.v,
  });
};

#!/usr/bin/env node

/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

const yargs = require("yargs/yargs");
const { hideBin } = require("yargs/helpers");
const { spawn } = require("child_process");

const argv = yargs(hideBin(process.argv)).options({
  env: {
    required: true,
    alias: "e",
    type: "string",
    description: "Environment variable name",
  },
  eq: {
    default: "true",
    alias: "equals",
    type: "string",
    description: "Value to be compared with the environment variable.",
  },
  command: {
    required: true,
    alias: "c",
    type: "string",
    description: "Command to execute if environment variable has the desired value",
  },
  "run-if-empty": {
    default: "true",
    description: "Runs the command if the environment variable is not set.",
  },
}).argv;

if (process.env[argv.env] && process.env[argv.env] !== argv.eq) {
  console.info(`Skipping '${argv.c}' because environment variable '${argv.env}' is not equal to '${argv.eq}'.`);
  process.exit(0);
}

if (!process.env[argv.env] && argv["run-if-empty"] !== "true") {
  console.info(`Skipping '${argv.c}' because environment variable '${argv.env}' is not set.`);
  process.exit(0);
}

if (!process.env[argv.env]) {
  console.info(`Running '${argv.c}'. Environment variable '${argv.env}' is not set.`);
} else {
  console.info(`Running '${argv.c}'. Environment variable '${argv.env}' is equal to '${argv.eq}'.`);
}

let commandBin = argv.c.split(" ")[0];
let commandArgs = argv.c.split(" ").slice(1);

const command = spawn(commandBin, commandArgs, { stdio: "inherit" });
command.on("error", (data) => {
  console.error(`Error executing '${argv.c}':`);
  console.error(data.toString());
});

command.on("exit", (code) => {
  process.exit(code);
});

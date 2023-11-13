#!/usr/bin/env node

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

import { execSync, spawnSync } from "child_process";
import * as yargs from "yargs";
import { hideBin } from "yargs/helpers";

function shell() {
  return process.platform === "win32" ? { shell: "powershell.exe" } : {};
}

function evalStringArg(arg: never | string | number) {
  let ret;
  if (process.platform === "win32" && arg && typeof arg === "string" && arg.startsWith("$")) {
    const output = spawnSync(arg, [], { stdio: "pipe", ...shell() });
    ret = String(output.stdout).trim();
  } else {
    ret = arg;
  }

  return ret;
}

async function main() {
  const args = await yargs(hideBin(process.argv))
    .version(false)
    .scriptName("")
    .wrap(Math.min(150, yargs.terminalWidth()))
    .scriptName("@kie-tools/image-builder")
    .epilog(
      `
CLI tool to help building container images using build variables and different engines on different OSes.
    `
    )
    .example(
      `$ image-builder --registry "$(build-env myCustomEnv.registry)" --account "$(build-env myCustomEnv.account)" --name "$(build-env myCustomEnv.name)" --tags "$(build-env myCustomEnv.buildTags)" --engine docker --push`,
      "Build an image using parameters from your myCustomEnv build env variables"
    )
    .options({
      registry: {
        alias: "r",
        demandOption: false,
        describe: "The string for the image registry",
        type: "string",
        nargs: 1,
        coerce: evalStringArg,
      },
      account: {
        alias: "a",
        demandOption: false,
        describe: "The string for the image account",
        type: "string",
        nargs: 1,
        coerce: evalStringArg,
      },
      name: {
        alias: "n",
        demandOption: true,
        describe: "The string for the image name",
        type: "string",
        nargs: 1,
        coerce: evalStringArg,
      },
      tags: {
        alias: "t",
        demandOption: true,
        describe: "The string for the image tags",
        type: "string",
        nargs: 1,
        coerce: evalStringArg,
      },
      engine: {
        alias: "e",
        demandOption: false,
        default: "docker",
        describe: "The build engine to be used",
        type: "string",
        nargs: 1,
        choices: ["docker", "podman"] as const,
      },
      push: {
        alias: "p",
        demandOption: "false",
        default: false,
        describe: "Push the image to the registry",
        type: "boolean",
      },
      containerfile: {
        alias: "f",
        demandOption: "false",
        default: "Containerfile",
        describe: "Path to the Containerfile/Dockerfile",
        type: "string",
        nargs: 1,
      },
      context: {
        alias: "c",
        demandOption: "false",
        default: "./",
        describe: "Path to the build context",
        type: "string",
        nargs: 1,
      },
      "build-arg": {
        demandOption: false,
        describe:
          "Build args for the builder in the format '<arg>=<value>', where <value> is a string (Can be used multiple times)",
        type: "array",
        default: [],
        coerce: (args) => {
          const regex = new RegExp(/(.*=.*)+/);
          const results = args.map((arg: string) => regex.test(arg.toString().trim()));
          if (!results.every(Boolean)) {
            throw new Error(
              `ERROR! --build-arg: Invalid build argument supplied ("${args.join(
                " "
              )}"). Use the format 'var1=value1 var2=value2 ...'`
            );
          }
          return args;
        },
      },
    })
    .alias("h", "help")
    .parse();

  const imageFullNameWithoutTags = `${args.registry ? `${args.registry}/` : ""}${
    args.account ? `${args.account}/` : ""
  }${args.name}`;

  const imageFullNames = (args.tags as string).split(" ").map((tag) => `${imageFullNameWithoutTags}:${tag}`);

  const buildCommand = `${args.engine} build ${args.push ? "--push" : ""} ${imageFullNames
    .map((fullName) => `-t ${fullName}`)
    .join(" ")} ${args.buildArg.map((arg: string) => `--build-arg ${arg}`).join(" ")} ${args.context} -f ${
    args.containerfile
  }`;

  try {
    execSync(`command ${process.platform === "win32" ? "" : "-v"} ${args.engine}`, { stdio: "inherit", ...shell() });
  } catch (e) {
    console.log(`Build engine "${args.engine}" not available. Skipping build!`);
    return;
  }

  execSync(buildCommand, { stdio: "inherit" });
}

main();

#!/usr/bin/env node

/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import * as path from "path";
import { execSync } from "child_process";
import { findEnv, flattenObj } from "@kie-tools-scripts/build-env";
import * as yargs from "yargs";
import { hideBin } from "yargs/helpers";

async function main() {
  const currentPath = path.resolve(".") !== path.resolve(__dirname, "..") ? "." : "../root-env";
  const { env } = await findEnv(path.resolve(currentPath), path.resolve(currentPath));
  const flattenedEnv = flattenObj(env);

  const parseBuildEnvPath = (path: string) => {
    if (!flattenedEnv[path]) {
      return path;
    }
    return flattenedEnv[path];
  };

  const args = yargs(hideBin(process.argv))
    .version(false)
    .scriptName("")
    .wrap(Math.min(150, yargs.terminalWidth()))
    .options({
      registry: {
        alias: "r",
        demandOption: false,
        describe: "The build-env path or string for the image registry",
        type: "string",
        nargs: 1,
        coerce: parseBuildEnvPath,
      },
      account: {
        alias: "a",
        demandOption: false,
        describe: "The build-env path or string for the image account",
        type: "string",
        nargs: 1,
        coerce: parseBuildEnvPath,
      },
      name: {
        alias: "n",
        demandOption: true,
        describe: "The build-env path or string for the image name",
        type: "string",
        nargs: 1,
        coerce: parseBuildEnvPath,
      },
      tags: {
        alias: "t",
        demandOption: true,
        describe: "The build-env path or string for the image tags",
        type: "string",
        nargs: 1,
        coerce: parseBuildEnvPath,
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
        nargs: 1,
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
          "Build args for the builder in the format '<arg>=<value>', where <value> can be a string or a build-env path (Can be used multiple times)",
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
          const replacedArgs = args.map((arg: string) => {
            const [key, value] = arg.split("=");
            const replacedValue = parseBuildEnvPath(value);
            return `${key}=${replacedValue}`;
          });
          return replacedArgs;
        },
      },
    })
    .alias("h", "help")
    .parseSync();

  const imageFullNameWithoutTags = `${args.registry ? `${args.registry}/` : ""}${
    args.account ? `${args.account}/` : ""
  }${args.name}`;

  const imageFullNames = (args.tags as string).split(" ").map((tag) => `${imageFullNameWithoutTags}:${tag}`);

  const buildCommand = `${args.engine} build ${imageFullNames
    .map((fullName) => `-t ${fullName}`)
    .join(" ")} ${args.buildArg.map((arg: string) => `--build-arg ${arg}`).join(" ")} ${args.context} -f ${
    args.containerfile
  }`;

  try {
    process.platform === "win32"
      ? execSync(`command ${args.engine}`, { shell: "powershell", stdio: "inherit" })
      : execSync(`command -v ${args.engine}`, { stdio: "inherit" });
  } catch (e) {
    console.log(`Build engine "${args.engine}" not available. Skipping build!`);
    return;
  }

  execSync(buildCommand, { stdio: "inherit" });
}

main();

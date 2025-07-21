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
import yargs from "yargs/yargs";
import { terminalWidth, exit } from "yargs";
import { hideBin } from "yargs/helpers";

type ArgsType = {
  engine: string;
  context: string;
  containerfile: string;
  registry?: string;
  account?: string;
  name: string;
  tags: string[];
  push: boolean;
  buildArg: string[];
  arch: string;
  allowHostNetworkAccess: boolean;
};

function shell() {
  return process.platform === "win32" ? { shell: "powershell.exe" } : {};
}

function evalStringArg<T>(arg: never | string | number) {
  let ret;
  if (arg && typeof arg === "string" && arg.startsWith("$(") && arg.endsWith(")")) {
    const output = spawnSync(arg, [], { stdio: "pipe", ...shell() });
    ret = String(output.stdout).trim();
  } else {
    ret = arg;
  }

  return ret as T;
}

function getImageFullNames(args: ArgsType) {
  const imageFullNameWithoutTags = `${args.registry ? `${args.registry}/` : ""}${
    args.account ? `${args.account}/` : ""
  }${args.name}`;

  return args.tags.map((tag) => `${imageFullNameWithoutTags}:${tag}`);
}

function createAndUseDockerBuilder(args: { allowHostNetworkAccess: boolean }) {
  try {
    console.info("[image-builder] Checking for existing kie-tools-builder...");
    execSync("docker buildx inspect kie-tools-builder", { stdio: "inherit" });
    console.info("[image-builder] kie-tools-builder found, using it.");
    execSync("docker buildx use kie-tools-builder", { stdio: "inherit" });
  } catch (e) {
    console.info("[image-builder] kie-tools-builder not found, creating it.");
    execSync(
      `docker buildx create
        --buildkitd-flags '--allow-insecure-entitlement network.host'
        --name kie-tools-builder
        --driver docker-container
        --bootstrap
        --use`
        .split("\n")
        .join(" "),
      {
        stdio: "inherit",
      }
    );
  }
}

function checkBuildEngine(args: ArgsType) {
  try {
    execSync(`command ${process.platform === "win32" ? "" : "-v"} ${args.engine}`, {
      stdio: "inherit",
      ...shell(),
    });
  } catch (e) {
    console.log(`[image-builder] Build engine "${args.engine}" not available. Skipping build!`);
    return;
  }
}

function buildArchImage(args: ArgsType & { arch: "arm64" | "amd64" }, imageFullNames: string[]) {
  const platform = {
    arm64: "linux/arm64",
    amd64: "linux/amd64",
  }[args.arch];

  createAndUseDockerBuilder({ allowHostNetworkAccess: args.allowHostNetworkAccess });
  console.log(`[image-builder] Building arch image ${args.arch}`);
  const buildPlatformCommand = `docker buildx build
    ${args.allowHostNetworkAccess ? "--allow network.host --network host" : ""}
    --progress=plain
    --load
    --platform ${platform}
    ${args.push ? "--push" : ""}
    ${imageFullNames.map((fullName) => `-t ${fullName}`).join(" ")}
    ${args.buildArg.map((arg) => `--build-arg ${arg}`).join(" ")}
    ${args.context}
    -f ${args.containerfile}`
    .split("\n")
    .join(" ");

  execSync(buildPlatformCommand, { stdio: "inherit" });
}

function buildNativeImage(args: ArgsType, imageFullNames: string[]) {
  console.log(`[image-builder] Building native image`);
  const buildNativeCommand = `${args.engine} build
    --progress=plain
    --load
    ${args.allowHostNetworkAccess ? "--allow network.host --network host" : ""}
    ${args.push ? "--push" : ""}
    ${imageFullNames.map((fullName) => `-t ${fullName}`).join(" ")}
    ${args.buildArg.map((arg) => `--build-arg ${arg}`).join(" ")}
    ${args.context}
    -f ${args.containerfile}`
    .split("\n")
    .join(" ");

  execSync(buildNativeCommand, { stdio: "inherit" });
}

function checkNotNativeArch(arch: ArgsType["arch"]): arch is "arm64" | "amd64" {
  return arch !== "native";
}

function buildImage(args: ArgsType, imageFullNames: string[]) {
  checkBuildEngine(args);

  const arch = args.arch;

  if (checkNotNativeArch(arch)) {
    buildArchImage({ ...args, arch }, imageFullNames);
  } else {
    buildNativeImage(args, imageFullNames);
  }
}

function createOpenShiftImageStream(imageName: string) {
  const contents = `<<EOF
apiVersion: image.openshift.io/v1
kind: ImageStream
metadata:
  name: ${imageName}
spec:
  lookupPolicy:
    local: true
EOF
  `;

  execSync(`oc apply -f - ${contents}`, { stdio: "inherit" });
}

function createOpenShfitBuildConfig(imageName: string, tag: string, containerfile: string, buildArgs: string[]) {
  const contents = `<<EOF
apiVersion: build.openshift.io/v1
kind: BuildConfig
metadata:
  name: ${imageName}
spec:
  output:
    to:
      kind: ImageStreamTag
      name: ${imageName}:${tag}
  strategy:
    dockerStrategy:
      dockerfilePath: ${containerfile}
      ${
        buildArgs?.length > 0
          ? `buildArgs: ${buildArgs
              .map((arg) => {
                const [key, value] = arg.split("=");
                return `
       - name: ${key}
         value: ${value}`;
              })
              .join("")}`
          : ""
      }
  source:
    type: Binary
    binary: {}
  resources:
    limits:
      memory: 4Gi
EOF
  `;

  execSync(`oc apply -f - ${contents}`, { stdio: "inherit" });
}

async function main() {
  function prettyPrintError(error: Error) {
    console.error("\x1b[31m[image-builder] %s\x1b[0m", error);
  }
  try {
    await yargs(hideBin(process.argv))
      .version(false)
      .scriptName("")
      .wrap(Math.min(150, terminalWidth()))
      .scriptName("@kie-tools/image-builder")
      .epilog(
        `
CLI tool to help building container images using build variables and different engines on different OSes.
Also useful to aid on developing images and pushing them to Kubernetes/OpenShift clusters.
    `
      )
      .example(
        `$ image-builder --registry "$(build-env myCustomEnv.registry)" --account "$(build-env myCustomEnv.account)" --name "$(build-env myCustomEnv.name)" --tags "$(build-env myCustomEnv.buildTag)" --engine docker --push`,
        "Build an image using parameters from your myCustomEnv build env variables"
      )
      .options({
        registry: {
          alias: "r",
          demandOption: false,
          describe: "The string for the image registry",
          type: "string",
          nargs: 1,
          coerce: (arg) => evalStringArg<string>(arg),
        },
        account: {
          alias: "a",
          demandOption: false,
          describe: "The string for the image account",
          type: "string",
          nargs: 1,
          coerce: (arg) => evalStringArg<string>(arg),
        },
        name: {
          alias: "n",
          demandOption: true,
          describe: "The string for the image name",
          type: "string",
          nargs: 1,
          coerce: (arg) => evalStringArg<string>(arg),
        },
        tags: {
          alias: "t",
          demandOption: true,
          describe: "The string for the image tags",
          type: "array",
          coerce: (tags) => {
            if (tags.length === 1) {
              const evaluedArgs = evalStringArg<string>(tags[0]);
              return evaluedArgs.split(" ") as string[];
            }
            return tags as string[];
          },
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
          demandOption: false,
          default: false,
          describe: "Push the image to the registry",
          type: "boolean",
        },
        allowHostNetworkAccess: {
          demandOption: false,
          default: false,
          describe: "Allows host network access during build",
          type: "boolean",
        },
        containerfile: {
          alias: "f",
          demandOption: false,
          default: "Containerfile",
          describe: "Path to the Containerfile/Dockerfile",
          type: "string",
          nargs: 1,
        },
        context: {
          alias: "c",
          demandOption: false,
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
          coerce: (buildArgs) => {
            const regex = new RegExp(/(.*=.*)+/);
            const results = buildArgs.map((arg: string) => regex.test(arg.toString().trim()));
            if (!results.every(Boolean)) {
              throw new Error(
                `ERROR! --build-arg: Invalid build argument supplied ("${buildArgs.join(
                  " "
                )}"). Use the format 'var1=value1 var2=value2 ...'`
              );
            }
            const evaluedBuildArgs = buildArgs.map((arg: string) => {
              const [key, value] = arg.split("=");
              return `${key}=${evalStringArg<string>(value)}`;
            });
            return evaluedBuildArgs;
          },
        },
        arch: {
          demandOption: false,
          describe: "The target build architecture. If not provided will default to the native architecture",
          type: "string",
          default: "native",
          choices: ["amd64", "arm64", "native"] as const,
          nargs: 1,
        },
      })
      .command(
        "build",
        "Builds the image locally and store it in your local Docker/Podman image registry",
        () => {},
        (args) => {
          console.info(`
[image-builder] Building local image.
    - registry: ${args.registry}
    - account: ${args.account}
    - name: ${args.name}
    - tags: ${args.tags}
    - containerfile: ${args.containerfile}
    - context: ${args.context}
    - buildArgs: ${args.buildArg.join(" ")}
    - engine: ${args.engine}
    - push: ${args.push}
    - allowHostNetworkAccess: ${args.allowHostNetworkAccess}
    - arch: linux/${args.arch}
        `);
          if (args.arch !== "native" && args.engine !== "docker") {
            throw new Error(
              `ERROR! --arch: Targetting non-native architecturies is only supported with the Docker engine.`
            );
          }
          const imageFullNames = getImageFullNames(args);
          buildImage(args, imageFullNames);
        }
      )
      .command(
        "minikube",
        "Builds the image locally and load it to your Minikube cluster",
        () => {},
        (args) => {
          console.info(`
[image-builder] Building local image and loading it to Minikube cluster.
    - registry: ${args.registry}
    - account: ${args.account}
    - name: ${args.name}
    - tags: ${args.tags}
    - containerfile: ${args.containerfile}
    - context: ${args.context}
    - buildArg: ${args.buildArg?.join(" ") ?? " - "}
    - engine: ${args.engine}
    - push: ${args.push}
    - allowHostNetworkAccess: ${args.allowHostNetworkAccess}
    - arch: linux/amd64`);

          if (args.engine !== "docker") {
            throw new Error(
              `ERROR! --engine: Loading images to a Kubernetes cluster is only supported on Docker builds.`
            );
          }

          const imageFullNames = getImageFullNames(args);
          buildImage({ ...args, arch: "amd64" }, imageFullNames);
          imageFullNames.forEach((imageName) => {
            execSync(`minikube image load ${imageName}`, { stdio: "inherit" });
          });
        }
      )
      .command(
        "kind",
        "Builds the image locally and load it to your Kind cluster",
        (yargs) => {
          return yargs.options({
            "kind-cluster-name": {
              demandOption: false,
              describe: "Your Kind cluster name. Used only when loading image into Kind cluster",
              type: "string",
              nargs: 1,
              default: "kind",
            },
          });
        },
        (args) => {
          console.info(`
[image-builder] Building local image and loading it to Kind cluster.
    - registry: ${args.registry}
    - account: ${args.account}
    - name: ${args.name}
    - tags: ${args.tags}
    - containerfile: ${args.containerfile}
    - context: ${args.context}
    - buildArg: ${args.buildArg?.join(" ") ?? " - "}
    - engine: ${args.engine}
    - push: ${args.push}
    - arch: linux/amd64
    - allowHostNetworkAccess: ${args.allowHostNetworkAccess} (ignored)
    - kindClusterName: ${args.kindClusterName}
        `);

          const clusters = execSync("kind get clusters").toString().split("\n");
          if (!clusters.includes(args.kindClusterName)) {
            throw new Error(
              `ERROR! --kind-cluster-name: Kind cluster named ${args.kindClusterName} not found. Are you sure it's correct?`
            );
          }

          if (args.engine !== "docker") {
            throw new Error(
              `ERROR! --engine: Loading images to a Kubernetes cluster is only supported on Docker builds.`
            );
          }

          const imageFullNames = getImageFullNames(args);
          buildImage({ ...args, arch: "amd64" }, imageFullNames);
          imageFullNames.forEach((imageName) => {
            execSync(`kind load docker-image ${imageName} --name ${args.kindClusterName}`, { stdio: "inherit" });
          });
        }
      )
      .command(
        "openshift",
        "Builds the image on the OpenShift cluster in an ImageStream",
        () => {},
        (args) => {
          console.info(`
[image-builder] Building image on OpenShift cluster.
    - registry: ${args.registry} (ignored)
    - account: ${args.account} (ignored)
    - name: ${args.name}
    - tags: ${args.tags}
    - containerfile: ${args.containerfile}
    - context: ${args.context}
    - buildArg: ${args.buildArg?.join(" ") ?? " - "}
    - engine: ${args.engine} (ignored)
    - push: ${args.push}
    - allowHostNetworkAccess: ${args.allowHostNetworkAccess} (ignored)
    - arch: linux/amd64
        `);

          try {
            createOpenShiftImageStream(args.name);
            args.tags.forEach((tag: string, index) => {
              if (index === 0) {
                createOpenShfitBuildConfig(args.name, tag, args.containerfile, args.buildArg);
                execSync(`oc start-build --from-dir=${args.context} ${args.name} --follow`, { stdio: "inherit" });
              } else {
                execSync(`oc tag ${args.name}:${args.tags[0]} ${args.name}:${tag}`);
              }
            });
          } catch (e) {
            throw new Error("-> Failed to create required resources. Are you logged in the 'oc' CLI?");
          }
        }
      )
      .alias("h", "help")
      .parse();
  } catch (e) {
    prettyPrintError(e);
    exit(1, e);
  }
}

main();

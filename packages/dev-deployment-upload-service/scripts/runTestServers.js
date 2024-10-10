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

const { execSync } = require("child_process");
const { argv } = require("process");
const version = require("../package.json").version;
const { env } = require("../env");

const network = "ddus-network";
const builder = "ddus-builder";

const containersNames = {
  fileserver: "ddus-fileserver",
  buildtimeInstall: "ddus-buildtime-install",
  runTimeInstall: "ddus-runtime-install",
};

const imagesNames = {
  fileserver: `${containersNames.fileserver}-image`,
  buildtimeInstall: `${containersNames.buildtimeInstall}-image`,
  runTimeInstall: `${containersNames.runTimeInstall}-image`,
};

const containersPorts = {
  fileserver: env.devDeploymentUploadService.dev.fileServerPort,
  buildtimeInstall: env.devDeploymentUploadService.dev.buildTimePort,
  runTimeInstall: env.devDeploymentUploadService.dev.runtTimePort,
};

function runCommand(command, returnResult = false) {
  console.log(`> ${command}`);
  return execSync(command, returnResult ? {} : { stdio: "inherit" });
}

function cleanup() {
  try {
    execSync(
      `docker stop ${containersNames.fileserver} && docker rm ${containersNames.fileserver} && docker image rm ${imagesNames.fileserver}`
    );
  } catch (e) {
    // nothing to do
  }
  try {
    execSync(
      `docker stop ${containersNames.buildtimeInstall} && docker rm ${containersNames.buildtimeInstall} && docker image rm ${imagesNames.buildtimeInstall}`
    );
  } catch (e) {
    // nothing to do
  }
  try {
    execSync(
      `docker stop ${containersNames.runTimeInstall} && docker rm ${containersNames.runTimeInstall} && docker image rm ${imagesNames.runTimeInstall}`
    );
  } catch (e) {
    // nothing to do
  }
  try {
    execSync(`docker builder rm ${builder}`);
  } catch (e) {
    // nothing to do
  }
  try {
    execSync(`docker network rm ${network}`);
  } catch (e) {
    // nothing to do
  }
}

if (argv[2] === "--cleanup") {
  cleanup();
  return;
}

try {
  console.info(`Checking existing ${network}...`);
  runCommand(`docker network inspect ${network}`);
  console.info("Netowork found!");
} catch (e) {
  console.info("Network not found. Creating it!");
  runCommand(`docker network create --ipv6=false ${network}`);
}

let fileServerIp;
try {
  console.info(`Starting File Server container: ${containersNames.fileserver}`);
  runCommand(
    `docker buildx build -t ${imagesNames.fileserver} --build-arg DDUS_VERSION=${version} --build-arg DDUS_FILESERVER_PORT=${containersPorts.fileserver} . -f ./dev/Containerfile.${containersNames.fileserver} --load`,
    { stdio: "inherit" }
  );
  runCommand(
    `docker run -d --name ${containersNames.fileserver} --network ${network} -p ${containersPorts.fileserver}:8090 ${imagesNames.fileserver}`,
    { stdio: "inherit" }
  );
  fileServerIp = runCommand(`docker exec ${containersNames.fileserver} awk 'END{print $1}' /etc/hosts`, true)
    .toString()
    .trim();
} catch (e) {
  cleanup();
  throw new Error(`Failed to build and start ${containersNames.fileserver}. Exiting!`);
}

try {
  console.info(`Creating docker builder: ${builder}`);
  runCommand(`docker buildx create --name ${builder} --driver docker-container --driver-opt network=${network}`, {
    stdio: "inherit",
  });
  runCommand("docker buildx ls", { stdio: "inherit" });
} catch (e) {
  cleanup();
  throw new Error(`Failed to create builder ${builder}. Exiting!`);
}

try {
  console.info(`Starting BuildTime Install container: ${containersNames.buildtimeInstall}`);
  runCommand(
    `docker buildx --builder ${builder} build -t ${imagesNames.buildtimeInstall} --build-arg DDUS_VERSION=${version} --build-arg DDUS_FILESERVER_PORT=8090 --build-arg DDUS_FILESERVER_IP=${fileServerIp} . -f ./dev/Containerfile.${containersNames.buildtimeInstall} --load`,
    { stdio: "inherit" }
  );
  runCommand(
    `docker run -d --name ${containersNames.buildtimeInstall} --network ${network} -p ${containersPorts.buildtimeInstall}:8091 ${imagesNames.buildtimeInstall}`,
    { stdio: "inherit" }
  );
} catch (e) {
  cleanup();
  throw new Error(`Failed to build and start ${containersNames.buildtimeInstall}. Exiting!`);
}

try {
  console.info(`Starting RunTime Install container: ${containersNames.runTimeInstall}`);
  runCommand(
    `docker buildx --builder ${builder} build -t ${imagesNames.runTimeInstall} . -f ./dev/Containerfile.${containersNames.runTimeInstall} --load`,
    { stdio: "inherit" }
  );
  runCommand(
    `docker run -d --name ${containersNames.runTimeInstall} --network ${network} -p ${containersPorts.runTimeInstall}:8092 -e DDUS_FILESERVER_IP=${fileServerIp} -e DDUS_VERSION=${version} -e DDUS_FILESERVER_PORT=8090 ${imagesNames.runTimeInstall}`,
    { stdio: "inherit" }
  );
} catch (e) {
  cleanup();
  throw new Error(`Failed to build and start ${containersNames.runTimeInstall}. Exiting!`);
}

runCommand("docker ps -f name=ddus", { stdio: "inherit" });

runCommand("sleep 10");

Object.values(containersNames).forEach((name) => {
  const logs = runCommand(`docker logs ${name}`, true).toString();
  console.info(`Checking logs for ${name}:`);
  console.info("--------------------------");
  console.info(logs);
  console.info("--------------------------");
});

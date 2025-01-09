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

const { execSync, execFileSync } = require("child_process");
const path = require("path");
const { env } = require("../env");
const version = require("../package.json").version;

const filePath = path.join(process.cwd(), "tests/test.zip");

const containersPorts = {
  fileserver: env.devDeploymentUploadService.dev.fileServerPort,
  buildtimeInstall: env.devDeploymentUploadService.dev.buildTimePort,
  runTimeInstall: env.devDeploymentUploadService.dev.runtTimePort,
};

const dockerInfo = JSON.parse(execSync(`docker info --format '{{ json . }}'`).toString().trim());
const platform = dockerInfo["OSType"];
const arch = dockerInfo["Architecture"] === "aarch64" ? "arm64" : "amd64";

describe("Test built images individually", () => {
  beforeAll(() => {
    execSync(
      `pnpm start-test-servers && wait-on -t 20m http://localhost:${containersPorts.runTimeInstall}/upload-status`,
      { stdio: "inherit" }
    );
  });
  it("buildtime install", async () => {
    const response = execFileSync("curl", [
      "-X",
      "POST",
      "-H",
      "Content-Type: multipart/form-data",
      "-F",
      `myFile=@${filePath}`,
      `http://localhost:${containersPorts.buildtimeInstall}/upload?apiKey=dev`,
    ]).toString();
    const dockerLogs = execSync(`docker logs ddus-buildtime-install`)
      .toString()
      .replace(/http:\/\/.*:/, "<ddus-fileserver-ip>:");
    expect(response).toMatchSnapshot();
    expect(dockerLogs).toMatchSnapshot();
  });
  it("runtime install", async () => {
    const response = execFileSync("curl", [
      "-X",
      "POST",
      "-H",
      "Content-Type: multipart/form-data",
      "-F",
      `myFile=@${filePath}`,
      `http://localhost:${containersPorts.runTimeInstall}/upload?apiKey=dev`,
    ]).toString();
    const dockerLogs = execSync(`docker logs ddus-runtime-install`)
      .toString()
      .replace(/http:\/\/.*:/, "<ddus-fileserver-ip>:")
      .replaceAll(version, "<ddus-version>")
      .replaceAll(`${platform}-${arch}`, "<platform-arch>");
    expect(response).toMatchSnapshot();
    expect(dockerLogs).toMatchSnapshot();
  });
  afterAll(() => {
    execSync("pnpm stop-test-servers", { stdio: "inherit" });
  });
});

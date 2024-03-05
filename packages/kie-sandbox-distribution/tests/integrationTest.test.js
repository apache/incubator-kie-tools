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
const kieSandboxDistributionEnv = require("../env");
const env = kieSandboxDistributionEnv.env;
const kieSandboxUrl = `http://127.0.0.1:${env.kieSandboxDistribution.kieSandbox.exposedPort}`;
const corsProxyUrl = `http://127.0.0.1:${env.kieSandboxDistribution.corsProxy.exposedPort}`;
const extendedServicesUrl = `http://127.0.0.1:${env.kieSandboxDistribution.extendedServices.exposedPort}`;

describe("Test built images individually", () => {
  beforeAll(() => {
    execSync(
      `pnpm docker:start-no-pull && wait-on -t 5m ${extendedServicesUrl}/ping && wait-on -t 5m ${corsProxyUrl}/ping && wait-on -t 5m ${kieSandboxUrl}`,
      { stdio: "inherit" }
    );
  });
  afterAll(() => {
    execSync(`pnpm docker:stop`, { stdio: "inherit" });
  });
  it("cors-proxy homepage", async () => {
    expect(await (await fetch(corsProxyUrl)).text()).toMatchSnapshot();
  });
  it("cors-proxy ping", async () => {
    expect(await (await fetch(`${corsProxyUrl}/ping`)).text()).toBe("pong");
  });
  it("extended-services homepage", async () => {
    expect(await (await fetch(extendedServicesUrl)).text()).toMatchSnapshot();
  });
  it("extended-services-java ping", async () => {
    const response = await (await fetch(`${extendedServicesUrl}/ping`)).json();
    expect(response).toHaveProperty("version");
    expect(response).toHaveProperty("started");
    expect(response.started).toBe(true);
  });
  it("kie-sandbox homepage", async () => {
    expect(await (await fetch(kieSandboxUrl)).text()).toContain('<script src="index.js"></script>');
  });
});

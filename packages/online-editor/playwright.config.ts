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

import { devices, defineConfig } from "@playwright/test";
import playwirghtBaseConfig from "@kie-tools/playwright-base/playwright.config";
import { ProjectName } from "@kie-tools/playwright-base/projectNames";
import merge from "lodash/merge";

import { env } from "./env";
const buildEnv: any = env; // build-env is not typed

const customConfig = defineConfig({
  expect: {
    timeout: 30000,
  },
  use: {
    viewport: { width: 1600, height: 1200 },
    baseURL: `http://localhost:${buildEnv.onlineEditor.dev.port}`,
    ignoreHTTPSErrors: true,
  },
  /* Run your local dev server before starting the tests */
  webServer: [
    {
      command: "pnpm start:cors-proxy",
      url: `http://localhost:${buildEnv.corsProxy.dev.port}/ping`,
      reuseExistingServer: !process.env.CI || true,
      stdout: "pipe",
    },
    {
      command: "pnpm start:extended-services",
      url: `http://localhost:${buildEnv.extendedServicesJava.port}/ping`,
      reuseExistingServer: !process.env.CI || true,
      stdout: "pipe",
    },
    {
      command: "pnpm start:kie-sandbox-accelerator-quarkus",
      url: `http://localhost:${buildEnv.kieSandboxAcceleratorQuarkus.dev.port}/git-repo-bare.git`,
      reuseExistingServer: !process.env.CI || true,
      stdout: "pipe",
    },
    {
      command: "pnpm start:kie-sandbox",
      url: `http://localhost:${buildEnv.onlineEditor.dev.port}`,
      reuseExistingServer: !process.env.CI || true,
      ignoreHTTPSErrors: true,
      timeout: 240000,
    },
  ],
  // Override
  projects: buildEnv.onlineEditor.test.ONLINE_EDITOR__skipPlaywrightTestsForArm64
    ? [
        {
          timeout: 60000,
          name: ProjectName.CHROMIUM,
          use: { ...devices["Desktop Chrome"], permissions: ["clipboard-read"] },
          testIgnore: "*",
        },

        // {
        //   name: "firefox",
        //   use: { ...devices["Desktop Firefox"] },
        // },

        {
          timeout: 60000,
          name: ProjectName.WEBKIT,
          use: { ...devices["Desktop Safari"], deviceScaleFactor: 1 },
        },

        {
          timeout: 60000,
          name: ProjectName.GOOGLE_CHROME,
          use: { ...devices["Desktop Chrome"], channel: "chrome", permissions: ["clipboard-read"] },
          testIgnore: "*",
        },
      ]
    : undefined,
});

export default defineConfig(merge(playwirghtBaseConfig, customConfig));

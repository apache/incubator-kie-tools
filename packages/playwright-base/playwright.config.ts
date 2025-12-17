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
import { ProjectName } from "./projectNames";

export default defineConfig({
  testDir: "./tests-e2e",
  outputDir: "dist-tests-e2e/output",
  snapshotPathTemplate: "{testDir}/__screenshots__/{projectName}/{testFileDir}/{arg}{ext}",
  /* Run tests in files in parallel */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: process.env.CI ? 2 : 1,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 2 : undefined,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: process.env.CI
    ? [
        ["github"],
        ["junit", { outputFile: "./dist-tests-e2e/junit-report-e2e.xml" }],
        ["html", { outputFolder: "./dist-tests-e2e/reports/", open: "never" }],
        ["list"],
      ]
    : [["html", { outputFolder: "./dist-tests-e2e/reports/", open: "never" }], ["list"]],
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    screenshot: "only-on-failure",
    /* automatically record video on retry  */
    video: "on-first-retry",
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: "on-first-retry",
    // Default locale;
    locale: "en-US",
  },
  expect: {
    timeout: 30_000,
    toHaveScreenshot: {
      // An acceptable ratio of pixels that are different to the
      // total amount of pixels, between 0 and 1.
      maxDiffPixelRatio: 0.001,
    },
  },
  /* Configure projects for major browsers */
  projects: [
    {
      timeout: 180_000,
      name: ProjectName.CHROMIUM,
      use: { ...devices["Desktop Chrome"], permissions: ["clipboard-read"] },
    },

    // {
    //   name: "firefox",
    //   use: { ...devices["Desktop Firefox"] },
    // },

    {
      timeout: 180_000,
      name: ProjectName.WEBKIT,
      use: { ...devices["Desktop Safari"], deviceScaleFactor: 1 },
    },

    {
      timeout: 180_000,
      name: ProjectName.GOOGLE_CHROME,
      use: { ...devices["Desktop Chrome"], channel: "chrome", permissions: ["clipboard-read"] },
    },
  ],
});

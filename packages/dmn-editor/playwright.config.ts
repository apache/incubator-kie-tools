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

import { defineConfig } from "@playwright/test";
import playwirghtBaseConfig from "@kie-tools/playwright-base/playwright.config";
import merge from "lodash/merge";
import { env } from "./env";

const buildEnv: any = env;

const customConfig = defineConfig({
  use: {
    baseURL: `http://localhost:${buildEnv.dmnEditor.storybook.port}`,
  },
  /* Run your local dev server before starting the tests */
  webServer: {
    command: "pnpm start",
    url: `http://localhost:${buildEnv.dmnEditor.storybook.port}/iframe.html?args=&id=use-cases-empty--empty&viewMode=story`,
    reuseExistingServer: !process.env.CI || true,
    stdout: "pipe",
    timeout: 180000,
  },
});

export default defineConfig(merge(playwirghtBaseConfig, customConfig));

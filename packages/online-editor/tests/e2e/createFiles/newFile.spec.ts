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

import { test, expect } from "../__fixtures__/base";

test.describe("New file", () => {
  test.beforeEach(async () => {
    test.slow();
  });

  test.describe("Home", () => {
    test.beforeEach(async ({ page }) => {
      await page.goto("/");
    });

    test("should create a new BPMN file", async ({ page, kieSandbox }) => {
      await page.getByRole("button", { name: "New Workflow" }).click();
      await expect(page.getByRole("button", { name: "Workflow Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Workflow Edit file name" })).toContainText("Untitled");
      await kieSandbox.isEditorLoaded();
      await expect(kieSandbox.getEditor().getByRole("button", { name: "Start Events" })).toBeAttached();
      await expect(page).toHaveScreenshot("new-file-bpmn.png");
    });

    test("should create a new DMN file", async ({ page, kieSandbox }) => {
      await page.getByRole("button", { name: "New Decision" }).click();
      await expect(page.getByRole("button", { name: "Decision Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Decision Edit file name" })).toContainText("Untitled");
      await expect(kieSandbox.getEditor().getByRole("heading", { name: "This DMN's Diagram is empty" })).toBeAttached();
      await expect(page).toHaveScreenshot("new-file-dmn.png");
    });

    test("should create a new PMML file", async ({ page, kieSandbox }) => {
      await page.getByRole("button", { name: "New Scorecard" }).click();
      await expect(page.getByRole("button", { name: "Scorecard Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Scorecard Edit file name" })).toContainText("Untitled");
      await expect(kieSandbox.getEditor().getByRole("button", { name: "Set Data Dictionary" })).toBeAttached();
      await expect(page).toHaveScreenshot("new-file-pmml.png");
    });
  });

  test.describe("Editor", () => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/546");
  });
});

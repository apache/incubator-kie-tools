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

test.describe("Sample", () => {
  test.beforeEach(async ({ page }) => {
    test.slow();
  });

  test.describe("Home", () => {
    test.beforeEach(async ({ page }) => {
      await page.goto("/");
    });

    test("should create a BPMN sample file", async ({ page, kieSandbox }) => {
      await page.getByRole("button", { name: "Try sample" }).nth(0).click();
      await expect(page.getByRole("button", { name: "Workflow Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Workflow Edit file name" })).toContainText("Sample");
      await kieSandbox.isEditorLoaded();
      await expect(kieSandbox.getEditor().getByRole("button", { name: "Start Events" })).toBeAttached();
      await expect(page).toHaveScreenshot("sample-bpmn.png");
    });

    test("should create a DMN sample file", async ({ page, kieSandbox }) => {
      await page.getByRole("button", { name: "Try sample" }).nth(1).click();
      await expect(page.getByRole("button", { name: "Decision Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Decision Edit file name" })).toContainText("Sample");
      await expect(kieSandbox.getEditor().getByText("Loan Pre-Qualification")).toBeAttached();
      await expect(page).toHaveScreenshot("sample-dmn.png");
    });

    test("should create a PMML sample file", async ({ page, kieSandbox }) => {
      await page.getByRole("button", { name: "Try sample" }).nth(2).click();
      await expect(page.getByRole("button", { name: "Scorecard Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Scorecard Edit file name" })).toContainText("Sample");
      await expect(kieSandbox.getEditor().getByRole("button", { name: "Set Data Dictionary" })).toBeAttached();
      await expect(page).toHaveScreenshot("sample-pmml.png");
    });
  });

  test.describe("Editor", () => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/547");
  });
});

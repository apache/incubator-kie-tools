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

test.describe("Upload", () => {
  test.beforeEach(async ({ page }) => {
    test.slow();
  });

  test.describe("Home", () => {
    test.beforeEach(async ({ page }) => {
      await page.goto("/");
    });

    test("should upload file by drag and drop", async ({ page, upload, kieSandbox }) => {
      await upload.dragAndDropFile(page.getByText("Drag & drop files and folders here..."), "testProcess.bpmn");
      await expect(page.getByRole("button", { name: "Workflow Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Workflow Edit file name" })).toContainText("testProcess");
      await expect(kieSandbox.getEditor().getByRole("button", { name: "Start Events" })).toBeAttached();
    });

    test("should upload file by 'Select files'", async ({ page, upload, kieSandbox }) => {
      await upload.fileSelector(page.getByRole("button", { name: "Select files..." }), "testModel.dmn");
      await expect(page.getByRole("button", { name: "Decision Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Decision Edit file name" })).toContainText("testModel");
      await expect(kieSandbox.getEditor().getByText("Test input data")).toBeAttached();
    });

    test("should upload file by 'Select folders'", async ({ page, upload }) => {
      test.skip(true, "https://github.com/microsoft/playwright/issues/6854");
      await upload.fileSelector(page.getByRole("button", { name: "Select folder..." }), "testFolder");
      await expect(page.getByRole("button", { name: "Scorecard Edit file name" })).toBeAttached();
      await expect(page.getByRole("button", { name: "Scorecard Edit file name" })).toContainText("testScoreCard");
    });
  });

  test.describe("Editor", () => {
    test.skip(true, "https://github.com/apache/incubator-kie-issues/issues/549");
  });
});

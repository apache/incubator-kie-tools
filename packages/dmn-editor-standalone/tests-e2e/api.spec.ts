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

import { test, expect } from "@playwright/test";
import type { open } from "../src/index";
import type { DmnEditorStandaloneApi } from "../src/DmnEditorStandaloneApi";

declare global {
  interface Window {
    DmnEditor: { open: typeof open };
    currentEditor: DmnEditorStandaloneApi;
  }
}

test.describe("Dmn Editor - API", () => {
  test.describe("Open editor", () => {
    test.beforeEach(async ({ page }) => {
      test.slow();
      await page.goto("");
    });
    test("should open and close the editor with a blank DMN", async ({ page }) => {
      await page.evaluate(() => {
        window.currentEditor = window.DmnEditor.open({
          container: document.getElementById("dmn-editor-container")!,
          initialContent: Promise.resolve(""),
          initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "model.dmn",
        });
      });

      const dmnEditorIFrame = page.frameLocator("#dmn-editor-standalone");

      await expect(dmnEditorIFrame.getByText("This DMN's Diagram is empty")).toBeAttached();
      await expect(page).toHaveScreenshot("open-editor-with-blank-dmn.png");

      await page.evaluate(() => {
        window.currentEditor.close();
      });

      await expect(dmnEditorIFrame.getByText("This DMN's Diagram is empty")).not.toBeAttached();
      await expect(page).toHaveScreenshot("closed-editor-after-opening-dmn.png");
    });
  });
});

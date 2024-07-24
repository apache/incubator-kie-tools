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
import type { open } from "../../src/index";
import type { DmnEditorStandaloneApi } from "../../src/DmnEditorStandaloneApi";
import { emptyDmn, emptyDrd, loanPreQualificationDmn } from "../__fixtures__/externalModels";

declare global {
  interface Window {
    DmnEditor: { open: typeof open };
    currentEditor: DmnEditorStandaloneApi;
    editCounter: number;
  }
}

test.describe("Dmn Editor - API", () => {
  test.describe("Open editor", () => {
    test.beforeEach(async ({ page }) => {
      test.slow();
      await page.goto("");
      await page.evaluate(() => {
        window.currentEditor = window.DmnEditor.open({
          container: document.getElementById("dmn-editor-container")!,
          initialContent: Promise.resolve(""),
          initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "model.dmn",
        });

        window.editCounter = 0;
        window.currentEditor.subscribeToContentChanges(() => {
          window.editCounter += 1;
          document.getElementById("edit-counter")!.innerHTML = window.editCounter.toString();
        });
      });
      const dmnEditorIFrame = page.frameLocator("#dmn-editor-standalone");
      await expect(dmnEditorIFrame.getByText("This DMN's Diagram is empty")).toBeAttached();
    });

    test("should open and close the editor with a blank DMN", async ({ page }) => {
      const dmnEditorIFrame = page.frameLocator("#dmn-editor-standalone");

      await expect(page).toHaveScreenshot("open-editor.png");

      await page.evaluate(() => {
        window.currentEditor.close();
      });

      await expect(dmnEditorIFrame.getByText("This DMN's Diagram is empty")).not.toBeAttached();
      await expect(page).toHaveScreenshot("closed-editor.png");
    });

    test("should count edits via subscribeToContentChanges", async ({ page }) => {
      const dmnEditorIFrame = page.frameLocator("#dmn-editor-standalone");

      const inputSelector = dmnEditorIFrame.getByTitle("Input Data", { exact: true });

      const diagramArea = dmnEditorIFrame.getByTestId("kie-dmn-editor--diagram-container");

      // Add 4 Input Data nodes and check if dirty count increases to 4
      for (let i = 1; i <= 4; i++) {
        await inputSelector.dragTo(diagramArea, { targetPosition: { x: 100 + (i - 1) * 200, y: 100 } });
        await diagramArea.click({ position: { x: 0, y: 0 } });
        await expect((await dmnEditorIFrame.locator(".react-flow__node-node_inputData").all()).length).toBe(i);
        await expect(await page.locator("#edit-counter")).toContainText(i.toString());
      }

      await expect(page).toHaveScreenshot("subscribeToContentChanges-4edits.png");
    });

    test("should update content via setContent", async ({ page }) => {
      const dmnEditorIFrame = page.frameLocator("#dmn-editor-standalone");

      // Loan Pre Qualification
      await page.evaluate((loanPreQualificationDmn) => {
        console.log(loanPreQualificationDmn);
        return window.currentEditor.setContent("bla.dmn", loanPreQualificationDmn);
      }, loanPreQualificationDmn);
      await expect(dmnEditorIFrame.getByText("Loan Pre-Qualification", { exact: true })).toBeAttached();
      await expect(page).toHaveScreenshot("setContent-loanPreQualification.png");

      // Empty DMN
      await page.evaluate((dmn) => {
        return window.currentEditor.setContent("emptyDmn.dmn", dmn);
      }, emptyDmn);
      await expect(dmnEditorIFrame.getByText("This DMN's Diagram is empty")).toBeAttached();
      await expect(page).toHaveScreenshot("setContent-emptyDmn.png");

      // Empty DRD
      await page.evaluate((dmn) => {
        return window.currentEditor.setContent("emptyDrd.dmn", dmn);
      }, emptyDrd);
      await expect(dmnEditorIFrame.getByText("Empty Diagram")).toBeAttached();
      await expect(page).toHaveScreenshot("setContent-emptyDrd.png");
    });
  });
});

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
import { loanPreQualificationDmn } from "../__fixtures__/externalModels";

test.describe("Dmn Editor - API", () => {
  test.describe("getContent", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
    });

    test("should get DMN contents of inputted DMN file", async ({ page, editor }) => {
      const editorIFrame = editor.getEditorIframe();
      await editor.setContent("loanPreQualification.dmn", loanPreQualificationDmn);
      await expect(editorIFrame.getByText("Loan Pre-Qualification", { exact: true })).toBeAttached();

      const content = await editor.getContent();
      expect(content).toBe(loanPreQualificationDmn);
    });

    test("should get current DMN contents via getContent", async ({ page, editor }) => {
      const editorIFrame = editor.getEditorIframe();
      const inputSelector = editorIFrame.getByTitle("Input Data", { exact: true });
      const decisionSelector = editorIFrame.getByTitle("Decision", { exact: true });
      const editorDiagram = editor.getEditorDiagram();

      // Add 4 Input Data nodes
      for (let i = 0; i < 4; i++) {
        await inputSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 100 } });
        await editor.resetFocus();
      }
      await expect((await editorIFrame.locator(".react-flow__node-node_inputData").all()).length).toBe(4);

      // Add 2 Decision nodes
      for (let i = 0; i < 2; i++) {
        await decisionSelector.dragTo(editorDiagram, { targetPosition: { x: 100 + i * 200, y: 300 } });
        await editor.resetFocus();
      }
      await expect(await editorIFrame.locator(".react-flow__node-node_decision")).toHaveCount(2);

      await expect(async () => {
        const dmnContentWith4Inputs2Decisions = await editor.getContent();
        await expect((dmnContentWith4Inputs2Decisions.match(/<inputData/gm) || []).length).toBe(4);
        await expect((dmnContentWith4Inputs2Decisions.match(/<decision/gm) || []).length).toBe(2);
      }).toPass({
        intervals: [100, 250, 500, 1000],
        timeout: 10_000,
      });

      // Delete 1 Input Data node
      await editorIFrame.locator(".react-flow__node-node_inputData").first().click();
      await page.keyboard.press("Delete");
      await editor.resetFocus();

      await expect((await editorIFrame.locator(".react-flow__node-node_inputData").all()).length).toBe(3);

      await expect(async () => {
        const dmnContentWith4Inputs2Decisions = await editor.getContent();
        await expect((dmnContentWith4Inputs2Decisions.match(/<inputData/gm) || []).length).toBe(3);
        await expect((dmnContentWith4Inputs2Decisions.match(/<decision/gm) || []).length).toBe(2);
      }).toPass({
        intervals: [100, 250, 500, 1000],
        timeout: 10_000,
      });
    });
  });
});

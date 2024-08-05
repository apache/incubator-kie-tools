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
import { ExternalFile } from "../__fixtures__/files";
import { NodeType } from "../__fixtures__/nodes";

test.describe("DMN Editor - Standalone - API", () => {
  test.describe("getContent", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
      test.slow();
    });

    test("should get DMN contents of input DMN file", async ({ editor, files }) => {
      await editor.setContent("loanPreQualification.dmn", await files.getFile(ExternalFile.LOAN_PRE_QUALIFICATION_DMN));
      await expect(editor.get().getByText("Loan Pre-Qualification", { exact: true })).toBeAttached();
      await expect(await editor.getFormattedContent()).toEqual(
        await files.getFormattedFile(ExternalFile.LOAN_PRE_QUALIFICATION_DMN)
      );
    });

    test("should get current DMN contents via getContent", async ({ editor, palette, nodes }) => {
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 100, y: 100 },
        thenRenameTo: "Input-A",
      });
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 300, y: 100 },
        thenRenameTo: "Input-B",
      });
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 500, y: 100 },
        thenRenameTo: "Input-C",
      });
      await palette.dragNewNode({
        type: NodeType.INPUT_DATA,
        targetPosition: { x: 700, y: 100 },
        thenRenameTo: "Input-D",
      });
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 100, y: 300 },
        thenRenameTo: "Decision-A",
      });
      await palette.dragNewNode({
        type: NodeType.DECISION,
        targetPosition: { x: 300, y: 300 },
        thenRenameTo: "Decision-B",
      });

      await expect(async () => {
        const { decisionNodeQtt, inputDataNodeQtt } = await editor.getContentStats();
        await expect(inputDataNodeQtt).toBe(4);
        await expect(decisionNodeQtt).toBe(2);
      }).toPass();

      // Delete 1 Input Data node
      await nodes.delete({ name: "Input-A" });
      await expect(nodes.get({ name: "Input-A" })).not.toBeAttached();

      await expect(async () => {
        const { decisionNodeQtt, inputDataNodeQtt } = await editor.getContentStats();
        await expect(inputDataNodeQtt).toBe(3);
        await expect(decisionNodeQtt).toBe(2);
      }).toPass();
    });
  });
});

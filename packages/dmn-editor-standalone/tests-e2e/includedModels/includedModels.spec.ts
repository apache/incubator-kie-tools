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

test.describe("DMN Editor - Standalone - Resources", () => {
  test.describe("includedModels/resources", () => {
    test("should list all resources", async ({ editor, files }) => {
      const resources: Array<[string, { contentType: "text" | "binary"; content: string }]> = [
        [
          "loan-pre-qualification.dmn",
          { content: await files.getFile(ExternalFile.LOAN_PRE_QUALIFICATION_DMN), contentType: "text" },
        ],
        ["can-drive.dmn", { content: await files.getFile(ExternalFile.CAN_DRIVE_DMN), contentType: "text" }],
        ["find-employees.dmn", { content: await files.getFile(ExternalFile.FIND_EMPLOYEES_DMN), contentType: "text" }],
        ["types.dmn", { content: await files.getFile(ExternalFile.TYPES_DMN), contentType: "text" }],
        ["scorecard.pmml", { content: await files.getFile(ExternalFile.SCORECARD_PMML), contentType: "text" }],
      ];

      // Open the editor
      await editor.open({ resources });

      // Switch to Included Models tab
      await editor.get().getByRole("tab").getByText("Included models").click();

      // Include models modal
      await editor.get().getByText("Include model").click();
      await editor.get().getByPlaceholder("Select a model to include...").click();

      await expect(editor.get().getByText("DMN", { exact: true })).toBeAttached();
      await expect(editor.get().getByText("loan-pre-qualification.dmn")).toBeAttached();
      await expect(editor.get().getByText("can-drive.dmn")).toBeAttached();
      await expect(editor.get().getByText("find-employees.dmn")).toBeAttached();
      await expect(editor.get().getByText("types.dmn")).toBeAttached();
      await expect(editor.get().getByText("PMML", { exact: true })).toBeAttached();
      await expect(editor.get().getByText("scorecard.pmml")).toBeAttached();
    });

    test("should not list any models to be included", async ({ editor, files }) => {
      const resources: Array<[string, { contentType: "text" | "binary"; content: string }]> = [
        [
          "loan-pre-qualification.dmn",
          { content: await files.getFile(ExternalFile.LOAN_PRE_QUALIFICATION_DMN), contentType: "text" },
        ],
        ["path1/can-drive.dmn", { content: await files.getFile(ExternalFile.CAN_DRIVE_DMN), contentType: "text" }],
        [
          "path2/find-employees.dmn",
          { content: await files.getFile(ExternalFile.FIND_EMPLOYEES_DMN), contentType: "text" },
        ],
        ["path3/types.dmn", { content: await files.getFile(ExternalFile.TYPES_DMN), contentType: "text" }],
        [
          "path1/pmml/scorecard.pmml",
          { content: await files.getFile(ExternalFile.SCORECARD_PMML), contentType: "text" },
        ],
      ];

      // Open the editor
      await editor.open({ resources, initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/dmn/model.dmn" });

      // Switch to Included Models tab
      await editor.get().getByRole("tab").getByText("Included models").click();

      // Include models modal
      await editor.get().getByText("Include model").click();

      await expect(editor.get().getByText("There's no available models to be included.")).toBeAttached();
    });

    test("should list all resources on same parent path", async ({ editor, files }) => {
      const resources: Array<[string, { contentType: "text" | "binary"; content: string }]> = [
        [
          "loan-pre-qualification.dmn",
          { content: await files.getFile(ExternalFile.LOAN_PRE_QUALIFICATION_DMN), contentType: "text" },
        ],
        ["path1/can-drive.dmn", { content: await files.getFile(ExternalFile.CAN_DRIVE_DMN), contentType: "text" }],
        [
          "path2/find-employees.dmn",
          { content: await files.getFile(ExternalFile.FIND_EMPLOYEES_DMN), contentType: "text" },
        ],
        ["path3/types/types.dmn", { content: await files.getFile(ExternalFile.TYPES_DMN), contentType: "text" }],
        [
          "path1/pmml/scorecard.pmml",
          { content: await files.getFile(ExternalFile.SCORECARD_PMML), contentType: "text" },
        ],
      ];

      // Open the editor
      await editor.open({ resources, initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/model.dmn" });

      // Switch to Included Models tab
      await editor.get().getByRole("tab").getByText("Included models").click();

      // Include models modal
      await editor.get().getByText("Include model").click();
      await editor.get().getByPlaceholder("Select a model to include...").click();

      await expect(editor.get().getByText("DMN", { exact: true })).toBeAttached();
      await expect(editor.get().getByText("loan-pre-qualification.dmn")).not.toBeAttached();
      await expect(editor.get().getByText("can-drive.dmn")).toBeAttached();
      await expect(editor.get().getByText("find-employees.dmn")).not.toBeAttached();
      await expect(editor.get().getByText("types.dmn")).not.toBeAttached();
      await expect(editor.get().getByText("PMML", { exact: true })).toBeAttached();
      await expect(editor.get().getByText("scorecard.pmml")).toBeAttached();

      await expect(editor.get().getByText("path1", { exact: true })).toBeAttached();
      await expect(editor.get().getByText("path2", { exact: true })).not.toBeAttached();
      await expect(editor.get().getByText("path3/types", { exact: true })).not.toBeAttached();
      await expect(editor.get().getByText("path1/pmml", { exact: true })).toBeAttached();
    });
  });
});

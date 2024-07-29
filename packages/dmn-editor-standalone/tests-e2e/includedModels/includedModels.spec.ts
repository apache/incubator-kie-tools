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
import {
  loanPreQualificationDmn,
  canDriveDmn,
  findEmployeesDmn,
  typesDmn,
  scorecardPmml,
} from "../__fixtures__/externalModels";
import { ContentType } from "@kie-tools-core/workspace/dist/api";

test.describe("DMN Editor - Standalone - Resources", () => {
  test.describe("includedModels/resources", () => {
    test("should list all resources", async ({ editor }) => {
      const resources: Array<[string, { contentType: ContentType; content: string }]> = [
        ["loan-pre-qualification.dmn", { content: loanPreQualificationDmn, contentType: ContentType.TEXT }],
        ["can-drive.dmn", { content: canDriveDmn, contentType: ContentType.TEXT }],
        ["find-employees.dmn", { content: findEmployeesDmn, contentType: ContentType.TEXT }],
        ["types.dmn", { content: typesDmn, contentType: ContentType.TEXT }],
        ["scorecard.pmml", { content: scorecardPmml, contentType: ContentType.TEXT }],
      ];

      await editor.open({ resources });
      const editorIFrame = editor.getEditorIframe();

      // Switch to Included Models tab
      await editorIFrame.getByRole("tab").getByText("Included models").click();

      // Include models modal
      await editorIFrame.getByText("Include model").click();
      await editorIFrame.getByPlaceholder("Select a model to include...").click();

      await expect(editorIFrame.getByText("DMN", { exact: true })).toBeAttached();
      await expect(editorIFrame.getByText("loan-pre-qualification.dmn")).toBeAttached();
      await expect(editorIFrame.getByText("can-drive.dmn")).toBeAttached();
      await expect(editorIFrame.getByText("find-employees.dmn")).toBeAttached();
      await expect(editorIFrame.getByText("types.dmn")).toBeAttached();
      await expect(editorIFrame.getByText("PMML", { exact: true })).toBeAttached();
      await expect(editorIFrame.getByText("scorecard.pmml")).toBeAttached();
    });

    test("should not list any models to be included", async ({ editor }) => {
      const resources: Array<[string, { contentType: ContentType; content: string }]> = [
        ["loan-pre-qualification.dmn", { content: loanPreQualificationDmn, contentType: ContentType.TEXT }],
        ["path1/can-drive.dmn", { content: canDriveDmn, contentType: ContentType.TEXT }],
        ["path2/find-employees.dmn", { content: findEmployeesDmn, contentType: ContentType.TEXT }],
        ["path3/types.dmn", { content: typesDmn, contentType: ContentType.TEXT }],
        ["path1/pmml/scorecard.pmml", { content: scorecardPmml, contentType: ContentType.TEXT }],
      ];

      await editor.open({ resources, initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/dmn/model.dmn" });
      const editorIFrame = editor.getEditorIframe();

      // Switch to Included Models tab
      await editorIFrame.getByRole("tab").getByText("Included models").click();

      // Include models modal
      await editorIFrame.getByText("Include model").click();

      await expect(editorIFrame.getByText("There's no available models to be included.")).toBeAttached();
    });

    test("should list all resources on same parent path", async ({ editor }) => {
      const resources: Array<[string, { contentType: ContentType; content: string }]> = [
        ["loan-pre-qualification.dmn", { content: loanPreQualificationDmn, contentType: ContentType.TEXT }],
        ["path1/can-drive.dmn", { content: canDriveDmn, contentType: ContentType.TEXT }],
        ["path2/find-employees.dmn", { content: findEmployeesDmn, contentType: ContentType.TEXT }],
        ["path3/types/types.dmn", { content: typesDmn, contentType: ContentType.TEXT }],
        ["path1/pmml/scorecard.pmml", { content: scorecardPmml, contentType: ContentType.TEXT }],
      ];

      await editor.open({ resources, initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/model.dmn" });
      const editorIFrame = editor.getEditorIframe();

      // Switch to Included Models tab
      await editorIFrame.getByRole("tab").getByText("Included models").click();

      // Include models modal
      await editorIFrame.getByText("Include model").click();
      await editorIFrame.getByPlaceholder("Select a model to include...").click();

      await expect(editorIFrame.getByText("DMN", { exact: true })).toBeAttached();
      await expect(editorIFrame.getByText("loan-pre-qualification.dmn")).not.toBeAttached();
      await expect(editorIFrame.getByText("can-drive.dmn")).toBeAttached();
      await expect(editorIFrame.getByText("find-employees.dmn")).not.toBeAttached();
      await expect(editorIFrame.getByText("types.dmn")).not.toBeAttached();
      await expect(editorIFrame.getByText("PMML", { exact: true })).toBeAttached();
      await expect(editorIFrame.getByText("scorecard.pmml")).toBeAttached();

      await expect(editorIFrame.getByText("path1", { exact: true })).toBeAttached();
      await expect(editorIFrame.getByText("path2", { exact: true })).not.toBeAttached();
      await expect(editorIFrame.getByText("path3/types", { exact: true })).not.toBeAttached();
      await expect(editorIFrame.getByText("path1/pmml", { exact: true })).toBeAttached();
    });
  });
});

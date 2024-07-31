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

test.describe("DMN Editor - Standalone - API", () => {
  test.describe("getPreview", () => {
    test.beforeEach(async ({ editor }) => {
      await editor.open();
    });

    test("should get preview SVG via getPreview for loanPreQualification", async ({ page, editor, files }) => {
      // Loan Pre Qualification
      await editor.setContent("loanPreQualification.dmn", await files.getFile(ExternalFile.LOAN_PRE_QUALIFICATION_DMN));
      await expect(editor.get().getByText("Loan Pre-Qualification", { exact: true })).toBeAttached();
      await page.setContent((await editor.getPreview())!);
      await expect(page).toHaveScreenshot("getPreview-loanPreQualificationSvg.png");
    });

    test("should get preview SVG via getPreview for emptyDmn", async ({ page, editor, files }) => {
      // Empty DMN
      await editor.setContent("emptyDmn.dmn", await files.getFile(ExternalFile.EMPTY_DMN));
      await expect(editor.get().getByText("This DMN's Diagram is empty")).toBeAttached();
      await page.setContent((await editor.getPreview())!);
      await expect(page).toHaveScreenshot("getPreview-emptyPreviewSvg.png");
    });
  });
});

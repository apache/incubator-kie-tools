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
import { DataType } from "../__fixtures__/dataTypes";
import { TabName } from "../__fixtures__/editor";

test.describe("Copy and Paste Data Type", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();

    //create any data type without constaint
    await dataTypes.changeDataTypeName({ newName: "Custom data type" });
    await dataTypes.addDataTypeDescription({ newDescription: "Custom undefined data type description" });
    await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Any });
    await expect(dataTypes.getDataType({ name: "Custom data type" })).toBeAttached();
  });

  test("should copy and paste data type - empty state", async ({ browserName, context, dataTypes, page }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    //copy data type
    await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    await dataTypes.get().getByLabel("Action", { exact: true }).click();
    await page.getByRole("menuitem").getByText("Copy", { exact: true }).click();

    //remove data type
    await dataTypes.get().getByLabel("Action").click();
    await page.getByRole("menuitem").getByText("Remove", { exact: true }).click();
    await expect(dataTypes.get()).toHaveScreenshot("copy-paste-data-type-after-remove.png");

    //paste copied first data type
    await dataTypes.pasteFirstDataType();
    await expect(dataTypes.get()).toHaveScreenshot("copy-paste-data-type-empty-state.png");
  });

  test(`should copy and paste data type - non empty state`, async ({ browserName, context, dataTypes, page }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    //copy data type
    await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    await dataTypes.get().getByLabel("Action").click();
    await page.getByRole("menuitem").getByText("Copy", { exact: true }).click();

    //paste copied data type
    await dataTypes.get().getByRole("button", { name: "Select", exact: true }).click();
    await page.getByRole("menuitem").getByText("Paste", { exact: true }).click();
    await expect(dataTypes.get()).toHaveScreenshot("copy-paste-data-type-non-empty-state.png");
  });
});

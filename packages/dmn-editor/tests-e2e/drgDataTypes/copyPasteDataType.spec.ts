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
import { DataType, DefaultDataTypeName, DMN15_SPEC_TYPE_LANGUAGE } from "../__fixtures__/dataTypes";
import { TabName } from "../__fixtures__/editor";

test.describe("Copy and Paste Data Type", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test("Should copy and paste data type - empty state", async ({
    jsonModel,
    browserName,
    context,
    dataTypes,
    page,
  }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    //create any data type without constaint
    await dataTypes.changeDataTypeName({ newName: DefaultDataTypeName.Any });
    await dataTypes.addDataTypeDescription({ newDescription: "New Data Type Description" });
    await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Any,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
    });

    //copy data type
    await context.grantPermissions(["clipboard-read", "clipboard-write"]);
    await dataTypes.get().getByLabel("Action").click();
    await page.getByRole("menuitem").getByText("Copy", { exact: true }).click();

    //remove data type
    await dataTypes.get().getByLabel("Action").click();
    await page.getByRole("menuitem").getByText("Remove", { exact: true }).click();
    await expect(dataTypes.get()).toHaveScreenshot("copy-paste-data-type-after-remove.png");

    //paste copied first data type
    await dataTypes.pasteFirstDataType();
    await expect(dataTypes.get()).toHaveScreenshot("copy-paste-data-type-empty-state.png");
  });

  test(`Should copy and paste data type - non empty state`, async ({
    jsonModel,
    browserName,
    context,
    dataTypes,
    page,
  }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );

    //create any data type without constaint
    await dataTypes.changeDataTypeName({ newName: DefaultDataTypeName.Any });
    await dataTypes.addDataTypeDescription({ newDescription: "New Data Type Description" });
    await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Any,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
    });

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

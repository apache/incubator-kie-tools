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

import { create } from "domain";
import { test, expect } from "../__fixtures__/base";
import { DataType, DMN15_SPEC_TYPE_LANGUAGE } from "../__fixtures__/dataTypes";
import { TabName } from "../__fixtures__/editor";

test.describe("Struct Data Types - Properties Actions", () => {
  test.beforeEach(async ({ editor, dataTypes, clipboard, context, browserName }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
    clipboard.setup(context, browserName);
  });

  test(`should view struct data type property`, async ({ jsonModel, dataTypes, page }) => {
    await dataTypes.createStructDataType({ name: "New Data type - Struct" });
    await expect(page.getByRole("checkbox").last()).toBeChecked();

    //add property
    await dataTypes.addStructProperty({ name: "Property 1", type: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [
        {
          "@_id": dataType.itemComponent?.[0]["@_id"],
          "@_name": "Property 1",
          "@_isCollection": dataType.itemComponent?.[0]["@_isCollection"],
          "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
          typeRef: { __$$text: "Any" },
        },
      ],
    });

    //view property
    await dataTypes.setStructTypeAction({ action: "View" });

    await expect(dataTypes.get()).toHaveScreenshot("view-struct-data-type-property.png");
  });

  test(`should extract data type of struct data type property`, async ({ jsonModel, dataTypes, page }) => {
    await dataTypes.createStructDataType({ name: "New Data type - Struct" });

    //add property
    await dataTypes.addStructProperty({ name: "Property 1", type: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [
        {
          "@_id": dataType.itemComponent?.[0]["@_id"],
          "@_name": "Property 1",
          "@_isCollection": dataType.itemComponent?.[0]["@_isCollection"],
          "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
          typeRef: { __$$text: "Any" },
        },
      ],
    });

    //extract property data type
    await dataTypes.setStructTypeAction({ action: "Extract data type" });

    const propertyDataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(propertyDataType).toEqual({
      "@_id": propertyDataType["@_id"],
      "@_name": "tProperty 1",
      "@_isCollection": propertyDataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: { __$$text: "Any" },
    });
  });

  test(`should copy struct data type property`, async ({ jsonModel, dataTypes, page, browserName }) => {
    test.skip(
      browserName === "webkit",
      "Playwright Webkit doesn't support clipboard permissions: https://github.com/microsoft/playwright/issues/13037"
    );
    await dataTypes.createStructDataType({ name: "New Data type - Struct" });

    //add property
    await dataTypes.addStructProperty({ name: "Property 1", type: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [
        {
          "@_id": dataType.itemComponent?.[0]["@_id"],
          "@_name": "Property 1",
          "@_isCollection": dataType.itemComponent?.[0]["@_isCollection"],
          "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
          typeRef: { __$$text: "Any" },
        },
      ],
    });

    //copt property
    await dataTypes.setStructTypeAction({ action: "Copy" });

    //paste property
    await page.getByTestId("kie-tools--dmn-editor--data-types-list").getByLabel("Select").click();
    await page.getByRole("menuitem", { name: "Paste" }).click();
    const propertyDataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(propertyDataType).toEqual({
      "@_id": propertyDataType["@_id"],
      "@_name": "Property 1",
      "@_isCollection": propertyDataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: { __$$text: "Any" },
    });
  });

  test(`should cut struct data type property`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.createStructDataType({ name: "New Data type - Struct" });

    //add property
    await dataTypes.addStructProperty({ name: "Property 1", type: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [
        {
          "@_id": dataType.itemComponent?.[0]["@_id"],
          "@_name": "Property 1",
          "@_isCollection": dataType.itemComponent?.[0]["@_isCollection"],
          "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
          typeRef: { __$$text: "Any" },
        },
      ],
    });

    //cut property
    await dataTypes.setStructTypeAction({ action: "Cut" });

    const propertyRemoved = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(propertyRemoved).toEqual({
      "@_id": propertyRemoved["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": propertyRemoved["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [],
    });
  });

  test(`should remove struct data type property`, async ({ jsonModel, dataTypes, page, browserName }) => {
    await dataTypes.changeDataTypeName({ newName: "New Data type - Struct" });
    await dataTypes.enableDataTypeStruct();

    //add property
    await dataTypes.addStructProperty({ name: "Property 1", type: DataType.Any });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [
        {
          "@_id": dataType.itemComponent?.[0]["@_id"],
          "@_name": "Property 1",
          "@_isCollection": dataType.itemComponent?.[0]["@_isCollection"],
          "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
          typeRef: { __$$text: "Any" },
        },
      ],
    });

    //remove property
    await dataTypes.setStructTypeAction({ action: "Remove" });

    const propertyRemoved = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(propertyRemoved).toEqual({
      "@_id": propertyRemoved["@_id"],
      "@_name": "New Data type - Struct",
      "@_isCollection": propertyRemoved["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      itemComponent: [],
    });
  });
});

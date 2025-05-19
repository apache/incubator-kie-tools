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
import { DataType, DMN15_SPEC_TYPE_LANGUAGE } from "../__fixtures__/dataTypes";
import { TabName } from "../__fixtures__/editor";

test.describe("Create Struct Data Types - Without Constraint", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test.describe("Create struct data type without constraint", () => {
    test(`Create Struct data type`, async ({ jsonModel, dataTypes, page }) => {
      await dataTypes.changeNameAndBaseType({
        newName: "New Data type - Struct",
        baseType: DataType.Any,
        description: "New Data Type Description",
      });
      await dataTypes.enableIsStruct();
      await expect(page.getByRole("checkbox").last()).toBeChecked();

      //add property
      await dataTypes.addStructProperty({ name: "Property 1" });
      await dataTypes.resetFocus();

      const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
      expect(dataType).not.toBeUndefined();
      expect(dataType).toEqual({
        "@_id": dataType["@_id"],
        "@_name": "New Data type - Struct",
        "@_isCollection": dataType["@_isCollection"],
        "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
        description: { __$$text: "New Data Type Description" },
        itemComponent: [
          {
            "@_id": dataType.itemComponent?.[0]["@_id"],
            "@_name": "Property 1",
            "@_isCollection": dataType.itemComponent?.[0]["@_isCollection"],
            "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
          },
        ],
      });
    });
  });
});

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

test.describe("Create Struct Data Types - Without Constraint", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test.describe("Create struct data type without constraint", () => {
    test(`create struct data type`, async ({ dataTypes, page }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Struct" });
      await dataTypes.addDataTypeDescription({ newDescription: "Custom struct data type description" });
      await dataTypes.enableDataTypeStruct();
      await expect(await page.getByRole("checkbox").last()).toBeChecked();
      await dataTypes.addDataTypeStructProperty({ name: "Property 1" });
      await dataTypes.addDataTypeStructProperty({ name: "Property 2" });
      await expect(dataTypes.getDataType({ name: "Custom data type - Struct" })).toBeAttached();
    });
  });
});

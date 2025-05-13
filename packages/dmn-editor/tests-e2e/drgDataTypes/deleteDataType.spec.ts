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

test.describe("Delete - Data Type", () => {
  test(`Should delete a data type `, async ({ jsonModel, editor, dataTypes, page }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });

    await dataTypes.createFirstCustonDataType();
    await dataTypes.changeDataTypeName({ newName: DefaultDataTypeName.Any });
    await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Any });

    // Delete the data type
    await dataTypes.get().getByLabel("Action").click();
    await page.getByRole("menuitem").getByText("Remove").click();

    // Assert the data type is removed from the model
    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toBeUndefined();

    await expect(dataTypes.get()).toHaveScreenshot("delete-custom-data-type-after-delete.png");
  });
});

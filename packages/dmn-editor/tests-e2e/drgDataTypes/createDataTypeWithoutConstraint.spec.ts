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

test.describe("Create Data Types - Without Constraint", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test.describe("Create custom data type without constraint", () => {
    test(`Create Undefined data type`, async ({ jsonModel, dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Undefined" });
      await dataTypes.addDataTypeDescription({ newDescription: "New Data Type Description" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Undefined });
      const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 1, drdIndex: 0 });
      console.log(dataType);
      //await expect(dataTypes.getDataType({ name: "Custom data type - Undefined" })).toBeAttached();
    });

    test(`Create Any data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Any" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Any });
      await expect(dataTypes.getDataType({ name: "Custom data type - Any" })).toBeAttached();
    });

    test(`Create Boolean data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Boolean" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Boolean });
      await expect(dataTypes.getDataType({ name: "Custom data type - Boolean" })).toBeAttached();
    });

    test(`Create Context data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Context" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Context });
      await expect(dataTypes.getDataType({ name: "Custom data type - Context" })).toBeAttached();
    });

    test(`Custom Date data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Date" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Date });
      await expect(dataTypes.getDataType({ name: "Custom data type - Date" })).toBeAttached();
    });

    test(`create DateTime data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTime" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTime });
      await expect(dataTypes.getDataType({ name: "Custom data type - DateTime" })).toBeAttached();
    });

    test(`create DateTimeDuration data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTimeDuration" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTimeDuration });
      await expect(dataTypes.getDataType({ name: "Custom data type - DateTimeDuration" })).toBeAttached();
    });

    test(`create Number data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Number" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await expect(dataTypes.getDataType({ name: "Custom data type - Number" })).toBeAttached();
    });

    test(`create String data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - String" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await expect(dataTypes.getDataType({ name: "Custom data type - String" })).toBeAttached();
    });

    test(`create Time data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Time" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Time });
      await expect(dataTypes.getDataType({ name: "Custom data type - Time" })).toBeAttached();
    });

    test(`create YearsMonthsDuration data type`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - YearsMonthsDuration" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.YearsMonthsDuration });
      await expect(dataTypes.getDataType({ name: "Custom data type - YearsMonthsDuration" })).toBeAttached();
    });
  });
});

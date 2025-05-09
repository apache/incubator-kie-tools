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

test.describe("Create Data Types - With Constraint", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test.describe("Create custom data type with constraint", () => {
    test(`Create Any data type - Expression Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Any - Expression Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Any });
      await dataTypes.getExpressionConstraintButton().click();
      await dataTypes.addExpressionConstraint({ value: "> 20" });
      await expect(dataTypes.getDataType({ name: "Custom data type - Any - Expression Constraint" })).toBeAttached();
    });

    test(`Custom Date data type - Expression Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Date - Expression Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Date });
      await dataTypes.getExpressionConstraintButton().click();
      await dataTypes.addExpressionConstraint({ value: 'date("2025-04-14")' });
      await expect(dataTypes.getDataType({ name: "Custom data type - Date - Expression Constraint" })).toBeAttached();
    });

    test(`Custom Date data type - Enumeration Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Date - Enumeration Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Date });
      await dataTypes.getEnumerationConstraintButton().click();
      await dataTypes.addEnumerationConstraint({ values: ["2025-01-14", "2025-04-15"] });
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toBeAttached();
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toContainText(
        'date("2025-01-14"), date("2025-04-15")'
      );
    });

    test(`Custom Date data type - Range Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Date - Range Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Date });
      await dataTypes.getRangeConstraintButton().click();
      await dataTypes.addRangeConstraint({ values: ["2025-01-14", "2025-04-15"] });
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toBeAttached();
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toContainText(
        '[date("2025-01-14")..date("2025-04-15"))'
      );
    });

    test(`create Number data type - Expression Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Number - Expression Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.getExpressionConstraintButton().click();
      await dataTypes.addExpressionConstraint({ value: "20" });
    });

    test(`create Number data type - Enumeration Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Number - Enumeration Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.getEnumerationConstraintButton().click();
      await dataTypes.addEnumerationConstraint({ values: ["10", "20"] });
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toBeAttached();
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toContainText("10, 20");
    });

    test(`create Number data type - Range Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - Number - Range Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.Number });
      await dataTypes.getRangeConstraintButton().click();
      await dataTypes.addRangeConstraint({ values: ["10", "20"] });
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toBeAttached();
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toContainText("[10..20)");
    });

    test(`create String data type - Expression Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - String - Expression Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await dataTypes.getExpressionConstraintButton().click();
      await dataTypes.addExpressionConstraint({ value: "20" });
    });

    test(`create String data type - Enumeration Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - String - Enumeration Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await dataTypes.getEnumerationConstraintButton().click();
      await dataTypes.addEnumerationConstraint({ values: ["10", "20"] });
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toBeAttached();
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toContainText('"10", "20"');
    });

    test(`create String data type - Range Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - String - Range Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.String });
      await dataTypes.getRangeConstraintButton().click();
      await dataTypes.addRangeConstraint({ values: ["10", "20"] });
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toBeAttached();
      await expect(dataTypes.getReadonlyExpressionConstraintValue()).toContainText('["10".."20")');
    });

    test(`create DateTime data type - Expression Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTime - Expression Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTime });
      await dataTypes.getExpressionConstraintButton().click();
    });

    test(`create DateTime data type - Enumeration Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTime - Enumeration Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTime });
      await dataTypes.getEnumerationConstraintButton().click();
    });

    test(`create DateTime data type - Range Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTime - Range Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTime });
      await dataTypes.getRangeConstraintButton().click();
    });

    test(`create DateTimeDuration data type - Expression Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTimeDuration - Expression Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTimeDuration });
      await dataTypes.getExpressionConstraintButton().click();
    });

    test(`create DateTimeDuration data type - Enumeration Constraint`, async ({ dataTypes }) => {
      await dataTypes.changeDataTypeName({ newName: "Custom data type - DateTimeDuration - Enumeration Constraint" });
      await dataTypes.changeDataTypeBaseType({ newBaseType: DataType.DateTimeDuration });
      await dataTypes.getEnumerationConstraintButton().click();
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

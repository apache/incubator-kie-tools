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

test.describe("Create Data Types - With Constraint", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test(`Create Any data type - Expression constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Any,
      baseType: DataType.Any,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: "> 20" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Any,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: "> 20" },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create Number data type - Expression constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Number,
      baseType: DataType.Number,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: "20" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Number,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: "20" },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create Number data type - Enumeration constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Number,
      baseType: DataType.Number,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraint({ values: ["10", "20"] });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Number,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: "10, 20" },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create Number data type - Range constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Number,
      baseType: DataType.Number,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraint({ values: ["10", "20"] });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Number,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: "[10..20)" },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create String data type - Expression constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.String,
      baseType: DataType.String,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: "20" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.String,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: "20" },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create String data type - Enumeration constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.String,
      baseType: DataType.String,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraint({ values: ["10", "20"] });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.String,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '"10", "20"' },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create String data type - Range constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.String,
      baseType: DataType.String,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraint({ values: ["10", "20"] });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.String,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '["10".."20")' },
        "@_kie:constraintType": "range",
      },
    });
  });
});

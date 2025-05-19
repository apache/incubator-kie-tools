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

test.describe("Create Data Types - Without Constraint", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test(`Create data type - Undefined`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Undefined,
      baseType: DataType.Undefined,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Undefined,
      "@_isCollection": dataType["@_isCollection"],
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
    });
  });

  test(`Create data type - Any`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Any,
      baseType: DataType.Any,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Any,
      "@_isCollection": dataType["@_isCollection"],
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - Boolean`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Boolean,
      baseType: DataType.Boolean,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.Boolean,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - Context`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Context,
      baseType: DataType.Context,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.Context,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Custom data type - Date`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Date,
      baseType: DataType.Date,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.Date,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - DateTime`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTime,
      baseType: DataType.DateTime,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.DateTime,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - DateTimeDuration`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTimeDuration,
      baseType: DataType.DateTimeDuration,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.DateTimeDuration,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - Number`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Number,
      baseType: DataType.Number,
      description: "New Data Type Description",
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.Number,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - String`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.String,
      baseType: DataType.String,
      description: "New Data Type Description",
    });
    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.String,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - Time`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Time,
      baseType: DataType.Time,
      description: "New Data Type Description",
    });
    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.Time,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });

  test(`Create data type - YearsMonthsDuration`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.YearsMonthsDuration,
      baseType: DataType.YearsMonthsDuration,
      description: "New Data Type Description",
    });
    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).not.toBeUndefined();
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_isCollection": dataType["@_isCollection"],
      "@_name": DefaultDataTypeName.YearsMonthsDuration,
      description: { __$$text: "New Data Type Description" },
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      typeRef: dataType["typeRef"],
    });
  });
});

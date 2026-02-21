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

test.describe("Create Date, Time and Duration Data Types - With Constraints", () => {
  test.beforeEach(async ({ editor, dataTypes }) => {
    await editor.open();
    await editor.changeTab({ tab: TabName.DATA_TYPES });
    await dataTypes.createFirstCustonDataType();
  });

  test(`Create Date data type - Expression constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Date,
      baseType: DataType.Date,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: 'date("2025-04-14")' });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Date,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: dataType["typeRef"],
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'date("2025-04-14")' },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create Date data type - Enumeration constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Date,
      baseType: DataType.Date,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraintDate({ values: ["2025-05-1", "2025-05-5"] });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Date,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'date("2025-05-01"), date("2025-05-05")' },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create Date data type - Range constraint with start value included`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Date,
      baseType: DataType.Date,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDate({
      values: ["2025-05-1", "2025-05-5"],
      includeStart: true,
      includeEnd: false,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Date,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[date("2025-05-01")..date("2025-05-05"))' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create Date data type - Range constraint with end value included`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Date,
      baseType: DataType.Date,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDate({
      values: ["2025-05-1", "2025-05-5"],
      includeStart: false,
      includeEnd: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Date,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '(date("2025-05-01")..date("2025-05-05")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create Date data type - Range constraint including start and end values`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Date,
      baseType: DataType.Date,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDate({
      values: ["2025-05-1", "2025-05-5"],
      includeStart: true,
      includeEnd: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Date,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[date("2025-05-01")..date("2025-05-05")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create DateTime data type - Expression constraint`, async ({ jsonModel, dataTypes }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTime,
      baseType: DataType.DateTime,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: 'date and time("2025-05-01T01:00:00-05:00")' });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTime,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date and time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'date and time("2025-05-01T01:00:00-05:00")' },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create DateTime data type - Enumeration constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTime,
      baseType: DataType.DateTime,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraintDateTime({ date: "2025-05-5", time: "01:20:25", timezone: "-05:00" });
    await dataTypes.addEnumerationConstraintDateTime({ date: "2025-05-10", time: "10:20:30", timezone: "+05:30" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTime,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date and time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'date and time("2025-05-05T01:20:25-05:00"), date and time("2025-05-10T10:20:30+05:30")' },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create DateTime data type - Range constraint with start value included`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTime,
      baseType: DataType.DateTime,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDateTime({
      date: "2025-05-1",
      time: "01:30:15",
      timezone: "+05:30",
      range: "start",
      includeDate: true,
    });
    await dataTypes.addRangeConstraintDateTime({
      date: "2025-05-10",
      time: "01:30:15",
      timezone: "-05:00",
      range: "end",
      includeDate: false,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTime,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date and time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[date and time("2025-05-01T01:30:15+05:30")..date and time("2025-05-10T01:30:15-05:00"))' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create DateTime data type - Range constraint with end value included`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTime,
      baseType: DataType.DateTime,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDateTime({
      date: "2025-05-1",
      time: "01:30:15",
      timezone: "+05:30",
      range: "start",
      includeDate: false,
    });
    await dataTypes.addRangeConstraintDateTime({
      date: "2025-05-10",
      time: "01:30:15",
      timezone: "-05:00",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTime,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date and time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '(date and time("2025-05-01T01:30:15+05:30")..date and time("2025-05-10T01:30:15-05:00")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create DateTime data type - Range constraint including start and end values`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTime,
      baseType: DataType.DateTime,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDateTime({
      date: "2025-05-1",
      time: "01:30:15",
      timezone: "+05:30",
      range: "start",
      includeDate: true,
    });
    await dataTypes.addRangeConstraintDateTime({
      date: "2025-05-10",
      time: "01:30:15",
      timezone: "-05:00",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTime,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "date and time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[date and time("2025-05-01T01:30:15+05:30")..date and time("2025-05-10T01:30:15-05:00")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create DateTimeDuration data type - Expression constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTimeDuration,
      baseType: DataType.DateTimeDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: 'duration("P1DT03H30M30S")' });
    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTimeDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "days and time duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'duration("P1DT03H30M30S")' },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create DateTimeDuration data type - Enumeration constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTimeDuration,
      baseType: DataType.DateTimeDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraintDateTimeDuration({ days: "1", hrs: "15", mins: "30", sec: "15" });
    await dataTypes.addEnumerationConstraintDateTimeDuration({ days: "10", hrs: "30", mins: "45", sec: "35" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTimeDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "days and time duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'duration("P1DT15H30M15S"), duration("P10DT30H45M35S")' },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create DateTimeDuration data type - Range constraint with start value included`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTimeDuration,
      baseType: DataType.DateTimeDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDateTimeDuration({
      days: "1",
      hrs: "15",
      mins: "30",
      sec: "15",
      range: "start",
      includeDate: true,
    });
    await dataTypes.addRangeConstraintDateTimeDuration({
      days: "10",
      hrs: "30",
      mins: "45",
      sec: "35",
      range: "end",
      includeDate: false,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTimeDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "days and time duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[duration("P1DT15H30M15S")..duration("P10DT30H45M35S"))' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create DateTimeDuration data type - Range constraint with end value included`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTimeDuration,
      baseType: DataType.DateTimeDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDateTimeDuration({
      days: "1",
      hrs: "15",
      mins: "30",
      sec: "15",
      range: "start",
      includeDate: false,
    });
    await dataTypes.addRangeConstraintDateTimeDuration({
      days: "10",
      hrs: "30",
      mins: "45",
      sec: "35",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTimeDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "days and time duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '(duration("P1DT15H30M15S")..duration("P10DT30H45M35S")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create DateTimeDuration data type - Range constraint including start and end values`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.DateTimeDuration,
      baseType: DataType.DateTimeDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintDateTimeDuration({
      days: "1",
      hrs: "15",
      mins: "30",
      sec: "15",
      range: "start",
      includeDate: true,
    });
    await dataTypes.addRangeConstraintDateTimeDuration({
      days: "10",
      hrs: "30",
      mins: "45",
      sec: "35",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.DateTimeDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "days and time duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[duration("P1DT15H30M15S")..duration("P10DT30H45M35S")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create Time data type - Expression constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Time,
      baseType: DataType.Time,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: 'time("01:00:00+05:30")' });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Time,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'time("01:00:00+05:30")' },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create Time data type - Enumeration constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Time,
      baseType: DataType.Time,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraintTime({ time: "01:20:25", timezone: "-05:00" });
    await dataTypes.addEnumerationConstraintTime({ time: "02:30:00", timezone: "+05:30" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Time,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'time("01:20:25-05:00"), time("02:30:00+05:30")' },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create Time data type - Range constraint with start value included`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Time,
      baseType: DataType.Time,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintTime({
      time: "01:30:15",
      timezone: "-05:00",
      range: "start",
      includeDate: true,
    });
    await dataTypes.addRangeConstraintTime({
      time: "02:30:45",
      timezone: "+05:30",
      range: "end",
      includeDate: false,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Time,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[time("01:30:15-05:00")..time("02:30:45+05:30"))' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create Time data type - Range constraint with end value included`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Time,
      baseType: DataType.Time,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintTime({
      time: "01:30:15",
      timezone: "-05:00",
      range: "start",
      includeDate: false,
    });
    await dataTypes.addRangeConstraintTime({
      time: "02:30:45",
      timezone: "+05:30",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Time,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '(time("01:30:15-05:00")..time("02:30:45+05:30")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create Time data type - Range constraint including start and end values`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.Time,
      baseType: DataType.Time,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintTime({
      time: "01:30:15",
      timezone: "-05:00",
      range: "start",
      includeDate: true,
    });
    await dataTypes.addRangeConstraintTime({
      time: "02:30:45",
      timezone: "+05:30",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.Time,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "time" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[time("01:30:15-05:00")..time("02:30:45+05:30")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create YearsMonthsDuration data type - Expression constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.YearsMonthsDuration,
      baseType: DataType.YearsMonthsDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getExpressionConstraintButton().click();
    await dataTypes.addExpressionConstraint({ value: 'duration("P3Y6M")' });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.YearsMonthsDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "years and months duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'duration("P3Y6M")' },
        "@_kie:constraintType": "expression",
      },
    });
  });

  test(`Create YearsMonthsDuration data type - Enumeration constraint`, async ({ dataTypes, jsonModel }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.YearsMonthsDuration,
      baseType: DataType.YearsMonthsDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getEnumerationConstraintButton().click();
    await dataTypes.addEnumerationConstraintYearsMonthsDuration({ year: "01", month: "02" });
    await dataTypes.addEnumerationConstraintYearsMonthsDuration({ year: "02", month: "05" });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.YearsMonthsDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "years and months duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: 'duration("P01Y02M"), duration("P02Y05M")' },
        "@_kie:constraintType": "enumeration",
      },
    });
  });

  test(`Create YearsMonthsDuration data type - Range constraint with start value included`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.YearsMonthsDuration,
      baseType: DataType.YearsMonthsDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintYearsMonthsDuration({
      year: "01",
      month: "02",
      range: "start",
      includeDate: true,
    });

    await dataTypes.addRangeConstraintYearsMonthsDuration({
      year: "02",
      month: "06",
      range: "end",
      includeDate: false,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.YearsMonthsDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "years and months duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[duration("P01Y02M")..duration("P02Y06M"))' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create YearsMonthsDuration data type - Range constraint with end value included`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.YearsMonthsDuration,
      baseType: DataType.YearsMonthsDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintYearsMonthsDuration({
      year: "01",
      month: "02",
      range: "start",
      includeDate: false,
    });
    await dataTypes.addRangeConstraintYearsMonthsDuration({
      year: "02",
      month: "06",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.YearsMonthsDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "years and months duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '(duration("P01Y02M")..duration("P02Y06M")]' },
        "@_kie:constraintType": "range",
      },
    });
  });

  test(`Create YearsMonthsDuration data type - Range constraint including start and end values`, async ({
    dataTypes,
    jsonModel,
  }) => {
    await dataTypes.changeNameAndBaseType({
      newName: DefaultDataTypeName.YearsMonthsDuration,
      baseType: DataType.YearsMonthsDuration,
      description: "New Data Type Description",
    });

    await dataTypes.getRangeConstraintButton().click();
    await dataTypes.addRangeConstraintYearsMonthsDuration({
      year: "01",
      month: "02",
      range: "start",
      includeDate: true,
    });

    await dataTypes.addRangeConstraintYearsMonthsDuration({
      year: "02",
      month: "06",
      range: "end",
      includeDate: true,
    });

    const dataType = await jsonModel.drgDataType.getDataType({ drgDataTypeIndex: 0, drdIndex: 0 });
    expect(dataType).toEqual({
      "@_id": dataType["@_id"],
      "@_name": DefaultDataTypeName.YearsMonthsDuration,
      "@_isCollection": dataType["@_isCollection"],
      "@_typeLanguage": DMN15_SPEC_TYPE_LANGUAGE,
      description: { __$$text: "New Data Type Description" },
      typeRef: { __$$text: "years and months duration" },
      typeConstraint: {
        "@_id": dataType.typeConstraint?.["@_id"],
        text: { __$$text: '[duration("P01Y02M")..duration("P02Y06M")]' },
        "@_kie:constraintType": "range",
      },
    });
  });
});

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { DataField, FieldName } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions, AllActions, DataDictionaryFieldReducer } from "../../../editor/reducers";
import { Reducer } from "react";
import { HistoryService } from "../../../editor/history";

const service = new HistoryService();
const dataFields: DataField[] = [{ name: "field1" as FieldName, dataType: "boolean", optype: "categorical" }];
const pmml = { version: "1.0", DataDictionary: { DataField: dataFields }, Header: {} };
const reducer: Reducer<DataField[], AllActions> = DataDictionaryFieldReducer(service);

describe("DataDictionaryFieldReducer::Valid actions", () => {
  test("Actions.UpdateDataDictionaryField", () => {
    reducer(dataFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: 0,
        dataField: { name: "updated" as FieldName, dataType: "string", optype: "ordinal" },
        originalName: "field1" as FieldName
      }
    });
    const updated = service.commit(pmml)?.DataDictionary.DataField as DataField[];

    expect(updated).not.toEqual(dataFields);
    expect(updated.length).toBe(1);
    expect(updated[0].name).toBe("updated");
    expect(updated[0].dataType).toBe("string");
    expect(updated[0].optype).toBe("ordinal");
  });

  test("Actions.SetDataFieldName::Index out of bounds (less than 0)", () => {
    reducer(dataFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: -1,
        dataField: { name: "updated" as FieldName, dataType: "boolean", optype: "categorical" },
        originalName: "field1" as FieldName
      }
    });
    const updated = service.commit(pmml)?.DataDictionary.DataField as DataField[];

    expect(updated).toEqual(dataFields);
  });

  test("Actions.SetDataFieldName::Index out of bounds (greater than number of fields)", () => {
    reducer(dataFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: 1,
        dataField: { name: "updated" as FieldName, dataType: "boolean", optype: "categorical" },
        originalName: "field1" as FieldName
      }
    });
    const updated = service.commit(pmml)?.DataDictionary.DataField as DataField[];

    expect(updated).toEqual(dataFields);
  });
});

describe("DataFieldReducer::Invalid actions", () => {
  test("Actions.SetHeaderDescription", () => {
    const updated: DataField[] = reducer(dataFields, {
      type: Actions.SetHeaderDescription,
      payload: {
        description: "description"
      }
    });
    expect(updated).toEqual(dataFields);
  });
});

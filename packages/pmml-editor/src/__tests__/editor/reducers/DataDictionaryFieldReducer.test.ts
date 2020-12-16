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

const dataFields: DataField[] = [{ name: "field1" as FieldName, dataType: "boolean", optype: "categorical" }];

const reducer: Reducer<DataField[], AllActions> = DataDictionaryFieldReducer(new HistoryService());

describe("DataDictionaryFieldReducer::Valid actions", () => {
  test("Actions.UpdateDataDictionaryField", () => {
    const updated: DataField[] = reducer(dataFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: 0,
        dataField: { name: "updated" as FieldName, dataType: "boolean", optype: "categorical" }
      }
    });
    expect(updated).not.toEqual(dataFields);
    expect(updated.length).toBe(1);
    expect(updated[0].name).toBe("updated");
  });

  test("Actions.SetDataFieldName::Index out of bounds (less than 0)", () => {
    const updated: DataField[] = reducer(dataFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: -1,
        dataField: { name: "updated" as FieldName, dataType: "boolean", optype: "categorical" }
      }
    });
    expect(updated).toEqual(dataFields);
  });

  test("Actions.SetDataFieldName::Index out of bounds (greater than number of fields)", () => {
    const updated: DataField[] = reducer(dataFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: 1,
        dataField: { name: "updated" as FieldName, dataType: "boolean", optype: "categorical" }
      }
    });
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

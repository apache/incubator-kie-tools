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
import { DataDictionary, FieldName } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions, AllActions, DataDictionaryReducer } from "../../../editor/reducers";
import { Reducer } from "react";
import { HistoryService } from "../../../editor/history";

const dataDictionary: DataDictionary = { DataField: [] };

const reducer: Reducer<DataDictionary, AllActions> = DataDictionaryReducer(new HistoryService());

describe("DataDictionaryReducer::Valid actions", () => {
  test("Actions.CreateDataField", () => {
    const updated: DataDictionary = reducer(dataDictionary, {
      type: Actions.CreateDataField,
      payload: {
        name: "field1"
      }
    });
    expect(updated).not.toEqual(dataDictionary);
    expect(updated.DataField.length).toBe(1);
    expect(updated.DataField[0].name).toBe("field1");
  });

  test("Actions.DeleteDataField", () => {
    const updated: DataDictionary = reducer(
      {
        DataField: [
          {
            name: "field1" as FieldName,
            dataType: "string",
            optype: "categorical"
          }
        ]
      },
      {
        type: Actions.DeleteDataField,
        payload: {
          index: 0
        }
      }
    );
    expect(updated).toEqual(dataDictionary);
    expect(updated.DataField.length).toEqual(0);
  });

  test("Actions.DeleteDataField::Index out of bounds (less than 0)", () => {
    const updated: DataDictionary = reducer(dataDictionary, {
      type: Actions.DeleteDataField,
      payload: {
        index: -1
      }
    });
    expect(updated).toEqual(dataDictionary);
  });

  test("Actions.DeleteDataField::Index out of bounds (greater than number of fields)", () => {
    const updated: DataDictionary = reducer(dataDictionary, {
      type: Actions.DeleteDataField,
      payload: {
        index: 0
      }
    });
    expect(updated).toEqual(dataDictionary);
  });
});

describe("DataDictionaryReducer::Invalid actions", () => {
  test("Actions.SetHeaderDescription", () => {
    const updated: DataDictionary = reducer(dataDictionary, {
      type: Actions.SetHeaderDescription,
      payload: {
        description: "description"
      }
    });
    expect(updated).toEqual(dataDictionary);
  });
});

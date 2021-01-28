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
import { ValidationService } from "../../../editor/validation";

const historyService = new HistoryService();
const validationService = new ValidationService();
const dataDictionary: DataDictionary = { DataField: [] };
const pmml = { version: "1.0", DataDictionary: dataDictionary, Header: {} };
const reducer: Reducer<DataDictionary, AllActions> = DataDictionaryReducer(historyService, validationService);

describe("DataDictionaryReducer::Valid actions", () => {
  test("Actions.AddDataDictionaryField", () => {
    reducer(dataDictionary, {
      type: Actions.AddDataDictionaryField,
      payload: {
        name: "field1",
        type: "string",
        optype: "categorical"
      }
    });

    const updated = historyService.commit(pmml)?.DataDictionary as DataDictionary;

    expect(updated).not.toEqual(dataDictionary);
    expect(updated.DataField.length).toBe(1);
    expect(updated.DataField[0].name).toBe("field1");
    expect(updated.DataField[0].dataType).toBe("string");
    expect(updated.DataField[0].optype).toBe("categorical");
  });

  test("Actions.DeleteDataDictionaryField", () => {
    reducer(
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
        type: Actions.DeleteDataDictionaryField,
        payload: {
          index: 0
        }
      }
    );

    const updated = historyService.commit(pmml)?.DataDictionary as DataDictionary;

    expect(updated).toEqual(dataDictionary);
    expect(updated.DataField.length).toEqual(0);
  });

  test("Actions.DeleteDataField::Index out of bounds (less than 0)", () => {
    const updated: DataDictionary = reducer(dataDictionary, {
      type: Actions.DeleteDataDictionaryField,
      payload: {
        index: -1
      }
    });
    expect(updated).toEqual(dataDictionary);
  });

  test("Actions.DeleteDataField::Index out of bounds (greater than number of fields)", () => {
    const updated: DataDictionary = reducer(dataDictionary, {
      type: Actions.DeleteDataDictionaryField,
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

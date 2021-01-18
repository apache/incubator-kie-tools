/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { FieldName, MiningField, Model, PMML, Scorecard } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions, AllActions } from "../../../editor/reducers";
import { Reducer } from "react";
import { HistoryService } from "../../../editor/history";
import { DelegatingModelReducer } from "../../../editor/reducers/DelegatingModelReducer";

const service = new HistoryService();
const miningFields: MiningField[] = [{ name: "field1" as FieldName }];
const models: Model[] = [
  new Scorecard({
    MiningSchema: { MiningField: miningFields },
    functionName: "regression",
    Characteristics: { Characteristic: [] }
  }),
  new Scorecard({
    MiningSchema: { MiningField: miningFields },
    functionName: "regression",
    Characteristics: { Characteristic: [] }
  })
];
const pmml: PMML = {
  version: "1.0",
  DataDictionary: { DataField: [{ name: "field1" as FieldName, dataType: "boolean", optype: "categorical" }] },
  Header: {},
  models: models
};

const mockReducer: any = jest.fn();

const reducer: Reducer<Model[], AllActions> = DelegatingModelReducer(
  service,
  new Map([
    [
      "Scorecard",
      {
        reducer: mockReducer,
        factory: jest.fn()
      }
    ]
  ])
);

beforeEach(() => jest.resetAllMocks());

describe("DelegatingModelReducer::Valid actions", () => {
  test("ModelAgnosticActions.UpdateDataDictionaryField", () => {
    reducer(models, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        dataDictionaryIndex: 0,
        dataField: { name: "updated" as FieldName, dataType: "string", optype: "ordinal" },
        originalName: "field1" as FieldName
      }
    });
    service.commit(pmml);

    expect(mockReducer).toBeCalled();
    //mock.calls[invocationIndex][parameterIndex]....
    expect(mockReducer.mock.calls.length).toBe(2);
    expect(mockReducer.mock.calls[0][1].payload.modelIndex).toBe(0);
    expect(mockReducer.mock.calls[1][1].payload.modelIndex).toBe(1);
  });

  test("ModelSpecificActions.UpdateMiningSchemaField::Model 2", () => {
    reducer(models, {
      type: Actions.UpdateMiningSchemaField,
      payload: {
        modelIndex: 1,
        miningSchemaIndex: 0,
        name: "updated" as FieldName,
        usageType: undefined,
        optype: undefined,
        importance: undefined,
        outliers: undefined,
        lowValue: undefined,
        highValue: undefined,
        missingValueReplacement: undefined,
        missingValueTreatment: undefined,
        invalidValueTreatment: undefined,
        invalidValueReplacement: undefined
      }
    });
    service.commit(pmml);

    expect(mockReducer).toBeCalled();
    //mock.calls[invocationIndex][parameterIndex]....
    expect(mockReducer.mock.calls.length).toBe(1);
    expect(mockReducer.mock.calls[0][1].payload.modelIndex).toBe(1);
  });
});

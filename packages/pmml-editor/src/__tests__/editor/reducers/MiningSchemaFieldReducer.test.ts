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
import { MiningSchemaFieldReducer } from "../../../editor/reducers/MiningSchemaFieldReducer";

const service = new HistoryService();
const miningFields: MiningField[] = [{ name: "field1" as FieldName }];
const models: Model[] = [
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
const reducer: Reducer<MiningField[], AllActions> = MiningSchemaFieldReducer(service);

describe("MiningSchemaFieldReducer::Valid actions", () => {
  test("Actions.UpdateDataDictionaryField", () => {
    reducer(miningFields, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        modelIndex: 0,
        dataDictionaryIndex: 0,
        dataField: { name: "updated" as FieldName, dataType: "string", optype: "ordinal" },
        originalName: "field1" as FieldName
      }
    });
    const updated = service.commit(pmml)?.models as Model[];
    const miningSchema = (updated[0] as Scorecard).MiningSchema;

    expect(miningSchema.MiningField).not.toEqual(miningFields);
    expect(miningSchema.MiningField.length).toBe(1);
    expect(miningSchema.MiningField[0].name).toBe("updated");
  });

  test("Actions.UpdateMiningSchemaField", () => {
    reducer(miningFields, {
      type: Actions.UpdateMiningSchemaField,
      payload: {
        modelIndex: 0,
        miningSchemaIndex: 0,
        name: "updated" as FieldName,
        usageType: "active",
        optype: "ordinal",
        importance: 5,
        outliers: "asExtremeValues",
        lowValue: -100,
        highValue: 120,
        missingValueReplacement: "a",
        missingValueTreatment: "asIs",
        invalidValueTreatment: "asValue",
        invalidValueReplacement: "b"
      }
    });
    const updated = service.commit(pmml)?.models as Model[];
    const miningSchema = (updated[0] as Scorecard).MiningSchema;

    expect(miningSchema.MiningField[0]).not.toEqual(miningFields[0]);
    expect(miningSchema.MiningField[0].name).toBe("updated");
    expect(miningSchema.MiningField[0].usageType).toBe("active");
    expect(miningSchema.MiningField[0].optype).toBe("ordinal");
    expect(miningSchema.MiningField[0].importance).toBe(5);
    expect(miningSchema.MiningField[0].outliers).toBe("asExtremeValues");
    expect(miningSchema.MiningField[0].lowValue).toBe(-100);
    expect(miningSchema.MiningField[0].highValue).toBe(120);
    expect(miningSchema.MiningField[0].missingValueReplacement).toBe("a");
    expect(miningSchema.MiningField[0].missingValueTreatment).toBe("asIs");
    expect(miningSchema.MiningField[0].invalidValueReplacement).toBe("b");
    expect(miningSchema.MiningField[0].invalidValueTreatment).toBe("asValue");
  });
});

describe("DataFieldReducer::Invalid actions", () => {
  test("Actions.SetHeaderDescription", () => {
    const updated: MiningField[] = reducer(miningFields, {
      type: Actions.SetHeaderDescription,
      payload: {
        description: "description"
      }
    });
    expect(updated).toEqual(miningFields);
  });
});

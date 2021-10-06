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
import { Model, Scorecard, PMML } from "@kogito-tooling/pmml-editor-marshaller";
import { Actions, AllActions, ModelReducer } from "@kogito-tooling/pmml-editor/dist/editor/reducers";
import { Reducer } from "react";
import { HistoryService } from "@kogito-tooling/pmml-editor/dist/editor/history";
import { ValidationRegistry } from "@kogito-tooling/pmml-editor/dist/editor/validation";

const historyService = new HistoryService([]);
const validationRegistry = new ValidationRegistry();
const reducer: Reducer<Model[], AllActions> = ModelReducer(historyService, validationRegistry);

describe("ModelReducer::Valid actions", () => {
  test("Actions.DeleteModel", () => {
    const scorecard1: Scorecard = new Scorecard({
      MiningSchema: { MiningField: [] },
      functionName: "regression",
      Characteristics: { Characteristic: [] },
    });
    const scorecard2: Scorecard = new Scorecard({
      MiningSchema: { MiningField: [] },
      functionName: "classification",
      Characteristics: { Characteristic: [] },
    });
    const models = [scorecard1, scorecard2];
    const pmml: PMML = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: models };

    reducer(models, {
      type: Actions.DeleteModel,
      payload: {
        modelIndex: 0,
      },
    });

    const updated: Model[] = historyService.commit(pmml)?.models as Model[];

    expect(updated).not.toEqual(models);
    expect(updated).not.toBeUndefined();
    expect(updated.length).toBe(1);
    expect(updated[0]).toBeInstanceOf(Scorecard);
    const updatedScorecard: Scorecard = updated[0] as Scorecard;
    expect(updatedScorecard).toBe(scorecard2);
  });

  test("Actions.DeleteModel::Model not in PMML", () => {
    const scorecard1: Scorecard = new Scorecard({
      MiningSchema: { MiningField: [] },
      functionName: "regression",
      Characteristics: { Characteristic: [] },
    });
    const models = [scorecard1];
    const pmml: PMML = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: models };

    reducer(models, {
      type: Actions.DeleteModel,
      payload: {
        modelIndex: 1,
      },
    });

    const updated: Model[] = historyService.commit(pmml)?.models as Model[];

    expect(updated).toEqual(models);
  });
});

describe("ModelReducer::Invalid actions", () => {
  test("Actions.SetHeaderDescription", () => {
    const models: Model[] = [];
    const updated: Model[] = reducer(models, {
      type: Actions.SetHeaderDescription,
      payload: {
        description: "description",
      },
    });
    expect(updated).toEqual(models);
  });
});

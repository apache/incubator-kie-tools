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
import {
  CompoundPredicate,
  FieldName,
  Model,
  PMML,
  Predicate,
  Scorecard,
  SimplePredicate,
} from "@kogito-tooling/pmml-editor-marshaller";
import { Actions, AllActions, ScorecardReducer } from "@kogito-tooling/pmml-editor/dist/editor/reducers";
import { Reducer } from "react";
import { HistoryService } from "@kogito-tooling/pmml-editor/dist/editor/history";
import { ValidationRegistry } from "@kogito-tooling/pmml-editor/dist/editor/validation";

const historyService = new HistoryService([]);
const validationRegistry = new ValidationRegistry();
const reducer: Reducer<Scorecard, AllActions> = ScorecardReducer(historyService, validationRegistry);

describe("ScorecardReducer::Valid actions", () => {
  test("Actions.Scorecard_SetModelName", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: { Characteristic: [] },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_SetModelName,
      payload: {
        modelIndex: 0,
        modelName: "modelName",
      },
    });
    const models = historyService.commit(pmml)?.models as Model[];
    const updated = models[0] as Scorecard;

    expect(updated.modelName).toBe("modelName");
  });

  test("Actions.Scorecard_SetCoreProperties", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: { Characteristic: [] },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_SetCoreProperties,
      payload: {
        modelIndex: 0,
        algorithmName: "algorithmName",
        functionName: "regression",
        baselineMethod: "min",
        initialScore: 1.0,
        baselineScore: 2.0,
        isScorable: true,
        reasonCodeAlgorithm: "pointsAbove",
        useReasonCodes: true,
      },
    });
    const models = historyService.commit(pmml)?.models as Model[];
    const updated = models[0] as Scorecard;

    expect(updated.algorithmName).toBe("algorithmName");
    expect(updated.functionName).toBe("regression");
    expect(updated.baselineMethod).toBe("min");
    expect(updated.initialScore).toBe(1.0);
    expect(updated.baselineScore).toBe(2.0);
    expect(updated.isScorable).toBeTruthy();
    expect(updated.reasonCodeAlgorithm).toBe("pointsAbove");
    expect(updated.useReasonCodes).toBeTruthy();
  });

  test("Actions.Scorecard_SetCoreProperties::clearReasonCodesWhenUseReasonCodesIsFalse", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            Attribute: [{ reasonCode: "AttributeReasonCode" }],
            reasonCode: "CharacteristicReasonCode",
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_SetCoreProperties,
      payload: {
        modelIndex: 0,
        algorithmName: "algorithmName",
        functionName: "regression",
        baselineMethod: "min",
        initialScore: 1.0,
        baselineScore: 2.0,
        isScorable: true,
        reasonCodeAlgorithm: "pointsAbove",
        useReasonCodes: false,
      },
    });

    expect(scorecard.Characteristics.Characteristic[0].reasonCode).toBe("CharacteristicReasonCode");
    expect(scorecard.Characteristics.Characteristic[0].Attribute[0].reasonCode).toBe("AttributeReasonCode");

    const models = historyService.commit(pmml)?.models as Model[];
    const updated = models[0] as Scorecard;

    expect(updated.Characteristics.Characteristic[0].reasonCode).toBeUndefined();
    expect(updated.Characteristics.Characteristic[0].Attribute[0].reasonCode).toBeUndefined();
  });

  test("Actions.Scorecard_SetCoreProperties::clearBaselineScoresWhenBaselineScoreExists", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            Attribute: [],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_SetCoreProperties,
      payload: {
        modelIndex: 0,
        algorithmName: "algorithmName",
        functionName: "regression",
        baselineMethod: "min",
        initialScore: 1.0,
        baselineScore: 2.0,
        isScorable: true,
        reasonCodeAlgorithm: "pointsAbove",
        useReasonCodes: false,
      },
    });

    expect(scorecard.Characteristics.Characteristic[0].baselineScore).toBe(1.0);

    const models = historyService.commit(pmml)?.models as Model[];
    const updated = models[0] as Scorecard;

    expect(updated.Characteristics.Characteristic[0].baselineScore).toBeUndefined();
  });

  test("Actions.Scorecard_AddCharacteristic", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: { Characteristic: [] },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_AddCharacteristic,
      payload: {
        modelIndex: 0,
        name: "characteristicName",
        baselineScore: 1.0,
        reasonCode: "characteristicReasonCode",
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(0);
  });

  test("Actions.Scorecard_DeleteCharacteristic", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_DeleteCharacteristic,
      payload: {
        modelIndex: 0,
        characteristicIndex: 0,
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(1);
  });

  test("Actions.Scorecard_UpdateCharacteristic", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_UpdateCharacteristic,
      payload: {
        modelIndex: 0,
        characteristicIndex: 0,
        name: "updatedCharacteristicName",
        reasonCode: "updatedCharacteristicReasonCode",
        baselineScore: 2.0,
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(1);
  });

  test("Actions.Scorecard_AddAttribute", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [{ reasonCode: "attributeReasonCode", partialScore: 1.0, predicate: {} }],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_AddAttribute,
      payload: {
        modelIndex: 0,
        characteristicIndex: 0,
        partialScore: 1.0,
        reasonCode: "attributeReasonCode",
        predicate: {},
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute.length).toBe(1);
  });

  test("Actions.Scorecard_DeleteAttribute", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [{ reasonCode: "attributeReasonCode", partialScore: 1.0, predicate: {} }],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_DeleteAttribute,
      payload: {
        modelIndex: 0,
        characteristicIndex: 0,
        attributeIndex: 0,
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute.length).toBe(1);
  });

  test("Actions.AddMiningSchemaFields", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.AddMiningSchemaFields,
      payload: {
        modelIndex: 0,
        names: ["miningSchemaField1" as FieldName],
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute.length).toBe(0);
  });

  test("Actions.DeleteMiningSchemaField", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [{ name: "miningSchemaField" as FieldName }] },
      Characteristics: {
        Characteristic: [],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.DeleteMiningSchemaField,
      payload: {
        modelIndex: 0,
        miningSchemaIndex: 0,
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.MiningSchema.MiningField.length).toBe(1);
  });

  test("Actions.Scorecard_UpdateAttribute", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [{ reasonCode: "attributeReasonCode", partialScore: 1.0, predicate: {} }],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml = { version: "1.0", DataDictionary: { DataField: [] }, Header: {}, models: [scorecard] };

    reducer(scorecard, {
      type: Actions.Scorecard_UpdateAttribute,
      payload: {
        modelIndex: 0,
        characteristicIndex: 0,
        attributeIndex: 0,
        reasonCode: "updatedAttributeReasonCode",
        partialScore: 2.0,
        predicate: new SimplePredicate({ field: "field" as FieldName, operator: "equal", value: 100 }),
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    //ScorecardReducer only validates Characteristics for this action
    expect(updated.Characteristics.Characteristic.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute.length).toBe(1);
  });

  test("Actions.UpdateDataDictionaryField::SimplePredicate", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [
              {
                reasonCode: "attributeReasonCode",
                partialScore: 1.0,
                predicate: new SimplePredicate({
                  field: "dataField" as FieldName,
                  operator: "equal",
                  value: 100,
                }),
              },
            ],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml: PMML = {
      version: "1.0",
      DataDictionary: { DataField: [{ name: "dataField" as FieldName, dataType: "string", optype: "categorical" }] },
      Header: {},
      models: [scorecard],
    };

    reducer(scorecard, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        modelIndex: 0,
        dataField: { name: "updatedDataField" as FieldName, dataType: "string", optype: "categorical" },
        originalName: "dataField" as FieldName,
        dataDictionaryIndex: 0,
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    expect(updated.Characteristics.Characteristic.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute[0].predicate).toBeInstanceOf(SimplePredicate);

    const predicate: SimplePredicate = updated.Characteristics.Characteristic[0].Attribute[0]
      .predicate as SimplePredicate;
    expect(predicate.field as string).toBe("updatedDataField");
  });

  test("Actions.UpdateDataDictionaryField::CompoundPredicate", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: {
        Characteristic: [
          {
            name: "characteristic1",
            baselineScore: 1.0,
            reasonCode: "characteristicReasonCode",
            Attribute: [
              {
                reasonCode: "attributeReasonCode",
                partialScore: 1.0,
                predicate: new CompoundPredicate({
                  predicates: [
                    new SimplePredicate({
                      field: "dataField" as FieldName,
                      operator: "greaterThan",
                      value: 100,
                    }),
                    new SimplePredicate({
                      field: "dataField" as FieldName,
                      operator: "lessOrEqual",
                      value: 200,
                    }),
                  ],
                  booleanOperator: "and",
                }),
              },
            ],
          },
        ],
      },
      functionName: "regression",
    };
    const pmml: PMML = {
      version: "1.0",
      DataDictionary: { DataField: [{ name: "dataField" as FieldName, dataType: "string", optype: "categorical" }] },
      Header: {},
      models: [scorecard],
    };

    reducer(scorecard, {
      type: Actions.UpdateDataDictionaryField,
      payload: {
        modelIndex: 0,
        dataField: { name: "updatedDataField" as FieldName, dataType: "string", optype: "categorical" },
        originalName: "dataField" as FieldName,
        dataDictionaryIndex: 0,
      },
    });

    const models = historyService.commit(pmml)?.models as Model[];
    expect(models).not.toBeUndefined();
    const updated = models[0] as Scorecard;

    expect(updated.Characteristics.Characteristic.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute.length).toBe(1);
    expect(updated.Characteristics.Characteristic[0].Attribute[0].predicate).toBeInstanceOf(CompoundPredicate);

    const compoundPredicate: CompoundPredicate = updated.Characteristics.Characteristic[0].Attribute[0]
      .predicate as CompoundPredicate;
    expect(compoundPredicate.predicates).not.toBeUndefined();
    const predicates: Predicate[] = compoundPredicate.predicates as Predicate[];
    expect(predicates.length).toBe(2);
    expect(predicates[0]).toBeInstanceOf(SimplePredicate);
    expect(predicates[1]).toBeInstanceOf(SimplePredicate);
    const simplePredicate0: SimplePredicate = predicates[0] as SimplePredicate;
    expect(simplePredicate0.field as string).toBe("updatedDataField");
    const simplePredicate1: SimplePredicate = predicates[1] as SimplePredicate;
    expect(simplePredicate1.field as string).toBe("updatedDataField");
  });
});

describe("ScorecardReducer::Invalid actions", () => {
  test("Actions.SetHeaderDescription", () => {
    const scorecard: Scorecard = {
      MiningSchema: { MiningField: [] },
      Characteristics: { Characteristic: [] },
      functionName: "regression",
    };

    const updated: Scorecard = reducer(scorecard, {
      type: Actions.SetHeaderDescription,
      payload: {
        description: "description",
      },
    });

    expect(updated).toEqual(scorecard);
  });
});

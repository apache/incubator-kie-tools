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

import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import {
  BaselineMethod,
  CompoundPredicate,
  False,
  MiningFunction,
  Predicate,
  ReasonCodeAlgorithm,
  Scorecard,
  SimplePredicate,
  SimpleSetPredicate,
  True,
} from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { immerable } from "immer";
import { CharacteristicsActions } from "./CharacteristicsReducer";
import { CharacteristicActions } from "./CharacteristicReducer";
import { AttributesActions } from "./AttributesReducer";
import { validateOutputs } from "../validation/Outputs";
import { ValidationRegistry } from "../validation";
import { Builder, PredicateBuilder } from "../paths";
import { validateCharacteristic, validateCharacteristics } from "../validation/Characteristics";
import { validateBaselineScore } from "../validation/ModelCoreProperties";
import { getBaselineScore, getCharacteristics, getMiningSchema, getUseReasonCodes } from "../PMMLModelHelper";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
Scorecard[immerable] = true;

interface ScorecardPayload {
  [Actions.Scorecard_SetModelName]: {
    readonly modelIndex: number;
    readonly modelName: string;
  };
  [Actions.Scorecard_SetCoreProperties]: {
    readonly modelIndex: number;
    readonly isScorable: boolean;
    readonly functionName: MiningFunction;
    readonly algorithmName: string;
    readonly baselineScore: number;
    readonly baselineMethod: BaselineMethod;
    readonly initialScore: number;
    readonly useReasonCodes: boolean;
    readonly reasonCodeAlgorithm: ReasonCodeAlgorithm;
  };
}

export type ScorecardActions = ActionMap<ScorecardPayload>[keyof ActionMap<ScorecardPayload>];

export type AllScorecardActions = ScorecardActions | CharacteristicsActions | CharacteristicActions | AttributesActions;

export const ScorecardReducer: HistoryAwareValidatingReducer<Scorecard, AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<Scorecard, AllActions> => {
  return (state: Scorecard, action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_SetModelName:
        historyService.batch(state, Builder().forModel(action.payload.modelIndex).build(), (draft) => {
          draft.modelName = action.payload.modelName;
        });
        break;

      case Actions.Scorecard_SetCoreProperties:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).build(),
          (draft) => {
            draft.isScorable = action.payload.isScorable;
            draft.functionName = action.payload.functionName;
            draft.algorithmName = action.payload.algorithmName;
            draft.baselineScore = action.payload.baselineScore;
            draft.baselineMethod = action.payload.baselineMethod;
            draft.initialScore = action.payload.initialScore;
            draft.useReasonCodes = action.payload.useReasonCodes;
            draft.reasonCodeAlgorithm = action.payload.reasonCodeAlgorithm;
            if (!(action.payload.useReasonCodes === undefined || action.payload.useReasonCodes)) {
              draft.Characteristics.Characteristic.forEach((characteristic) => {
                characteristic.reasonCode = undefined;
                characteristic.Attribute.forEach((attribute) => (attribute.reasonCode = undefined));
              });
            }
            if (action.payload.baselineScore !== undefined) {
              draft.Characteristics.Characteristic.forEach((characteristic) => {
                characteristic.baselineScore = undefined;
              });
            }
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const characteristics = getCharacteristics(pmml, modelIndex);
            const baselineScore = getBaselineScore(pmml, modelIndex);
            const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
            if (miningSchema !== undefined && characteristics !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forBaselineScore().build());
              validateBaselineScore(
                modelIndex,
                useReasonCodes,
                baselineScore,
                characteristics.Characteristic,
                validationRegistry
              );
              validationRegistry.clear(Builder().forModel(modelIndex).forCharacteristics().build());
              validateCharacteristics(
                modelIndex,
                {
                  baselineScore: baselineScore,
                  useReasonCodes: useReasonCodes,
                },
                characteristics.Characteristic,
                miningSchema.MiningField,
                validationRegistry
              );
            }
          }
        );
        break;

      case Actions.Scorecard_AddCharacteristic:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).build(),
          () => {
            // No changes to model. Only validation is required.
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const characteristics = getCharacteristics(pmml, modelIndex);
            const baselineScore = getBaselineScore(pmml, modelIndex);
            const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
            if (miningSchema !== undefined && characteristics !== undefined) {
              validationRegistry.clear(Builder().forModel(action.payload.modelIndex).forBaselineScore().build());
              validateBaselineScore(
                modelIndex,
                useReasonCodes,
                baselineScore,
                characteristics.Characteristic,
                validationRegistry
              );
              validationRegistry.clear(Builder().forModel(modelIndex).forCharacteristics().build());
              validateCharacteristics(
                modelIndex,
                { baselineScore: baselineScore, useReasonCodes: useReasonCodes },
                characteristics.Characteristic,
                miningSchema.MiningField,
                validationRegistry
              );
            }
          }
        );
        break;

      case Actions.Scorecard_DeleteCharacteristic:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).build(),
          () => {
            // No changes to model. Only validation is required.
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const characteristics = getCharacteristics(pmml, modelIndex);
            const baselineScore = getBaselineScore(pmml, modelIndex);
            const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
            if (characteristics !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forBaselineScore().build());
              validateBaselineScore(
                modelIndex,
                useReasonCodes,
                baselineScore,
                characteristics.Characteristic,
                validationRegistry
              );
            }
          }
        );
        break;

      case Actions.Scorecard_UpdateCharacteristic:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).build(),
          () => {
            // No changes to model. Only validation is required.
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const characteristics = getCharacteristics(pmml, modelIndex);
            const baselineScore = getBaselineScore(pmml, modelIndex);
            const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
            if (miningSchema !== undefined && characteristics !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forBaselineScore().build());
              validateBaselineScore(
                modelIndex,
                useReasonCodes,
                baselineScore,
                characteristics.Characteristic,
                validationRegistry
              );
              validationRegistry.clear(Builder().forModel(modelIndex).forCharacteristics().build());
              validateCharacteristics(
                modelIndex,
                { baselineScore: baselineScore, useReasonCodes: useReasonCodes },
                characteristics.Characteristic,
                miningSchema.MiningField,
                validationRegistry
              );
            }
          }
        );
        break;

      case Actions.Scorecard_AddAttribute:
      case Actions.Scorecard_DeleteAttribute:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).build(),
          () => {
            // No changes to model. Only validation is required.
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const characteristicIndex = action.payload.characteristicIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const characteristics = getCharacteristics(pmml, modelIndex);
            const baselineScore = getBaselineScore(pmml, modelIndex);
            const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
            if (miningSchema !== undefined && characteristics !== undefined) {
              validationRegistry.clear(
                Builder().forModel(modelIndex).forCharacteristics().forCharacteristic(characteristicIndex).build()
              );
              validateCharacteristic(
                modelIndex,
                { baselineScore: baselineScore, useReasonCodes: useReasonCodes },
                characteristicIndex,
                characteristics.Characteristic[characteristicIndex],
                miningSchema.MiningField,
                validationRegistry
              );
            }
          }
        );
        break;

      case Actions.UpdateDataDictionaryField:
        if (action.payload.modelIndex !== undefined) {
          const modelIndex = action.payload.modelIndex;
          const dataFieldName = action.payload.dataField.name;
          const originalDataFieldName = action.payload.originalName;
          state.Characteristics.Characteristic.forEach((characteristic, characteristicIndex) => {
            characteristic.Attribute.forEach((attribute, attributeIndex) => {
              updatePredicateFieldName(
                Builder()
                  .forModel(modelIndex)
                  .forCharacteristics()
                  .forCharacteristic(characteristicIndex)
                  .forAttribute(attributeIndex)
                  .forPredicate(),
                attribute.predicate,
                dataFieldName,
                originalDataFieldName,
                historyService
              );
            });
          });
        }
        break;

      case Actions.AddMiningSchemaFields:
      case Actions.Scorecard_UpdateAttribute:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).build(),
          () => {
            // No changes to model. Only validation is required.
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const characteristics = getCharacteristics(pmml, modelIndex);
            const baselineScore = getBaselineScore(pmml, modelIndex);
            const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
            if (miningSchema !== undefined && characteristics !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forCharacteristics().build());
              validateCharacteristics(
                action.payload.modelIndex,
                { baselineScore: baselineScore, useReasonCodes: useReasonCodes },
                characteristics.Characteristic,
                miningSchema.MiningField,
                validationRegistry
              );
            }
          }
        );
        break;

      case Actions.DeleteMiningSchemaField:
        if (state.MiningSchema.MiningField.length > 0) {
          const modelIndex = action.payload.modelIndex;
          historyService.batch(
            state,
            Builder().forModel(modelIndex).build(),
            (draft) => {
              // No changes to model. Only validation is required.
            },
            (pmml) => {
              const miningSchema = getMiningSchema(pmml, modelIndex);
              const characteristics = getCharacteristics(pmml, modelIndex);
              const baselineScore = getBaselineScore(pmml, modelIndex);
              const useReasonCodes = getUseReasonCodes(pmml, modelIndex);
              if (miningSchema !== undefined && characteristics !== undefined) {
                validationRegistry.clear(Builder().forModel(modelIndex).forCharacteristics().build());
                validateCharacteristics(
                  modelIndex,
                  { baselineScore: baselineScore, useReasonCodes: useReasonCodes },
                  characteristics.Characteristic,
                  miningSchema.MiningField,
                  validationRegistry
                );
              }
            }
          );
        }
        break;

      case Actions.Validate:
        if (action.payload.modelIndex !== undefined) {
          const modelIndex = action.payload.modelIndex;
          validationRegistry.clear(Builder().forModel(modelIndex).forOutput().build());
          validateOutputs(
            modelIndex,
            state.Output?.OutputField ?? [],
            state.MiningSchema.MiningField,
            validationRegistry
          );

          validationRegistry.clear(Builder().forModel(modelIndex).forBaselineScore().build());
          validateBaselineScore(
            modelIndex,
            state.useReasonCodes,
            state.baselineScore,
            state.Characteristics.Characteristic,
            validationRegistry
          );

          validationRegistry.clear(Builder().forModel(modelIndex).forCharacteristics().build());
          validateCharacteristics(
            modelIndex,
            { baselineScore: state.baselineScore, useReasonCodes: state.useReasonCodes },
            state.Characteristics.Characteristic,
            state.MiningSchema.MiningField,
            validationRegistry
          );
        }
    }

    return state;
  };
};

const updatePredicateFieldName = (
  pathBuilder: PredicateBuilder,
  predicate: Predicate | undefined,
  name: string,
  originalName: string | undefined,
  service: HistoryService
) => {
  if (predicate === undefined) {
    return;
  } else if (predicate instanceof True) {
    return;
  } else if (predicate instanceof False) {
    return;
  }
  if (predicate instanceof SimpleSetPredicate) {
    if (originalName === predicate.field) {
      service.batch(predicate, pathBuilder.build(), (draft) => {
        draft.field = name;
      });
    }
  } else if (predicate instanceof SimplePredicate) {
    if (originalName === predicate.field) {
      service.batch(predicate, pathBuilder.build(), (draft) => {
        draft.field = name;
      });
    }
  } else if (predicate instanceof CompoundPredicate) {
    const cp: CompoundPredicate = predicate as CompoundPredicate;
    cp.predicates?.forEach((p, i) =>
      updatePredicateFieldName(pathBuilder.forPredicate(i), p, name, originalName, service)
    );
  }
};

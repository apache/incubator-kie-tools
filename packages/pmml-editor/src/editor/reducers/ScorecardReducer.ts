/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import {
  BaselineMethod,
  CompoundPredicate,
  False,
  FieldName,
  MiningField,
  MiningFunction,
  Predicate,
  ReasonCodeAlgorithm,
  Scorecard,
  SimplePredicate,
  SimpleSetPredicate,
  True
} from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { immerable } from "immer";
import { CharacteristicsActions } from "./CharacteristicsReducer";
import { CharacteristicActions } from "./CharacteristicReducer";
import { AttributesActions } from "./AttributesReducer";
import { isOutputsTargetFieldRequired, validateOutput, validateOutputs } from "../validation/Outputs";
import { Builder, ValidationEntry, ValidationLevel, ValidationRegistry } from "../validation";
import { validateCharacteristics } from "../validation/Characteristics";

// @ts-ignore
Scorecard[immerable] = true;

interface ScorecardPayload {
  [Actions.Scorecard_SetModelName]: {
    readonly modelIndex: number;
    readonly modelName: string;
  };
  [Actions.Scorecard_SetCoreProperties]: {
    readonly modelIndex: number;
    readonly modelName: string;
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
      case Actions.AddMiningSchemaFields:
        historyService.batch(state, `models[${action.payload.modelIndex}].Characteristics`, draft => {
          validationRegistry.clear(
            Builder()
              .forModel(action.payload.modelIndex)
              .forCharacteristics()
              .build()
          );
          validateCharacteristics(
            action.payload.modelIndex,
            state.Characteristics.Characteristic,
            state.MiningSchema.MiningField.concat(action.payload.names.map(n => new MiningField({ name: n }))),
            validationRegistry
          );
        });
        break;

      case Actions.Scorecard_UpdateAttribute:
        historyService.batch(state, `models[${action.payload.modelIndex}].Characteristics`, draft => {
          validationRegistry.clear(
            Builder()
              .forModel(action.payload.modelIndex)
              .forCharacteristics()
              .build()
          );
          validateCharacteristics(
            action.payload.modelIndex,
            state.Characteristics.Characteristic.map((characteristic, characteristicIndex) => {
              if (characteristicIndex === action.payload.characteristicIndex) {
                const attributes = characteristic.Attribute.map((attribute, attributeIndex) => {
                  if (attributeIndex === action.payload.attributeIndex) {
                    return {
                      ...attribute,
                      predicate: action.payload.predicate
                    };
                  }
                  return attribute;
                });
                return {
                  ...characteristic,
                  Attribute: attributes
                };
              }
              return characteristic;
            }),
            state.MiningSchema.MiningField,
            validationRegistry
          );
        });
        break;

      case Actions.UpdateDataDictionaryField:
        if (action.payload.modelIndex !== undefined) {
          const modelIndex = action.payload.modelIndex;
          state.Characteristics.Characteristic.forEach((characteristic, characteristicIndex) => {
            characteristic.Attribute.forEach((attribute, attributeIndex) => {
              updatePredicateFieldName(
                modelIndex,
                characteristicIndex,
                attributeIndex,
                attribute.predicate,
                action.payload.dataField.name,
                action.payload.originalName,
                historyService
              );
            });
          });
        }

        break;

      case Actions.Scorecard_SetModelName:
        historyService.batch(state, `models[${action.payload.modelIndex}]`, draft => {
          draft.modelName = action.payload.modelName;
        });
        break;

      case Actions.Scorecard_SetCoreProperties:
        historyService.batch(state, `models[${action.payload.modelIndex}]`, draft => {
          draft.isScorable = action.payload.isScorable;
          draft.functionName = action.payload.functionName;
          draft.algorithmName = action.payload.algorithmName;
          draft.baselineScore = action.payload.baselineScore;
          draft.baselineMethod = action.payload.baselineMethod;
          draft.initialScore = action.payload.initialScore;
          draft.useReasonCodes = action.payload.useReasonCodes;
          draft.reasonCodeAlgorithm = action.payload.reasonCodeAlgorithm;
        });
        break;

      case Actions.DeleteMiningSchemaField:
        validationRegistry.clear(
          Builder()
            .forModel(action.payload.modelIndex)
            .forCharacteristics()
            .build()
        );
        validateCharacteristics(
          action.payload.modelIndex,
          state.Characteristics.Characteristic,
          state.MiningSchema.MiningField.filter(mf => mf.name !== action.payload.name),
          validationRegistry
        );

        if (state.MiningSchema.MiningField.length && state.Output?.OutputField.length) {
          validationRegistry.clear(`models[${action.payload.modelIndex}].Output`);
          state.Output.OutputField.forEach((outputField, outputFieldIndex) => {
            if (outputField.targetField === action.payload.name) {
              historyService.batch(state, `models[${action.payload.modelIndex}]`, draft => {
                draft!.Output!.OutputField[outputFieldIndex] = {
                  ...draft!.Output!.OutputField[outputFieldIndex],
                  targetField: undefined
                };
                validateOutput(
                  action.payload.modelIndex,
                  { ...state.Output!.OutputField[outputFieldIndex], targetField: undefined },
                  outputFieldIndex,
                  state.MiningSchema.MiningField.filter(
                    (miningField, miningFieldIndex) => miningFieldIndex !== action.payload.miningSchemaIndex
                  ),
                  validationRegistry
                );
              });
            }
          });
          validateOutputs(
            action.payload.modelIndex,
            state.Output?.OutputField,
            state.MiningSchema.MiningField.filter((field, index) => index !== action.payload.miningSchemaIndex),
            validationRegistry
          );
        }
        break;

      case Actions.UpdateMiningSchemaField:
        if (state.MiningSchema.MiningField.length && state.Output?.OutputField.length) {
          validationRegistry.clear(`models[${action.payload.modelIndex}].Output`);
          if (action.payload.usageType !== "target") {
            state.Output.OutputField.forEach((outputField, outputFieldIndex) => {
              if (outputField.targetField === action.payload.name) {
                historyService.batch(state, `models[${action.payload.modelIndex}]`, draft => {
                  draft!.Output!.OutputField[outputFieldIndex] = {
                    ...draft!.Output!.OutputField[outputFieldIndex],
                    targetField: undefined
                  };
                  validateOutput(
                    action.payload.modelIndex,
                    { ...state.Output!.OutputField[outputFieldIndex], targetField: undefined },
                    outputFieldIndex,
                    state.MiningSchema.MiningField.map((miningField, miningFieldIndex) => {
                      if (miningFieldIndex === action.payload.miningSchemaIndex) {
                        miningField = { ...miningField, usageType: action.payload.usageType };
                      }
                      return miningField;
                    }),
                    validationRegistry
                  );
                });
              }
            });
          }
          validateOutputs(
            action.payload.modelIndex,
            state.Output?.OutputField,
            state.MiningSchema.MiningField.map((item, index) => {
              if (index === action.payload.miningSchemaIndex) {
                item = { ...item, usageType: action.payload.usageType };
              }
              return item;
            }),
            validationRegistry
          );
        }
        break;

      case Actions.UpdateOutput:
        validationRegistry.clear(
          `models[${action.payload.modelIndex}].Output.OutputField[${action.payload.outputIndex}].targetField`
        );
        validateOutput(
          action.payload.modelIndex,
          action.payload.outputField,
          action.payload.outputIndex,
          state.MiningSchema.MiningField,
          validationRegistry
        );
        break;

      case Actions.AddBatchOutputs:
        validationRegistry.clear(`models[${action.payload.modelIndex}].Output`);
        if (state.Output && state.Output?.OutputField.length > 0) {
          validateOutputs(
            action.payload.modelIndex,
            state.Output?.OutputField,
            state.MiningSchema.MiningField,
            validationRegistry
          );
        }
        if (isOutputsTargetFieldRequired(state.MiningSchema.MiningField)) {
          const startIndex = state.Output?.OutputField.length ?? 0;
          action.payload.outputFields.forEach((name, index) => {
            validationRegistry.set(
              `models[${action.payload.modelIndex}].Output.OutputField[${startIndex + index}].targetField`,
              new ValidationEntry(
                ValidationLevel.WARNING,
                `"${name}" output field, target field is required if Mining Schema has multiple target fields.`
              )
            );
          });
        }
        break;

      case Actions.AddOutput:
        validationRegistry.clear(`models[${action.payload.modelIndex}].Output`);
        if (state.Output && state.Output?.OutputField.length > 0) {
          validateOutputs(
            action.payload.modelIndex,
            state.Output?.OutputField,
            state.MiningSchema.MiningField,
            validationRegistry
          );
        }
        if (isOutputsTargetFieldRequired(state.MiningSchema.MiningField)) {
          const outputFieldIndex = state.Output?.OutputField.length ?? 0;
          validationRegistry.set(
            `models[${action.payload.modelIndex}].Output.OutputField[${outputFieldIndex}].targetField`,
            new ValidationEntry(
              ValidationLevel.WARNING,
              `"${action.payload.outputField.name}" output field, target field is required if Mining Schema has multiple target fields.`
            )
          );
        }
        break;

      case Actions.DeleteOutput:
        validationRegistry.clear(`models[${action.payload.modelIndex}].Output`);
        validateOutputs(
          action.payload.modelIndex,
          state.Output?.OutputField.filter((field, index) => index !== action.payload.outputIndex) ?? [],
          state.MiningSchema.MiningField,
          validationRegistry
        );
        break;

      case Actions.Validate:
        if (action.payload.modelIndex !== undefined) {
          const modelIndex = action.payload.modelIndex;
          validationRegistry.clear(`models[${modelIndex}].Output`);
          validateOutputs(
            action.payload.modelIndex,
            state.Output?.OutputField ?? [],
            state.MiningSchema.MiningField,
            validationRegistry
          );

          validationRegistry.clear(
            Builder()
              .forModel(modelIndex)
              .forCharacteristics()
              .build()
          );
          validateCharacteristics(
            modelIndex,
            state.Characteristics.Characteristic,
            state.MiningSchema.MiningField,
            validationRegistry
          );
        }
        break;
    }

    return state;
  };
};

const updatePredicateFieldName = (
  modelIndex: number,
  characteristicIndex: number,
  attributeIndex: number,
  predicate: Predicate | undefined,
  name: FieldName,
  originalName: FieldName | undefined,
  service: HistoryService
) => {
  if (predicate === undefined) {
    return;
  } else if (predicate instanceof True) {
    return;
  } else if (predicate instanceof False) {
    return;
  } else if (predicate instanceof SimpleSetPredicate) {
    if (originalName === predicate.field) {
      service.batch(
        predicate,
        `models[${modelIndex}].Characteristics.Characteristic[${characteristicIndex}].Attribute[${attributeIndex}].predicate`,
        draft => {
          draft.field = name;
        }
      );
    }
  } else if (predicate instanceof SimplePredicate) {
    if (originalName === predicate.field) {
      service.batch(
        predicate,
        `models[${modelIndex}].Characteristics.Characteristic[${characteristicIndex}].Attribute[${attributeIndex}].predicate`,
        draft => {
          draft.field = name;
        }
      );
    }
  } else if (predicate instanceof CompoundPredicate) {
    const cp: CompoundPredicate = predicate as CompoundPredicate;
    cp.predicates?.forEach(p =>
      updatePredicateFieldName(modelIndex, characteristicIndex, attributeIndex, p, name, originalName, service)
    );
  }
};

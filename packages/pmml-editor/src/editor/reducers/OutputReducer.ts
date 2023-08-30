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
import { Output, OutputField } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { Builder } from "../paths";
import { getCharacteristics, getMiningSchema, getOutputs } from "../PMMLModelHelper";
import { isOutputsTargetFieldRequired, validateOutputs } from "../validation/Outputs";
import { ValidationEntry, ValidationLevel, ValidationRegistry } from "../validation";

interface OutputPayload {
  [Actions.AddOutput]: {
    readonly modelIndex: number;
    readonly outputField: OutputField;
  };
  [Actions.DeleteOutput]: {
    readonly modelIndex: number;
    readonly outputIndex: number;
  };
  [Actions.AddBatchOutputs]: {
    readonly modelIndex: number;
    readonly outputFields: string[];
  };
}

export type OutputActions = ActionMap<OutputPayload>[keyof ActionMap<OutputPayload>];

export const OutputReducer: HistoryAwareValidatingReducer<Output, AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<Output, AllActions> => {
  return (state: Output, action: AllActions) => {
    switch (action.type) {
      case Actions.AddOutput:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forOutput().build(),
          (draft) => {
            draft.OutputField.push({
              name: action.payload.outputField.name,
              dataType: action.payload.outputField.dataType,
              optype: action.payload.outputField.optype,
              targetField: action.payload.outputField.targetField,
              feature: action.payload.outputField.feature,
              value: action.payload.outputField.value,
              rank: action.payload.outputField.rank,
              rankOrder: action.payload.outputField.rankOrder,
              segmentId: action.payload.outputField.segmentId,
              isFinalResult: action.payload.outputField.isFinalResult,
            });
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const outputField = action.payload.outputField;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const outputs = getOutputs(pmml, modelIndex);
            if (outputs !== undefined && miningSchema !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forOutput().build());
              validateOutputs(
                action.payload.modelIndex,
                outputs.OutputField,
                miningSchema.MiningField,
                validationRegistry
              );
              if (isOutputsTargetFieldRequired(miningSchema.MiningField)) {
                const outputFieldIndex = outputs.OutputField.length;
                validationRegistry.set(
                  Builder().forModel(modelIndex).forOutput().forOutputField(outputFieldIndex).forTargetField().build(),
                  new ValidationEntry(
                    ValidationLevel.WARNING,
                    `"${outputField.name}" output field, target field is required if Mining Schema has multiple target fields.`
                  )
                );
              }
            }
          }
        );
        break;

      case Actions.DeleteOutput:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forOutput().build(),
          (draft) => {
            const outputIndex = action.payload.outputIndex;
            if (outputIndex >= 0 && outputIndex < draft.OutputField.length) {
              draft.OutputField.splice(outputIndex, 1);
            }
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const outputs = getOutputs(pmml, modelIndex);
            if (miningSchema !== undefined && outputs !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forOutput().build());
              validateOutputs(modelIndex, outputs.OutputField, miningSchema.MiningField, validationRegistry);
            }
          }
        );
        break;

      case Actions.AddBatchOutputs:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forOutput().build(),
          (draft) => {
            action.payload.outputFields.forEach((name) => {
              draft.OutputField.push({
                name: name,
                dataType: "string",
              });
            });
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            const outputs = getOutputs(pmml, modelIndex);
            if (outputs !== undefined && miningSchema !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forOutput().build());
              validateOutputs(
                action.payload.modelIndex,
                outputs.OutputField,
                miningSchema.MiningField,
                validationRegistry
              );
              if (isOutputsTargetFieldRequired(miningSchema.MiningField)) {
                outputs.OutputField.forEach((name, index) => {
                  validationRegistry.set(
                    Builder()
                      .forModel(action.payload.modelIndex)
                      .forOutput()
                      .forOutputField(index)
                      .forTargetField()
                      .build(),
                    new ValidationEntry(
                      ValidationLevel.WARNING,
                      `"${name}" output field, target field is required if Mining Schema has multiple target fields.`
                    )
                  );
                });
              }
            }
          }
        );
        break;

      case Actions.DeleteMiningSchemaField:
        if (state.OutputField.length > 0) {
          const modelIndex = action.payload.modelIndex;
          historyService.batch(
            state,
            Builder().forModel(modelIndex).forOutput().build(),
            (draft) => {
              state.OutputField.forEach((outputField, outputFieldIndex) => {
                if (outputField.targetField === action.payload.name) {
                  draft!.OutputField[outputFieldIndex] = {
                    ...draft!.OutputField[outputFieldIndex],
                    targetField: undefined,
                  };
                }
              });
            },
            (pmml) => {
              const miningSchema = getMiningSchema(pmml, modelIndex);
              const outputs = getOutputs(pmml, modelIndex);
              if (miningSchema !== undefined && outputs !== undefined) {
                validationRegistry.clear(Builder().forModel(modelIndex).forOutput().build());
                validateOutputs(modelIndex, outputs.OutputField, miningSchema.MiningField, validationRegistry);
              }
            }
          );
        }
        break;

      case Actions.UpdateMiningSchemaField:
        if (state.OutputField.length > 0) {
          const modelIndex = action.payload.modelIndex;
          historyService.batch(
            state,
            Builder().forModel(modelIndex).forOutput().build(),
            (draft) => {
              if (action.payload.usageType !== "target") {
                state.OutputField.forEach((outputField, outputFieldIndex) => {
                  if (outputField.targetField === action.payload.name) {
                    draft!.OutputField[outputFieldIndex] = {
                      ...draft!.OutputField[outputFieldIndex],
                      targetField: undefined,
                    };
                  }
                });
              }
            },
            (pmml) => {
              const miningSchema = getMiningSchema(pmml, modelIndex);
              const outputs = getOutputs(pmml, modelIndex);
              const characteristics = getCharacteristics(pmml, modelIndex);
              if (miningSchema !== undefined && outputs !== undefined && characteristics !== undefined) {
                validationRegistry.clear(Builder().forModel(modelIndex).forOutput().build());
                validateOutputs(modelIndex, outputs.OutputField, miningSchema.MiningField, validationRegistry);
              }
            }
          );
        }
    }

    return state;
  };
};

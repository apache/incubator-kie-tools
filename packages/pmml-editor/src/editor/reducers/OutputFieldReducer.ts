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
import { OutputField } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { Builder } from "../paths";
import { getMiningSchema } from "../PMMLModelHelper";
import { validateOutput } from "../validation/Outputs";
import { ValidationRegistry } from "../validation";

interface OutputFieldPayload {
  [Actions.UpdateOutput]: {
    readonly modelIndex: number;
    readonly outputIndex: number;
    readonly outputField: OutputField;
  };
}

export type OutputFieldActions = ActionMap<OutputFieldPayload>[keyof ActionMap<OutputFieldPayload>];

export const OutputFieldReducer: HistoryAwareValidatingReducer<OutputField[], AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<OutputField[], AllActions> => {
  return (state: OutputField[], action: AllActions) => {
    switch (action.type) {
      case Actions.UpdateOutput:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forOutput().forOutputField().build(),
          (draft) => {
            const outputIndex = action.payload.outputIndex;
            if (outputIndex >= 0 && outputIndex < draft.length) {
              draft[outputIndex] = {
                ...draft[outputIndex],
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
              };
            }
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const outputField = action.payload.outputField;
            const outputFieldIndex = action.payload.outputIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            if (miningSchema !== undefined) {
              validationRegistry.clear(
                Builder().forModel(modelIndex).forOutput().forOutputField(outputFieldIndex).forTargetField().build()
              );
              validateOutput(modelIndex, outputField, outputFieldIndex, miningSchema.MiningField, validationRegistry);
            }
          }
        );
        break;

      case Actions.UpdateDataDictionaryField:
        state.forEach((outputField, index) => {
          if (outputField.targetField === action.payload.originalName) {
            historyService.batch(
              state,
              Builder().forModel(action.payload.modelIndex).forOutput().forOutputField().build(),
              (draft) => {
                draft[index] = {
                  ...draft[index],
                  targetField: action.payload.dataField.name,
                };
              }
            );
          }
        });
        break;
    }

    return state;
  };
};

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
import { HistoryAwareReducer, HistoryService } from "../history";
import { FieldName, Output, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

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
    readonly outputFields: FieldName[];
  };
}

export type OutputActions = ActionMap<OutputPayload>[keyof ActionMap<OutputPayload>];

export const OutputReducer: HistoryAwareReducer<Output, AllActions> = (
  service: HistoryService
): Reducer<Output, AllActions> => {
  return (state: Output, action: AllActions) => {
    switch (action.type) {
      case Actions.AddOutput:
        service.batch(state, `models[${action.payload.modelIndex}].Output`, draft => {
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
            isFinalResult: action.payload.outputField.isFinalResult
          });
        });
        break;

      case Actions.DeleteOutput:
        service.batch(state, `models[${action.payload.modelIndex}].Output`, draft => {
          const outputIndex = action.payload.outputIndex;
          if (outputIndex >= 0 && outputIndex < draft.OutputField.length) {
            draft.OutputField.splice(outputIndex, 1);
          }
        });
        break;

      case Actions.AddBatchOutputs:
        service.batch(state, `models[${action.payload.modelIndex}].Output`, draft => {
          action.payload.outputFields.forEach(name => {
            draft.OutputField.push({
              name: name,
              dataType: "string"
            });
          });
        });
    }

    return state;
  };
};

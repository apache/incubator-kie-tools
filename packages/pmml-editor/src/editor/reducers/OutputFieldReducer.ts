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
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface OutputFieldPayload {
  [Actions.UpdateOutput]: {
    readonly modelIndex: number;
    readonly outputIndex: number;
    readonly outputField: OutputField;
  };
}

export type OutputFieldActions = ActionMap<OutputFieldPayload>[keyof ActionMap<OutputFieldPayload>];

export const OutputFieldReducer: HistoryAwareReducer<OutputField[], AllActions> = (
  service: HistoryService
): Reducer<OutputField[], AllActions> => {
  return (state: OutputField[], action: AllActions) => {
    switch (action.type) {
      case Actions.UpdateOutput:
        service.batch(state, `models[${action.payload.modelIndex}].Output.OutputField`, draft => {
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
              isFinalResult: action.payload.outputField.isFinalResult
            };
          }
        });
    }

    return state;
  };
};

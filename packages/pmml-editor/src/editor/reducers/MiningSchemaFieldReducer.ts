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
import { Reducer } from "react";
import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareReducer, HistoryService } from "../history";
import {
  FieldName,
  InvalidValueTreatmentMethod,
  MiningField,
  MissingValueTreatmentMethod,
  OpType,
  OutlierTreatmentMethod,
  UsageType
} from "@kogito-tooling/pmml-editor-marshaller";

interface MiningSchemaFieldPayload {
  [Actions.UpdateMiningSchemaField]: {
    readonly modelIndex: number;
    readonly miningSchemaIndex: number;
    readonly name: FieldName;
    readonly usageType: UsageType | undefined;
    readonly optype: OpType | undefined;
    readonly importance: number | undefined;
    readonly outliers: OutlierTreatmentMethod | undefined;
    readonly lowValue: number | undefined;
    readonly highValue: number | undefined;
    readonly missingValueReplacement: any | undefined;
    readonly missingValueTreatment: MissingValueTreatmentMethod | undefined;
    readonly invalidValueTreatment: InvalidValueTreatmentMethod | undefined;
    readonly invalidValueReplacement: any | undefined;
  };
}

export type MiningSchemaFieldActions = ActionMap<MiningSchemaFieldPayload>[keyof ActionMap<MiningSchemaFieldPayload>];

export const MiningSchemaFieldReducer: HistoryAwareReducer<MiningField[], AllActions> = (
  service: HistoryService
): Reducer<MiningField[], AllActions> => {
  return (state: MiningField[], action: AllActions) => {
    switch (action.type) {
      case Actions.UpdateDataDictionaryField:
        state.forEach((mf, index) => {
          if (mf.name === action.payload.originalName) {
            service.batch(state, `models[${action.payload.modelIndex}].MiningSchema.MiningField`, draft => {
              draft[index] = {
                ...draft[index],
                name: action.payload.dataField.name
              };
            });
          }
        });
        break;

      case Actions.UpdateMiningSchemaField:
        service.batch(state, `models[${action.payload.modelIndex}].MiningSchema.MiningField`, draft => {
          const miningSchemaIndex = action.payload.miningSchemaIndex;
          if (miningSchemaIndex >= 0 && miningSchemaIndex < draft.length) {
            draft[miningSchemaIndex] = {
              ...draft[miningSchemaIndex],
              name: action.payload.name,
              usageType: action.payload.usageType,
              optype: action.payload.optype,
              importance: action.payload.importance,
              outliers: action.payload.outliers,
              lowValue: action.payload.lowValue,
              highValue: action.payload.highValue,
              missingValueReplacement: action.payload.missingValueReplacement,
              missingValueTreatment: action.payload.missingValueTreatment,
              invalidValueTreatment: action.payload.invalidValueTreatment,
              invalidValueReplacement: action.payload.invalidValueReplacement
            };
          }
        });
    }

    return state;
  };
};

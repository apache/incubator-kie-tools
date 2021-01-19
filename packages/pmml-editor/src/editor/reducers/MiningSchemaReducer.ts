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
import { FieldName, MiningSchema } from "@kogito-tooling/pmml-editor-marshaller";

interface MiningSchemaPayload {
  [Actions.AddMiningSchemaFields]: {
    readonly modelIndex: number;
    readonly names: FieldName[];
  };
  [Actions.DeleteMiningSchemaField]: {
    readonly modelIndex: number;
    readonly miningSchemaIndex: number;
  };
}

export type MiningSchemaActions = ActionMap<MiningSchemaPayload>[keyof ActionMap<MiningSchemaPayload>];

export const MiningSchemaReducer: HistoryAwareReducer<MiningSchema, AllActions> = (
  service: HistoryService
): Reducer<MiningSchema, AllActions> => {
  return (state: MiningSchema, action: AllActions) => {
    switch (action.type) {
      case Actions.AddMiningSchemaFields:
        service.batch(state, `models[${action.payload.modelIndex}].MiningSchema`, draft => {
          action.payload.names.forEach(name => {
            draft.MiningField.push({
              name: name
            });
          });
        });
        break;

      case Actions.DeleteMiningSchemaField:
        service.batch(state, `models[${action.payload.modelIndex}].MiningSchema`, draft => {
          const miningSchemaIndex = action.payload.miningSchemaIndex;
          if (miningSchemaIndex >= 0 && miningSchemaIndex < draft.MiningField.length) {
            draft.MiningField.splice(miningSchemaIndex, 1);
          }
        });
    }

    return state;
  };
};

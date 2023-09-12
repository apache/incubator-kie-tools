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

import { Reducer } from "react";
import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import { MiningSchema } from "@kie-tools/pmml-editor-marshaller";
import { ValidationRegistry } from "../validation";
import { Builder } from "../paths";
import { validateMiningFields } from "../validation/MiningSchema";
import { getMiningSchema } from "../PMMLModelHelper";

interface MiningSchemaPayload {
  [Actions.AddMiningSchemaFields]: {
    readonly modelIndex: number;
    readonly names: string[];
  };
  [Actions.DeleteMiningSchemaField]: {
    readonly modelIndex: number;
    readonly miningSchemaIndex: number;
    readonly name?: string;
  };
}

export type MiningSchemaActions = ActionMap<MiningSchemaPayload>[keyof ActionMap<MiningSchemaPayload>];

export const MiningSchemaReducer: HistoryAwareValidatingReducer<MiningSchema, AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<MiningSchema, AllActions> => {
  return (state: MiningSchema, action: AllActions) => {
    switch (action.type) {
      case Actions.AddMiningSchemaFields:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forMiningSchema().build(),
          (draft) => {
            action.payload.names.forEach((name) => {
              draft.MiningField.push({
                name: name,
              });
            });
          }
        );
        break;

      case Actions.DeleteMiningSchemaField:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forMiningSchema().build(),
          (draft) => {
            const miningSchemaIndex = action.payload.miningSchemaIndex;
            if (miningSchemaIndex >= 0 && miningSchemaIndex < draft.MiningField.length) {
              draft.MiningField.splice(miningSchemaIndex, 1);
            }
          },
          (pmml) => {
            const modelIndex = action.payload.modelIndex;
            const miningSchema = getMiningSchema(pmml, modelIndex);
            if (miningSchema !== undefined) {
              validationRegistry.clear(Builder().forModel(modelIndex).forMiningSchema().build());
              validateMiningFields(modelIndex, miningSchema.MiningField, validationRegistry);
            }
          }
        );
    }

    return state;
  };
};

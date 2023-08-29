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
import { HistoryAwareReducer, HistoryService } from "../history";
import { Attribute, CompoundPredicate, Predicate, SimplePredicate } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { immerable } from "immer";
import { Builder } from "../paths";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
Attribute[immerable] = true;
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
Predicate[immerable] = true;
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
SimplePredicate[immerable] = true;
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
CompoundPredicate[immerable] = true;

interface AttributesPayload {
  [Actions.Scorecard_AddAttribute]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
    readonly predicate: Predicate;
    readonly partialScore: number;
    readonly reasonCode: string;
  };
  [Actions.Scorecard_DeleteAttribute]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
    readonly attributeIndex: number;
  };
  [Actions.Scorecard_UpdateAttribute]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
    readonly attributeIndex: number;
    readonly predicate: Predicate;
    readonly partialScore: number;
    readonly reasonCode: string;
  };
}

export type AttributesActions = ActionMap<AttributesPayload>[keyof ActionMap<AttributesPayload>];

export const AttributesReducer: HistoryAwareReducer<Attribute[], AllActions> = (
  historyService: HistoryService
): Reducer<Attribute[], AllActions> => {
  return (state: Attribute[], action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddAttribute:
        historyService.batch(
          state,
          Builder()
            .forModel(action.payload.modelIndex)
            .forCharacteristics()
            .forCharacteristic(action.payload.characteristicIndex)
            .forAttribute()
            .build(),
          (draft) => {
            draft.push({
              predicate: action.payload.predicate,
              partialScore: action.payload.partialScore,
              reasonCode: action.payload.reasonCode,
            });
          }
        );
        break;

      case Actions.Scorecard_DeleteAttribute:
        historyService.batch(
          state,
          Builder()
            .forModel(action.payload.modelIndex)
            .forCharacteristics()
            .forCharacteristic(action.payload.characteristicIndex)
            .forAttribute()
            .build(),
          (draft) => {
            const attributeIndex = action.payload.attributeIndex;
            if (attributeIndex >= 0 && attributeIndex < draft.length) {
              draft.splice(attributeIndex, 1);
            }
          }
        );
        break;

      case Actions.Scorecard_UpdateAttribute:
        historyService.batch(
          state,
          Builder()
            .forModel(action.payload.modelIndex)
            .forCharacteristics()
            .forCharacteristic(action.payload.characteristicIndex)
            .forAttribute()
            .build(),
          (draft) => {
            const attributeIndex: number = action.payload.attributeIndex;
            if (attributeIndex >= 0 && attributeIndex < draft.length) {
              draft[attributeIndex] = {
                ...draft[attributeIndex],
                predicate: action.payload.predicate,
                partialScore: action.payload.partialScore,
                reasonCode: action.payload.reasonCode,
              };
            }
          }
        );
    }

    return state;
  };
};

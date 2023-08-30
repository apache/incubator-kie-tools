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
import { Characteristics } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { immerable } from "immer";
import { Builder } from "../paths";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
Characteristics[immerable] = true;

interface CharacteristicsPayload {
  [Actions.Scorecard_AddCharacteristic]: {
    readonly modelIndex: number;
    readonly name: string;
    readonly reasonCode: string;
    readonly baselineScore: number;
  };
  [Actions.Scorecard_DeleteCharacteristic]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
  };
}

export type CharacteristicsActions = ActionMap<CharacteristicsPayload>[keyof ActionMap<CharacteristicsPayload>];

export const CharacteristicsReducer: HistoryAwareReducer<Characteristics, AllActions> = (
  historyService: HistoryService
): Reducer<Characteristics, AllActions> => {
  return (state: Characteristics, action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddCharacteristic:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forCharacteristics().build(),
          (draft) => {
            draft.Characteristic.push({
              name: action.payload.name,
              reasonCode: action.payload.reasonCode,
              baselineScore: action.payload.baselineScore,
              Attribute: [],
            });
          }
        );
        break;

      case Actions.Scorecard_DeleteCharacteristic:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forCharacteristics().build(),
          (draft) => {
            const characteristicIndex = action.payload.characteristicIndex;
            if (characteristicIndex >= 0 && characteristicIndex < draft.Characteristic.length) {
              draft.Characteristic.splice(characteristicIndex, 1);
            }
          }
        );
    }

    return state;
  };
};

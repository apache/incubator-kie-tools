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
import { Characteristics } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { immerable } from "immer";

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
  service: HistoryService
): Reducer<Characteristics, AllActions> => {
  return (state: Characteristics, action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddCharacteristic:
        service.batch(state, `models[${action.payload.modelIndex}].Characteristics`, draft => {
          draft.Characteristic.push({
            name: action.payload.name,
            reasonCode: action.payload.reasonCode,
            baselineScore: action.payload.baselineScore,
            Attribute: []
          });
        });
        break;

      case Actions.Scorecard_DeleteCharacteristic:
        service.batch(state, `models[${action.payload.modelIndex}].Characteristics`, draft => {
          const characteristicIndex = action.payload.characteristicIndex;
          if (characteristicIndex >= 0 && characteristicIndex < draft.Characteristic.length) {
            draft.Characteristic.splice(characteristicIndex, 1);
          }
        });
    }

    return state;
  };
};

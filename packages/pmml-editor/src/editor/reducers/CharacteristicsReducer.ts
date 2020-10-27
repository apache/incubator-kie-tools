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
import { ActionMap, Actions } from "./Actions";
import { HistoryAwareReducer, HistoryService } from "../history";
import { Characteristics } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface CharacteristicsPayload {
  [Actions.Scorecard_AddCharacteristic]: {
    readonly modelIndex: number;
  };
  [Actions.Scorecard_DeleteCharacteristic]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
  };
}

export type CharacteristicsActions = ActionMap<CharacteristicsPayload>[keyof ActionMap<CharacteristicsPayload>];

export const CharacteristicsReducer: HistoryAwareReducer<Characteristics, CharacteristicsActions> = (
  service: HistoryService
): Reducer<Characteristics, CharacteristicsActions> => {
  return (state: Characteristics, action: CharacteristicsActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddCharacteristic:
        return service.mutate(state, `models[${action.payload.modelIndex}].Characteristics`, draft => {
          draft.Characteristic.push({
            name: undefined,
            reasonCode: undefined,
            baselineScore: undefined,
            Attribute: []
          });
        });

      case Actions.Scorecard_DeleteCharacteristic:
        return service.mutate(state, `models[${action.payload.modelIndex}].Characteristics`, draft => {
          const characteristicIndex = action.payload.characteristicIndex;
          if (characteristicIndex >= 0 && characteristicIndex < draft.Characteristic.length) {
            draft.Characteristic.splice(characteristicIndex, 1);
          }
        });
    }

    return state;
  };
};

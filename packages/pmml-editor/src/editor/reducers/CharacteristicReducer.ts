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
import { Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface CharacteristicPayload {
  [Actions.Scorecard_UpdateCharacteristic]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
    readonly name: string;
    readonly reasonCode: string;
    readonly baselineScore: number;
  };
}

export type CharacteristicActions = ActionMap<CharacteristicPayload>[keyof ActionMap<CharacteristicPayload>];

export const CharacteristicReducer: HistoryAwareReducer<Characteristic[], CharacteristicActions> = (
  service: HistoryService
): Reducer<Characteristic[], CharacteristicActions> => {
  return (state: Characteristic[], action: CharacteristicActions) => {
    switch (action.type) {
      case Actions.Scorecard_UpdateCharacteristic:
        return service.mutate(state, `models[${action.payload.modelIndex}].Characteristics.Characteristic`, draft => {
          const characteristicIndex: number = action.payload.characteristicIndex;
          if (characteristicIndex >= 0 && characteristicIndex < draft.length) {
            draft[characteristicIndex] = {
              ...draft[characteristicIndex],
              name: action.payload.name,
              reasonCode: action.payload.reasonCode,
              baselineScore: action.payload.baselineScore
            };
          }
        });
    }

    return state;
  };
};

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
import { Attribute, Characteristic } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { AttributesReducer } from "./AttributesReducer";
import { immerable } from "immer";

// @ts-ignore
Characteristic[immerable] = true;

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

export const CharacteristicReducer: HistoryAwareReducer<Characteristic[], AllActions> = (
  service: HistoryService
): Reducer<Characteristic[], AllActions> => {
  const attributesReducer = AttributesReducer(service);

  const delegateToAttributes = (state: Characteristic[], action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddAttribute:
      case Actions.Scorecard_UpdateAttribute:
      case Actions.Scorecard_DeleteAttribute:
        const characteristicIndex = action.payload.characteristicIndex;
        const attributes: Attribute[] = state[characteristicIndex].Attribute;
        const newAttributes = attributesReducer(attributes, action);
        if (newAttributes !== attributes) {
          const newCharacteristics: Characteristic[] = [];
          state.forEach(c => newCharacteristics.push(c));
          newCharacteristics[characteristicIndex] = {
            ...state[characteristicIndex],
            Attribute: newAttributes
          };
          return newCharacteristics;
        }
    }

    return state;
  };

  return (state: Characteristic[], action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_UpdateCharacteristic:
        service.batch(state, `models[${action.payload.modelIndex}].Characteristics.Characteristic`, draft => {
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
        return state;
    }

    return delegateToAttributes(state, action);
  };
};

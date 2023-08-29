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
import { Attribute, Characteristic } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { AttributesReducer } from "./AttributesReducer";
import { immerable } from "immer";
import { Builder } from "../paths";

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
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
  historyService: HistoryService
): Reducer<Characteristic[], AllActions> => {
  const attributesReducer = AttributesReducer(historyService);

  const delegateToAttributes = (state: Characteristic[], action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddAttribute:
      case Actions.Scorecard_UpdateAttribute:
      case Actions.Scorecard_DeleteAttribute:
        const characteristicIndex = action.payload.characteristicIndex;
        const attributes: Attribute[] = state[characteristicIndex].Attribute;
        attributesReducer(attributes, action);
    }

    return state;
  };

  return (state: Characteristic[], action: AllActions) => {
    switch (action.type) {
      case Actions.Scorecard_UpdateCharacteristic:
        historyService.batch(
          state,
          Builder().forModel(action.payload.modelIndex).forCharacteristics().forCharacteristic().build(),
          (draft) => {
            const characteristicIndex: number = action.payload.characteristicIndex;
            if (characteristicIndex >= 0 && characteristicIndex < draft.length) {
              draft[characteristicIndex] = {
                ...draft[characteristicIndex],
                name: action.payload.name,
                reasonCode: action.payload.reasonCode,
                baselineScore: action.payload.baselineScore,
              };
            }
            if (action.payload.reasonCode !== undefined) {
              draft[characteristicIndex].Attribute.forEach((attribute) => (attribute.reasonCode = undefined));
            }
          }
        );
        return state;
    }

    return delegateToAttributes(state, action);
  };
};

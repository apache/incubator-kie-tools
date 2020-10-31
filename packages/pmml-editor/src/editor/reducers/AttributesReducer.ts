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
import { Attribute, CompoundPredicate, Predicate, SimplePredicate } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { fromText } from "./AttributePredicateConverter";
import { immerable } from "immer";

// @ts-ignore
Attribute[immerable] = true;
// @ts-ignore
Predicate[immerable] = true;
// @ts-ignore
SimplePredicate[immerable] = true;
// @ts-ignore
CompoundPredicate[immerable] = true;

interface AttributesPayload {
  [Actions.Scorecard_AddAttribute]: {
    readonly modelIndex: number;
    readonly characteristicIndex: number;
    readonly text: string;
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
    readonly text: string;
    readonly partialScore: number;
    readonly reasonCode: string;
  };
}

export type AttributesActions = ActionMap<AttributesPayload>[keyof ActionMap<AttributesPayload>];

export const AttributesReducer: HistoryAwareReducer<Attribute[], AttributesActions> = (
  service: HistoryService
): Reducer<Attribute[], AttributesActions> => {
  return (state: Attribute[], action: AttributesActions) => {
    switch (action.type) {
      case Actions.Scorecard_AddAttribute:
        return service.mutate(
          state,
          `models[${action.payload.modelIndex}].Characteristics.Characteristic[${action.payload.characteristicIndex}].Attribute`,
          draft => {
            draft.push({
              predicate: fromText(action.payload.text),
              partialScore: action.payload.partialScore,
              reasonCode: action.payload.reasonCode
            });
          }
        );

      case Actions.Scorecard_DeleteAttribute:
        return service.mutate(
          state,
          `models[${action.payload.modelIndex}].Characteristics.Characteristic[${action.payload.characteristicIndex}].Attribute`,
          draft => {
            const attributeIndex = action.payload.attributeIndex;
            if (attributeIndex >= 0 && attributeIndex < draft.length) {
              draft.splice(attributeIndex, 1);
            }
          }
        );

      case Actions.Scorecard_UpdateAttribute:
        return service.mutate(
          state,
          `models[${action.payload.modelIndex}].Characteristics.Characteristic[${action.payload.characteristicIndex}].Attribute[${action.payload.attributeIndex}]`,
          draft => {
            const attributeIndex: number = action.payload.attributeIndex;
            if (attributeIndex >= 0 && attributeIndex < draft.length) {
              draft[attributeIndex] = {
                ...draft[attributeIndex],
                predicate: fromText(action.payload.text),
                partialScore: action.payload.partialScore,
                reasonCode: action.payload.reasonCode
              };
            }
          }
        );
    }

    return state;
  };
};

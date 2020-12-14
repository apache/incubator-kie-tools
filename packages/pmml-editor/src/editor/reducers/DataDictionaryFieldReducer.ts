/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import { ActionMap, Actions } from "./Actions";
import { HistoryAwareReducer, HistoryService } from "../history";
import { DataField, DataType, FieldName } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface DataDictionaryFieldPayload {
  [Actions.UpdateDataDictionaryField]: {
    readonly dataDictionaryIndex: number;
    readonly name: FieldName;
    readonly type: DataType;
  };
}

export type DataDictionaryFieldActions = ActionMap<DataDictionaryFieldPayload>[keyof ActionMap<
  DataDictionaryFieldPayload
>];

export const DataDictionaryFieldReducer: HistoryAwareReducer<DataField[], DataDictionaryFieldActions> = (
  service: HistoryService
): Reducer<DataField[], DataDictionaryFieldActions> => {
  return (state: DataField[], action: DataDictionaryFieldActions) => {
    switch (action.type) {
      case Actions.UpdateDataDictionaryField:
        return service.mutate(state, "DataDictionary.DataField", draft => {
          const dataDictionaryIndex = action.payload.dataDictionaryIndex;
          if (dataDictionaryIndex >= 0 && dataDictionaryIndex < draft.length) {
            draft[dataDictionaryIndex] = {
              ...draft[dataDictionaryIndex],
              name: action.payload.name,
              dataType: action.payload.type
            };
          }
        });
    }

    return state;
  };
};

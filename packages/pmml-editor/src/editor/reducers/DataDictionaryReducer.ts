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
import { HistoryService } from "../history/HistoryProvider";
import { DataDictionary, FieldName } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { HistoryAwareReducer } from "../history/HistoryAwareReducer";

interface DataDictionaryPayload {
  [Actions.CreateDataField]: {
    readonly name: string;
  };
  [Actions.DeleteDataField]: {
    readonly index: number;
  };
}

export type DataDictionaryActions = ActionMap<DataDictionaryPayload>[keyof ActionMap<DataDictionaryPayload>];

export const DataDictionaryReducer: HistoryAwareReducer<DataDictionary, DataDictionaryActions> = (
  service: HistoryService
): Reducer<DataDictionary, DataDictionaryActions> => {
  return (state: DataDictionary, action: DataDictionaryActions) => {
    switch (action.type) {
      case Actions.CreateDataField:
        return service.mutate(state, "DataDictionary", draft => {
          draft.DataField.push({
            dataType: "string",
            name: action.payload.name as FieldName,
            displayName: action.payload.name,
            optype: "categorical"
          });
        });

      case Actions.DeleteDataField:
        return service.mutate(state, "DataDictionary", draft => {
          const index = action.payload.index;
          if (index >= 0 && index < draft.DataField.length) {
            draft.DataField.splice(index, 1);
          }
        });
    }

    return state;
  };
};

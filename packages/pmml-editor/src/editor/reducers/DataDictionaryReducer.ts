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
import { DataDictionary, DataType, FieldName, OpType } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface DataDictionaryPayload {
  [Actions.AddDataDictionaryField]: {
    readonly name?: string;
    readonly type: DataType;
    readonly optype: OpType;
  };
  [Actions.DeleteDataDictionaryField]: {
    readonly index: number;
  };
  [Actions.AddBatchDataDictionaryFields]: {
    readonly dataDictionaryFields: FieldName[];
  };
  [Actions.ReorderDataDictionaryFields]: {
    readonly oldIndex: number;
    readonly newIndex: number;
  };
}

export type DataDictionaryActions = ActionMap<DataDictionaryPayload>[keyof ActionMap<DataDictionaryPayload>];

export const DataDictionaryReducer: HistoryAwareReducer<DataDictionary, AllActions> = (
  service: HistoryService
): Reducer<DataDictionary, AllActions> => {
  return (state: DataDictionary, action: AllActions) => {
    switch (action.type) {
      case Actions.AddDataDictionaryField:
        service.batch(state, "DataDictionary", draft => {
          draft.DataField.push({
            name: action.payload.name as FieldName,
            dataType: action.payload.type,
            optype: action.payload.optype
          });
        });
        break;

      case Actions.DeleteDataDictionaryField:
        service.batch(state, "DataDictionary", draft => {
          const index = action.payload.index;
          if (index >= 0 && index < draft.DataField.length) {
            draft.DataField.splice(index, 1);
          }
        });
        break;

      case Actions.ReorderDataDictionaryFields:
        service.batch(state, "DataDictionary", draft => {
          const [removed] = draft.DataField.splice(action.payload.oldIndex, 1);
          draft.DataField.splice(action.payload.newIndex, 0, removed);
        });
        break;

      case Actions.AddBatchDataDictionaryFields:
        service.batch(state, "DataDictionary", draft => {
          action.payload.dataDictionaryFields.forEach(name => {
            draft.DataField.push({
              name,
              dataType: "string",
              optype: "categorical"
            });
          });
        });
    }

    return state;
  };
};

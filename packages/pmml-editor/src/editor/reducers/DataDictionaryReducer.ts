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
import { DataDictionary, DataField, DataType, FieldName, OpType } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";

interface DataDictionaryPayload {
  [Actions.AddDataDictionaryField]: {
    readonly name?: string;
    readonly type: DataType;
  };
  [Actions.DeleteDataDictionaryField]: {
    readonly index: number;
  };
  [Actions.AddBatchDataDictionaryFields]: {
    readonly dataDictionaryFields: FieldName[];
  };
  [Actions.SetDataFields]: {
    readonly dataFields: DataField[];
  };
}

export type DataDictionaryActions = ActionMap<DataDictionaryPayload>[keyof ActionMap<DataDictionaryPayload>];

export const DataDictionaryReducer: HistoryAwareReducer<DataDictionary, DataDictionaryActions> = (
  service: HistoryService
): Reducer<DataDictionary, DataDictionaryActions> => {
  return (state: DataDictionary, action: DataDictionaryActions) => {
    switch (action.type) {
      case Actions.AddDataDictionaryField:
        return service.mutate(state, "DataDictionary", draft => {
          let opType: OpType;
          switch (action.payload.type) {
            case "boolean":
            case "string":
              opType = "categorical";
              break;
            case "double":
            case "float":
            case "integer":
              opType = "continuous";
              break;
            default:
              opType = "continuous";
              break;
          }
          draft.DataField.push({
            name: action.payload.name as FieldName,
            dataType: action.payload.type,
            optype: opType
          });
        });

      case Actions.DeleteDataDictionaryField:
        return service.mutate(state, "DataDictionary", draft => {
          const index = action.payload.index;
          if (index >= 0 && index < draft.DataField.length) {
            draft.DataField.splice(index, 1);
          }
        });

      case Actions.SetDataFields:
        return service.mutate(state, "DataDictionary", draft => {
          draft.DataField = [...action.payload.dataFields];
          draft.numberOfFields = action.payload.dataFields.length;
        });

      case Actions.AddBatchDataDictionaryFields:
        return service.mutate(state, "DataDictionary", draft => {
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

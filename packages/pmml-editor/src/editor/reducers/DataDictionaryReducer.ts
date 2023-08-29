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
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import { DataDictionary, DataType, OpType } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import { validateDataFields, ValidationRegistry } from "../validation";
import { Builder } from "../paths";

interface DataDictionaryPayload {
  [Actions.AddDataDictionaryField]: {
    readonly modelIndex?: number;
    readonly name?: string;
    readonly type: DataType;
    readonly optype: OpType;
  };
  [Actions.DeleteDataDictionaryField]: {
    readonly modelIndex?: number;
    readonly index: number;
  };
  [Actions.AddBatchDataDictionaryFields]: {
    readonly modelIndex?: number;
    readonly dataDictionaryFields: string[];
  };
  [Actions.ReorderDataDictionaryFields]: {
    readonly oldIndex: number;
    readonly newIndex: number;
  };
}

export type DataDictionaryActions = ActionMap<DataDictionaryPayload>[keyof ActionMap<DataDictionaryPayload>];

export const DataDictionaryReducer: HistoryAwareValidatingReducer<DataDictionary, AllActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<DataDictionary, AllActions> => {
  return (state: DataDictionary, action: AllActions) => {
    switch (action.type) {
      case Actions.AddDataDictionaryField:
        historyService.batch(state, Builder().forDataDictionary().build(), (draft) => {
          draft.DataField.push({
            name: action.payload.name as string,
            dataType: action.payload.type,
            optype: action.payload.optype,
          });
        });
        break;

      case Actions.DeleteDataDictionaryField:
        historyService.batch(
          state,
          Builder().forDataDictionary().build(),
          (draft) => {
            const index = action.payload.index;
            if (index >= 0 && index < draft.DataField.length) {
              draft.DataField.splice(index, 1);
            }
          },
          (pmml) => {
            validationRegistry.clear(Builder().forDataDictionary().build());
            validateDataFields(pmml.DataDictionary.DataField, validationRegistry);
          }
        );
        break;

      case Actions.ReorderDataDictionaryFields:
        historyService.batch(
          state,
          Builder().forDataDictionary().build(),
          (draft) => {
            const [removed] = draft.DataField.splice(action.payload.oldIndex, 1);
            draft.DataField.splice(action.payload.newIndex, 0, removed);
          },
          (pmml) => {
            validationRegistry.clear(Builder().forDataDictionary().build());
            validateDataFields(pmml.DataDictionary.DataField, validationRegistry);
          }
        );
        break;

      case Actions.AddBatchDataDictionaryFields:
        historyService.batch(state, Builder().forDataDictionary().build(), (draft) => {
          action.payload.dataDictionaryFields.forEach((name) => {
            draft.DataField.push({
              name,
              dataType: "string",
              optype: "categorical",
            });
          });
        });
    }

    return state;
  };
};

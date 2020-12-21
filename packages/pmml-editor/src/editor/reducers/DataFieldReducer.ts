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
import { DataField, FieldName } from "@redhat/pmml-editor-marshaller";
import { Reducer } from "react";

interface DataFieldPayload {
  [Actions.SetDataFieldName]: {
    readonly index: number;
    readonly name: FieldName;
  };
}

export type DataFieldActions = ActionMap<DataFieldPayload>[keyof ActionMap<DataFieldPayload>];

export const DataFieldReducer: HistoryAwareReducer<DataField[], DataFieldActions> = (
  service: HistoryService
): Reducer<DataField[], DataFieldActions> => {
  return (state: DataField[], action: DataFieldActions) => {
    switch (action.type) {
      case Actions.SetDataFieldName:
        return service.mutate(state, "DataDictionary.DataField", draft => {
          const index = action.payload.index;
          if (index >= 0 && index < draft.length) {
            draft[index] = { ...draft[index], name: action.payload.name };
          }
        });
    }

    return state;
  };
};

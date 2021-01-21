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
import { ActionMap, Actions, AllActions } from "./Actions";
import { HistoryAwareValidatingReducer, HistoryService } from "../history";
import { DataField, FieldName } from "@kogito-tooling/pmml-editor-marshaller";
import { Reducer } from "react";
import { ValidationService, validateDataField, validateDataFields, shouldConstraintsBeCleared } from "../validation";

interface DataDictionaryFieldPayload {
  [Actions.UpdateDataDictionaryField]: {
    readonly modelIndex?: number;
    readonly dataDictionaryIndex: number;
    readonly dataField: DataField;
    readonly originalName: FieldName;
  };
}

export type DataDictionaryFieldActions = ActionMap<DataDictionaryFieldPayload>[keyof ActionMap<
  DataDictionaryFieldPayload
>];

export const DataDictionaryFieldReducer: HistoryAwareValidatingReducer<DataField[], DataDictionaryFieldActions> = (
  service: HistoryService,
  validation: ValidationService
): Reducer<DataField[], AllActions> => {
  return (state: DataField[], action: AllActions) => {
    switch (action.type) {
      case Actions.UpdateDataDictionaryField:
        const dataField = action.payload.dataField;
        const dataDictionaryIndex = action.payload.dataDictionaryIndex;

        service.batch(state, "DataDictionary.DataField", draft => {
          if (dataDictionaryIndex >= 0 && dataDictionaryIndex < draft.length) {
            if (
              shouldConstraintsBeCleared(
                dataField,
                draft[dataDictionaryIndex].isCyclic,
                draft[dataDictionaryIndex].dataType,
                draft[dataDictionaryIndex].optype
              )
            ) {
              // clearing constraints if they contain only empty values because constraints are no more required
              // for non cyclic data types or for types different from ordinal strings)
              delete dataField.Interval;
              dataField.Value = dataField.Value?.filter(
                value => value.property === "invalid" || value.property === "missing"
              );
            }
            draft[dataDictionaryIndex] = dataField;
          }
          validation.clear(`DataDictionary.DataField[${dataDictionaryIndex}]`);
          validateDataField(dataField, dataDictionaryIndex, validation);
        });
        break;

      case Actions.Validate:
        validation.clear("DataDictionary.DataField");
        validateDataFields(state, validation);
    }

    return state;
  };
};

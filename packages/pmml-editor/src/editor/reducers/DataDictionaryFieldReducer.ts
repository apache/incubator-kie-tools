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
import { DataField } from "@kie-tools/pmml-editor-marshaller";
import { Reducer } from "react";
import {
  hasIntervals,
  hasValidValues,
  shouldConstraintsBeCleared,
  validateDataField,
  validateDataFields,
  ValidationRegistry,
} from "../validation";
import { Builder } from "../paths";

interface DataDictionaryFieldPayload {
  [Actions.UpdateDataDictionaryField]: {
    readonly modelIndex?: number;
    readonly dataDictionaryIndex: number;
    readonly dataField: DataField;
    readonly originalName: string;
  };
}

export type DataDictionaryFieldActions =
  ActionMap<DataDictionaryFieldPayload>[keyof ActionMap<DataDictionaryFieldPayload>];

export const DataDictionaryFieldReducer: HistoryAwareValidatingReducer<DataField[], DataDictionaryFieldActions> = (
  historyService: HistoryService,
  validationRegistry: ValidationRegistry
): Reducer<DataField[], AllActions> => {
  return (state: DataField[], action: AllActions) => {
    switch (action.type) {
      case Actions.UpdateDataDictionaryField:
        const dataField = action.payload.dataField;
        const dataDictionaryIndex = action.payload.dataDictionaryIndex;

        historyService.batch(
          state,
          Builder().forDataDictionary().forDataField().build(),
          (draft) => {
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
                  (value) => value.property === "invalid" || value.property === "missing"
                );
              }

              if (dataField.isCyclic === "1" && dataField.optype === "ordinal" && hasIntervals(dataField)) {
                // ordinal cyclic fields cannot have interval constraints
                delete dataField.Interval;
              }
              if (dataField.optype === "categorical" && dataField.isCyclic !== undefined) {
                // categorical fields cannot be cyclic
                delete dataField.isCyclic;
              }
              if (
                ((dataField.isCyclic === "1" && dataField.optype === "ordinal") ||
                  (dataField.dataType === "string" && dataField.optype === "ordinal")) &&
                !hasValidValues(dataField)
              ) {
                // add automatically an empty value when value constraint is required because we want to save the user
                // from having to manually select the only supported constraint type in the UI ("Value")
                dataField.Value = (dataField.Value || []).concat({ value: "" });
              }

              draft[dataDictionaryIndex] = dataField;
            }
          },
          () => {
            validationRegistry.clear(Builder().forDataDictionary().forDataField(dataDictionaryIndex).build());
            validateDataField(dataField, dataDictionaryIndex, validationRegistry);
          }
        );
        break;

      case Actions.Validate:
        validationRegistry.clear(Builder().forDataDictionary().forDataField().build());
        validateDataFields(state, validationRegistry);
    }

    return state;
  };
};

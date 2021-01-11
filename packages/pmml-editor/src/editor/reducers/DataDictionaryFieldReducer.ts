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
import { ValidationEntry, ValidationLevel, ValidationService } from "../validation";

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
        service.batch(state, "DataDictionary.DataField", draft => {
          const dataDictionaryIndex = action.payload.dataDictionaryIndex;
          if (dataDictionaryIndex >= 0 && dataDictionaryIndex < draft.length) {
            draft[dataDictionaryIndex] = action.payload.dataField;
          }
        });
        validateDataFieldConstraintRanges(action.payload.dataField, action.payload.dataDictionaryIndex, validation);
        break;

      case Actions.Validate:
        validateDataFieldsConstraintRanges(state, validation);
    }

    return state;
  };
};

const validateDataFieldConstraintRanges = (
  dataField: DataField,
  dataDictionaryIndex: number,
  validation: ValidationService
): void => {
  dataField.Interval?.forEach((interval, index) => {
    validation.setValidation(
      `DataDictionary.DataField[${dataDictionaryIndex}].Interval[${index}]`,
      interval.leftMargin === undefined && interval.rightMargin === undefined
        ? new ValidationEntry(
            ValidationLevel.WARNING,
            `Data Field[${dataDictionaryIndex}], Interval[${index}] must have either the left or right margin set.`
          )
        : new ValidationEntry(ValidationLevel.VALID)
    );
  });
};

const validateDataFieldsConstraintRanges = (dataFields: DataField[], validation: ValidationService): void => {
  dataFields.forEach((dataField, dataDictionaryIndex) =>
    validateDataFieldConstraintRanges(dataField, dataDictionaryIndex, validation)
  );
};

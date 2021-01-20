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
import {
  validateDataFieldConstraintRanges,
  validateDataFieldsConstraintRanges,
  ValidationService,
  ValidationEntry,
  ValidationLevel
} from "../validation";

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
            draft[dataDictionaryIndex] = dataField;
          }
          validation.clear(`DataDictionary.DataField[${dataDictionaryIndex}]`);
          validateDataFieldConstraintRanges(dataField, dataDictionaryIndex, validation);
        });
        break;

      case Actions.Validate:
        validation.clear("DataDictionary.DataField");
        validateDataFieldsConstraintRanges(state, validation);
    }

    return state;
  };
};

const validateDataFields = (dataFields: DataField[], validation: ValidationService): void => {
  dataFields.forEach((dataField, dataDictionaryIndex) => validateDataField(dataField, dataDictionaryIndex, validation));
};

const validateDataField = (dataField: DataField, dataDictionaryIndex: number, validation: ValidationService): void => {
  // required interval margins
  dataField.Interval?.forEach((interval, index) => {
    if (interval.leftMargin === undefined && interval.rightMargin === undefined) {
      validation.set(
        `DataDictionary.DataField[${dataDictionaryIndex}].Interval[${index}]`,

          new ValidationEntry(
          ValidationLevel.WARNING,
          `"${dataField.name}" data type, Interval (${index + 1}) must have the start and/or end value set.`
          )

      );
    }

  });
  // empty values
  dataField.Value?.forEach((value, index) => {
    if (value.value === "") {
      validation.set(
        `DataDictionary.DataField[${dataDictionaryIndex}].Interval[${index}]`,
        new ValidationEntry(ValidationLevel.WARNING, `"${dataField.name}" data type, Empty value (${index + 1})`)
      );
    }
  });
  // ordinal strings constraints requirements
  if (dataField.dataType === "string" && dataField.optype === "ordinal") {
    if (!hasValidValues(dataField)) {
      validation.set(
        `DataDictionary.DataField[${dataDictionaryIndex}].Interval`,
        new ValidationEntry(
          ValidationLevel.WARNING,
          `"${dataField.name}" data type, Values list required for ordinal strings`
        )
      );
    }
  }
  if (dataField.isCyclic) {
    // cyclic ordinal types require values constraint
    if (dataField.optype === "ordinal" && !hasValidValues(dataField)) {
      validation.set(
        `DataDictionary.DataField[${dataDictionaryIndex}].Value`,
        new ValidationEntry(
          ValidationLevel.WARNING,
          `"${dataField.name}" data type, Values list is required for cyclic ordinal data types`
        )
      );
    }
    if (dataField.optype === "continuous") {
      // cyclic continuous types require one interval constraint or values constraint
      if (!hasValidValues(dataField) && !hasIntervals(dataField)) {
        validation.set(
          `DataDictionary.DataField[${dataDictionaryIndex}].Interval`,
          new ValidationEntry(
            ValidationLevel.WARNING,
            `"${dataField.name}" data type, A Value or Interval constraint is required for cyclic continuous data types`
          )
        );
      }
      // cyclic continuous types can have only a single interval constraint
      if (dataField.Interval && dataField.Interval?.length > 1) {
        validation.set(
          `DataDictionary.DataField[${dataDictionaryIndex}].Interval`,
          new ValidationEntry(
            ValidationLevel.WARNING,
            `"${dataField.name}" data type, Continuous data types can have only a single interval constraint`
          )
        );
      }
    }
  }
};

// "valid" values correspond to value constraints (other types of values are isMissing or isInvalid)
const hasValidValues = (dataField: DataField) => {
  return (
    dataField.Value &&
    dataField.Value.filter(value => value.property === "valid" || value.property === undefined).length > 0
  );
};

const hasIntervals = (dataField: DataField) => {
  return dataField.Interval && dataField.Interval.length > 0;
};

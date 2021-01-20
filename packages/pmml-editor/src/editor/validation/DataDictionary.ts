/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { DataField } from "@kogito-tooling/pmml-editor-marshaller";
import { ValidationService } from "./ValidationService";
import { ValidationEntry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";

export const validateDataFieldConstraintRanges = (
  dataField: DataField,
  dataDictionaryIndex: number,
  validation: ValidationService
): void => {
  dataField.Interval?.forEach((interval, index) => {
    if (interval.leftMargin === undefined && interval.rightMargin === undefined) {
      validation.set(
        `DataDictionary.DataField[${dataDictionaryIndex}].Interval[${index}]`,
        new ValidationEntry(
          ValidationLevel.WARNING,
          `Data Field[${dataDictionaryIndex}], Interval[${index}] must have the start and/or end value set.`
        )
      );
    }
  });
};

export const validateDataFieldsConstraintRanges = (dataFields: DataField[], validation: ValidationService): void => {
  dataFields.forEach((dataField, dataDictionaryIndex) =>
    validateDataFieldConstraintRanges(dataField, dataDictionaryIndex, validation)
  );
};

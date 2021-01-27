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
import { OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { ValidationService } from "./ValidationService";
import { ValidationEntry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";

export const validateOutputs = (outputFields: OutputField[], validation: ValidationService): void => {
  outputFields.forEach((outputField, dataDictionaryIndex) =>
    validateOutput(outputField, dataDictionaryIndex, validation)
  );
};

export const validateOutput = (
  outputField: OutputField,
  dataDictionaryIndex: number,
  validation: ValidationService
): void => {
  // required interval margins
  // dataField.Interval?.forEach((interval, index) => {
  //   if (interval.leftMargin === undefined && interval.rightMargin === undefined) {
  //     validation.set(
  //       `DataDictionary.DataField[${dataDictionaryIndex}].Interval[${index}]`,
  //       new ValidationEntry(
  //         ValidationLevel.WARNING,
  //         `"${dataField.name}" data type, Interval (${index + 1}) must have the start and/or end value set.`
  //       )
  //     );
  //   }
  // });
};

export const validateOutputsRequiredTargetField = (
  modelIndex: number,
  outputFields: OutputField[] | undefined,
  validation: ValidationService
) => {
  outputFields?.forEach((field, fieldIndex) => {
    if (field.targetField === undefined) {
      validation.set(
        `models[${modelIndex}].Output.OutputField[${fieldIndex}].targetField`,
        new ValidationEntry(
          ValidationLevel.WARNING,
          `"${field.name}" output field, target field is required if the model has multiple target fields.`
        )
      );
    }
  });
};

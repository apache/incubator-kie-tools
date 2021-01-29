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
import { MiningField, OutputField } from "@kogito-tooling/pmml-editor-marshaller";
import { ValidationService } from "./ValidationService";
import { ValidationEntry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";

export const validateOutputs = (
  modelIndex: number,
  outputFields: OutputField[],
  miningFields: MiningField[],
  validation: ValidationService
): void => {
  if (outputFields.length === 0) {
    validation.set(
      `models[${modelIndex}].Output`,
      new ValidationEntry(ValidationLevel.WARNING, `At least one Output Field is required.`)
    );
  }
  outputFields?.forEach((outputField, dataDictionaryIndex) =>
    validateOutput(modelIndex, outputField, dataDictionaryIndex, miningFields, validation)
  );
};

export const validateOutput = (
  modelIndex: number,
  outputField: OutputField,
  outputFieldIndex: number,
  miningFields: MiningField[],
  validation: ValidationService
): void => {
  if (isOutputsTargetFieldRequired(miningFields) && outputField.targetField === undefined) {
    validation.set(
      `models[${modelIndex}].Output.OutputField[${outputFieldIndex}].targetField`,
      new ValidationEntry(
        ValidationLevel.WARNING,
        `"${outputField.name}": target field is required if Mining Schema has multiple target fields.`
      )
    );
  }
};

export const isOutputsTargetFieldRequired = (miningFields: MiningField[], miningSchemaCount = 1) => {
  return miningFields.filter(field => field.usageType === "target").length > miningSchemaCount;
};

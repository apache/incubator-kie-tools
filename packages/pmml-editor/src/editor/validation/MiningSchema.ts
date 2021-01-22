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
import {
  InvalidValueTreatmentMethod,
  MiningField,
  MissingValueTreatmentMethod,
  OutlierTreatmentMethod
} from "@kogito-tooling/pmml-editor-marshaller";
import { ValidationService } from "./ValidationService";
import { ValidationEntry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";

export const validateMiningField = (
  modelIndex: number,
  miningFieldIndex: number,
  miningField: MiningField,
  validation: ValidationService
): void => {
  //Importance
  const importance = miningField.importance;
  if (importance !== undefined && (importance < 0 || importance > 1)) {
    validation.set(
      `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].importance`,
      new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Importance must be between 0 and 1.`)
    );
  }

  //Low/High values
  const { outliers, lowValue, highValue } = miningField;
  if (areLowHighValuesRequired(outliers)) {
    if (lowValue === undefined && highValue === undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].lowValue`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Low and/or High Value must be set.`)
      );
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].highValue`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Low and/or High Value must be set.`)
      );
    }
  } else {
    if (lowValue !== undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].lowValue`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Low Value is not needed.`)
      );
    }
    if (highValue !== undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].highValue`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" High Value is not needed.`)
      );
    }
  }

  //Missing values
  const { missingValueTreatment, missingValueReplacement } = miningField;
  if (isMissingValueReplacementRequired(missingValueTreatment)) {
    if (missingValueReplacement === undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].missingValueReplacement`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Missing Value Replacement must be set.`)
      );
    }
  } else {
    if (missingValueReplacement !== undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].missingValueReplacement`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Missing Value Replacement is not required.`)
      );
    }
  }

  //Invalid values
  const { invalidValueTreatment, invalidValueReplacement } = miningField;
  if (isInvalidValueReplacementRequired(invalidValueTreatment)) {
    if (invalidValueReplacement === undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].invalidValueReplacement`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Invalid Value Replacement must be set.`)
      );
    }
  } else {
    if (invalidValueReplacement !== undefined) {
      validation.set(
        `models[${modelIndex}].MiningSchema.MiningField[${miningFieldIndex}].invalidValueReplacement`,
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Invalid Value Replacement is not required.`)
      );
    }
  }
};

export const validateMiningFields = (
  modelIndex: number,
  miningFields: MiningField[],
  validation: ValidationService
): void => {
  miningFields.forEach((miningField, miningFieldIndex) =>
    validateMiningField(modelIndex, miningFieldIndex, miningField, validation)
  );
};

export const areLowHighValuesRequired = (outliers: OutlierTreatmentMethod | string | undefined) =>
  outliers === "asExtremeValues" || outliers === "asMissingValues";

export const isMissingValueReplacementRequired = (
  missingValueTreatment: MissingValueTreatmentMethod | string | undefined
) => missingValueTreatment === "asMean" || missingValueTreatment === "asMedian" || missingValueTreatment === "asMode";

export const isInvalidValueReplacementRequired = (
  invalidValueTreatment: InvalidValueTreatmentMethod | string | undefined
) => invalidValueTreatment === "asValue";

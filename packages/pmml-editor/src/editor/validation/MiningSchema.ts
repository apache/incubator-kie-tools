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

import {
  DataField,
  InvalidValueTreatmentMethod,
  MiningField,
  MissingValueTreatmentMethod,
  OutlierTreatmentMethod,
} from "@kie-tools/pmml-editor-marshaller";
import { ValidationEntry, ValidationRegistry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder } from "../paths";

export const validateMiningFields = (
  modelIndex: number,
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  miningFields.forEach((miningField, miningFieldIndex) =>
    validateMiningField(modelIndex, miningFieldIndex, miningField, validationRegistry)
  );
};

export const validateMiningField = (
  modelIndex: number,
  miningFieldIndex: number,
  miningField: MiningField,
  validationRegistry: ValidationRegistry
): void => {
  //Importance
  const importance = miningField.importance;
  if (importance !== undefined && (importance < 0 || importance > 1)) {
    validationRegistry.set(
      Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forImportance().build(),
      new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Importance must be between 0 and 1.`)
    );
  }

  //Low/High values
  const { outliers, lowValue, highValue } = miningField;
  if (areLowHighValuesRequired(outliers)) {
    if (lowValue === undefined && highValue === undefined) {
      validationRegistry.set(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forLowValue().build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Low and/or High Value must be set.`)
      );
      validationRegistry.set(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forHighValue().build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Low and/or High Value must be set.`)
      );
    }
  } else {
    if (lowValue !== undefined) {
      validationRegistry.set(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forLowValue().build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Low Value is not needed.`)
      );
    }
    if (highValue !== undefined) {
      validationRegistry.set(
        Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forHighValue().build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" High Value is not needed.`)
      );
    }
  }

  //Missing values
  const { missingValueTreatment, missingValueReplacement } = miningField;
  if (isMissingValueReplacementRequired(missingValueTreatment)) {
    if (missingValueReplacement === undefined) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .forMiningField(miningFieldIndex)
          .forMissingValueReplacement()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Missing Value Replacement must be set.`)
      );
    }
  } else {
    if (missingValueReplacement !== undefined) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .forMiningField(miningFieldIndex)
          .forMissingValueReplacement()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Missing Value Replacement is not required.`)
      );
    }
  }

  //Invalid values
  const { invalidValueTreatment, invalidValueReplacement } = miningField;
  if (isInvalidValueReplacementRequired(invalidValueTreatment)) {
    if (invalidValueReplacement === undefined) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .forMiningField(miningFieldIndex)
          .forInvalidValueReplacement()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Invalid Value Replacement must be set.`)
      );
    }
  } else {
    if (invalidValueReplacement !== undefined) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forMiningSchema()
          .forMiningField(miningFieldIndex)
          .forInvalidValueReplacement()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" Invalid Value Replacement is not required.`)
      );
    }
  }
};

export const validateMiningFieldsDataFieldReference = (
  modelIndex: number,
  dataFields: DataField[],
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  miningFields.forEach((miningField, miningFieldIndex) =>
    validateMiningFieldDataFieldReference(modelIndex, dataFields, miningFieldIndex, miningField, validationRegistry)
  );
};

export const validateMiningFieldDataFieldReference = (
  modelIndex: number,
  dataFields: DataField[],
  miningFieldIndex: number,
  miningField: MiningField,
  validationRegistry: ValidationRegistry
): void => {
  if (dataFields.filter((dataField) => dataField.name === miningField.name).length === 0) {
    validationRegistry.set(
      Builder().forModel(modelIndex).forMiningSchema().forMiningField(miningFieldIndex).forDataFieldMissing().build(),
      new ValidationEntry(ValidationLevel.WARNING, `"${miningField.name}" cannot be found in the Data Dictionary.`)
    );
  }
};

export const areLowHighValuesRequired = (outliers: OutlierTreatmentMethod | string | undefined) =>
  outliers === "asExtremeValues" || outliers === "asMissingValues";

export const isMissingValueReplacementRequired = (
  missingValueTreatment: MissingValueTreatmentMethod | string | undefined
) => missingValueTreatment === "asMean" || missingValueTreatment === "asMedian" || missingValueTreatment === "asMode";

export const isInvalidValueReplacementRequired = (
  invalidValueTreatment: InvalidValueTreatmentMethod | string | undefined
) => invalidValueTreatment === "asValue";

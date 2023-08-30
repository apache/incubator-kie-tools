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

import { Cyclic, DataField, DataType, OpType } from "@kie-tools/pmml-editor-marshaller";
import { ValidationEntry, ValidationRegistry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder } from "../paths";

export const validateDataFields = (dataFields: DataField[], validationRegistry: ValidationRegistry): void => {
  dataFields.forEach((dataField, dataDictionaryIndex) =>
    validateDataField(dataField, dataDictionaryIndex, validationRegistry)
  );
};

export const validateDataField = (
  dataField: DataField,
  dataDictionaryIndex: number,
  validationRegistry: ValidationRegistry
): void => {
  // required interval margins
  dataField.Interval?.forEach((interval, index) => {
    if (interval.leftMargin === undefined && interval.rightMargin === undefined) {
      validationRegistry.set(
        Builder().forDataDictionary().forDataField(dataDictionaryIndex).forInterval(index).build(),
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
      validationRegistry.set(
        Builder().forDataDictionary().forDataField(dataDictionaryIndex).forValue(index).build(),
        new ValidationEntry(
          ValidationLevel.WARNING,
          `"${dataField.name}" data type, Empty Constraint Value (${index + 1})`
        )
      );
    }
  });
  // ordinal strings constraints requirements
  if (dataField.dataType === "string" && dataField.optype === "ordinal") {
    if (!hasValidValues(dataField)) {
      validationRegistry.set(
        Builder().forDataDictionary().forDataField(dataDictionaryIndex).forInterval().build(),
        new ValidationEntry(
          ValidationLevel.WARNING,
          `"${dataField.name}" data type, Values list required for ordinal strings`
        )
      );
    }
  }
  if (dataField.isCyclic === "1") {
    // cyclic ordinal types require values constraint
    if (dataField.optype === "ordinal" && !hasValidValues(dataField)) {
      validationRegistry.set(
        Builder().forDataDictionary().forDataField(dataDictionaryIndex).forValue().build(),
        new ValidationEntry(
          ValidationLevel.WARNING,
          `"${dataField.name}" data type, Values list is required for cyclic ordinal data types`
        )
      );
    }
    if (dataField.optype === "continuous") {
      // cyclic continuous types require one interval constraint or values constraint
      if (!hasValidValues(dataField) && !hasIntervals(dataField)) {
        validationRegistry.set(
          Builder().forDataDictionary().forDataField(dataDictionaryIndex).forInterval().build(),
          new ValidationEntry(
            ValidationLevel.WARNING,
            `"${dataField.name}" data type, A Value or Interval constraint is required for cyclic continuous data types`
          )
        );
      }
      // cyclic continuous types can have only a single interval constraint
      if (dataField.Interval && dataField.Interval?.length > 1) {
        validationRegistry.set(
          Builder().forDataDictionary().forDataField(dataDictionaryIndex).forInterval().build(),
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
export const hasValidValues = (dataField: DataField) => {
  return (
    dataField.Value &&
    dataField.Value.filter((value) => value.property === "valid" || value.property === undefined).length > 0
  );
};

export const hasIntervals = (dataField: DataField) => {
  return dataField.Interval && dataField.Interval.length > 0;
};

export const hasOnlyEmptyIntervals = (dataField: DataField) => {
  return dataField.Interval?.every(
    (interval) => interval.leftMargin === undefined && interval.rightMargin === undefined
  );
};

export const hasOnlyEmptyValues = (dataField: DataField) => {
  const validValues = dataField.Value?.filter((value) => value.property === undefined || value.property === "valid");
  return validValues?.every((value) => value.value === "");
};

export const shouldConstraintsBeCleared = (
  updatedDataField: DataField,
  isCyclic: Cyclic | undefined,
  dataType: DataType,
  optype: OpType
) => {
  let result = false;
  // if it's not a cyclic type and has empty constraints (value or interval)
  if (
    isCyclic === "1" &&
    updatedDataField.isCyclic !== "1" &&
    !(updatedDataField.optype === "ordinal" && updatedDataField.dataType === "string") &&
    (hasOnlyEmptyIntervals(updatedDataField) || hasOnlyEmptyValues(updatedDataField))
  ) {
    result = true;
  }
  // if it's not an ordinal string and has empty value constraints
  if (
    isCyclic !== "1" &&
    updatedDataField.isCyclic !== "1" &&
    (updatedDataField.optype !== "ordinal" || updatedDataField.dataType !== "string") &&
    optype === "ordinal" &&
    dataType === "string" &&
    hasOnlyEmptyValues(updatedDataField)
  ) {
    result = true;
  }

  return result;
};

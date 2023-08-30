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
  Attribute,
  Characteristic,
  CompoundPredicate,
  False,
  MiningField,
  Predicate,
  SimplePredicate,
  SimpleSetPredicate,
  True,
} from "@kie-tools/pmml-editor-marshaller";
import { ValidationEntry, ValidationRegistry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder, PredicateBuilder } from "../paths";

export const validateAttribute = (
  modelIndex: number,
  scorecardProperties: {
    baselineScore: number | undefined;
    useReasonCodes: boolean | undefined;
  },
  characteristicIndex: number,
  characteristic: Characteristic,
  isPartialScoreRequired: boolean,
  attributeIndex: number,
  attribute: Attribute,
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  if (
    scorecardProperties.useReasonCodes !== false &&
    characteristic.reasonCode === undefined &&
    attribute.reasonCode === undefined
  ) {
    validationRegistry.set(
      Builder()
        .forModel(modelIndex)
        .forCharacteristics()
        .forCharacteristic(characteristicIndex)
        .forAttribute(attributeIndex)
        .forReasonCode()
        .build(),
      new ValidationEntry(ValidationLevel.WARNING, `"${characteristic.name} attribute: Reason code is required.`)
    );
  }

  if (isPartialScoreRequired && attribute.partialScore === undefined) {
    validationRegistry.set(
      Builder()
        .forModel(modelIndex)
        .forCharacteristics()
        .forCharacteristic(characteristicIndex)
        .forAttribute(attributeIndex)
        .forPartialScore()
        .build(),
      new ValidationEntry(ValidationLevel.WARNING, `"${characteristic.name} attribute: Partial score is required.`)
    );
  }

  //Predicates
  const fieldNames = miningFields.map((miningField) => miningField.name);
  validatePredicate(
    Builder()
      .forModel(modelIndex)
      .forCharacteristics()
      .forCharacteristic(characteristicIndex)
      .forAttribute(attributeIndex)
      .forPredicate(),
    attribute.predicate,
    fieldNames,
    validationRegistry
  );
};

export const validateAttributes = (
  modelIndex: number,
  scorecardProperties: {
    baselineScore: number | undefined;
    useReasonCodes: boolean | undefined;
  },
  characteristicIndex: number,
  characteristic: Characteristic,
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  const isPartialScoreRequired =
    characteristic.Attribute.filter((attribute) => attribute.partialScore !== undefined).length > 0;

  characteristic.Attribute.forEach((attribute, attributeIndex) =>
    validateAttribute(
      modelIndex,
      scorecardProperties,
      characteristicIndex,
      characteristic,
      isPartialScoreRequired,
      attributeIndex,
      attribute,
      miningFields,
      validationRegistry
    )
  );
};

const validatePredicate = (
  pathBuilder: PredicateBuilder,
  predicate: Predicate | undefined,
  fieldNames: string[],
  validationRegistry: ValidationRegistry
) => {
  if (predicate === undefined) {
    validationRegistry.set(pathBuilder.build(), new ValidationEntry(ValidationLevel.WARNING, `No predicate defined.`));
    return;
  } else if (predicate instanceof True) {
    return;
  } else if (predicate instanceof False) {
    return;
  } else if (predicate instanceof SimpleSetPredicate) {
    if (fieldNames.filter((fieldName) => fieldName === predicate.field).length === 0) {
      validationRegistry.set(
        pathBuilder.forFieldName().build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${predicate.field}" cannot be found in the Mining Schema.`)
      );
    }
  } else if (predicate instanceof SimplePredicate) {
    if (fieldNames.filter((fieldName) => fieldName === predicate.field).length === 0) {
      validationRegistry.set(
        pathBuilder.forFieldName().build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${predicate.field}" cannot be found in the Mining Schema.`)
      );
    }
  } else if (predicate instanceof CompoundPredicate) {
    predicate.predicates?.forEach((p, i) =>
      validatePredicate(pathBuilder.forPredicate(i), p, fieldNames, validationRegistry)
    );
  }
};

export const areAttributesReasonCodesMissing = (attributes: Attribute[]) => {
  if (attributes.length === 0) {
    return true;
  }
  return !attributes.every((attribute) => attribute.reasonCode !== undefined);
};

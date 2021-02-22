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
  Attribute,
  CompoundPredicate,
  False,
  FieldName,
  MiningField,
  Predicate,
  SimplePredicate,
  SimpleSetPredicate,
  True
} from "@kogito-tooling/pmml-editor-marshaller";
import { ValidationEntry, ValidationRegistry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder } from "../paths";

export const validateAttribute = (
  modelIndex: number,
  characteristicIndex: number,
  attributeIndex: number,
  attribute: Attribute,
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  //Predicates
  const fieldNames = miningFields.map(miningField => miningField.name);
  validatePredicate(
    modelIndex,
    characteristicIndex,
    attributeIndex,
    attribute.predicate,
    fieldNames,
    validationRegistry,
    0
  );
};

export const validateAttributes = (
  modelIndex: number,
  characteristicIndex: number,
  attributes: Attribute[],
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  attributes.forEach((attribute, attributeIndex) =>
    validateAttribute(modelIndex, characteristicIndex, attributeIndex, attribute, miningFields, validationRegistry)
  );
};

const validatePredicate = (
  modelIndex: number,
  characteristicIndex: number,
  attributeIndex: number,
  predicate: Predicate | undefined,
  fieldNames: FieldName[],
  validationRegistry: ValidationRegistry,
  nesting: number
) => {
  if (predicate === undefined) {
    return;
  } else if (predicate instanceof True) {
    return;
  } else if (predicate instanceof False) {
    return;
  } else if (predicate instanceof SimpleSetPredicate) {
    if (fieldNames.filter(fieldName => fieldName === predicate.field).length === 0) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forPredicate(nesting)
          .forFieldName()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${predicate.field}" cannot be not found in the Mining Schema.`)
      );
    }
  } else if (predicate instanceof SimplePredicate) {
    if (fieldNames.filter(fieldName => fieldName === predicate.field).length === 0) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forAttribute(attributeIndex)
          .forPredicate(nesting)
          .forFieldName()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `"${predicate.field}" cannot be not found in the Mining Schema.`)
      );
    }
  } else if (predicate instanceof CompoundPredicate) {
    predicate.predicates?.forEach(p =>
      validatePredicate(modelIndex, characteristicIndex, attributeIndex, p, fieldNames, validationRegistry, nesting + 1)
    );
  }
};

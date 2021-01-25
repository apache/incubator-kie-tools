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
import { Characteristic, MiningField } from "@kogito-tooling/pmml-editor-marshaller";
import { ValidationService } from "./ValidationService";
import { validateAttributes } from "./Attributes";

export const validateCharacteristic = (
  modelIndex: number,
  characteristicIndex: number,
  characteristic: Characteristic,
  miningFields: MiningField[],
  validation: ValidationService
): void => {
  //Attributes
  validateAttributes(modelIndex, characteristicIndex, characteristic.Attribute, miningFields, validation);
};

export const validateCharacteristics = (
  modelIndex: number,
  characteristics: Characteristic[],
  miningFields: MiningField[],
  validation: ValidationService
): void => {
  characteristics.forEach((characteristic, characteristicIndex) =>
    validateCharacteristic(modelIndex, characteristicIndex, characteristic, miningFields, validation)
  );
};

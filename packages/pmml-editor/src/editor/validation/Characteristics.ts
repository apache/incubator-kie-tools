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

import { Characteristic, MiningField } from "@kie-tools/pmml-editor-marshaller";
import { ValidationRegistry } from "./ValidationRegistry";
import { areAttributesReasonCodesMissing, validateAttributes } from "./Attributes";
import { ValidationEntry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder } from "../paths";

export const validateCharacteristic = (
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
  if (scorecardProperties.useReasonCodes !== false) {
    if (characteristic.reasonCode === undefined && areAttributesReasonCodesMissing(characteristic.Attribute)) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forReasonCode()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `${characteristic.name}: Reason code is required`)
      );
    }
    if (scorecardProperties.baselineScore === undefined && characteristic.baselineScore === undefined) {
      validationRegistry.set(
        Builder()
          .forModel(modelIndex)
          .forCharacteristics()
          .forCharacteristic(characteristicIndex)
          .forBaselineScore()
          .build(),
        new ValidationEntry(ValidationLevel.WARNING, `${characteristic.name}: Baseline score is required`)
      );
    }
  }
  //Attributes
  validateAttributes(
    modelIndex,
    scorecardProperties,
    characteristicIndex,
    characteristic,
    miningFields,
    validationRegistry
  );
};

export const validateCharacteristics = (
  modelIndex: number,
  scorecardProperties: {
    baselineScore: number | undefined;
    useReasonCodes: boolean | undefined;
  },
  characteristics: Characteristic[],
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  characteristics.forEach((characteristic, characteristicIndex) =>
    validateCharacteristic(
      modelIndex,
      scorecardProperties,
      characteristicIndex,
      characteristic,
      miningFields,
      validationRegistry
    )
  );
};

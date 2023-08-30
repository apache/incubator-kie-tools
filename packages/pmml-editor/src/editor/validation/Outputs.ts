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

import { MiningField, OutputField } from "@kie-tools/pmml-editor-marshaller";
import { ValidationEntry, ValidationRegistry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder } from "../paths";

export const validateOutputs = (
  modelIndex: number,
  outputFields: OutputField[],
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  if (outputFields.length === 0) {
    validationRegistry.set(
      Builder().forModel(modelIndex).forOutput().build(),
      new ValidationEntry(ValidationLevel.WARNING, `At least one Output Field is required.`)
    );
  }
  outputFields?.forEach((outputField, dataDictionaryIndex) =>
    validateOutput(modelIndex, outputField, dataDictionaryIndex, miningFields, validationRegistry)
  );
};

export const validateOutput = (
  modelIndex: number,
  outputField: OutputField,
  outputFieldIndex: number,
  miningFields: MiningField[],
  validationRegistry: ValidationRegistry
): void => {
  if (isOutputsTargetFieldRequired(miningFields) && outputField.targetField === undefined) {
    validationRegistry.set(
      Builder().forModel(modelIndex).forOutput().forOutputField(outputFieldIndex).forTargetField().build(),
      new ValidationEntry(
        ValidationLevel.WARNING,
        `"${outputField.name}": target field is required if Mining Schema has multiple target fields.`
      )
    );
  }
};

export const isOutputsTargetFieldRequired = (miningFields: MiningField[]) => {
  return miningFields.filter((field) => field.usageType === "target").length > 1;
};

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

import { Characteristic, Scorecard } from "@kie-tools/pmml-editor-marshaller";
import { ValidationEntry, ValidationRegistry } from "./ValidationRegistry";
import { ValidationLevel } from "./ValidationLevel";
import { Builder } from "../paths";

export const validateBaselineScore = (
  modelIndex: number,
  useReasonCodes: Scorecard["useReasonCodes"],
  baselineScore: Scorecard["baselineScore"],
  characteristics: Characteristic[],
  validationRegistry: ValidationRegistry
) => {
  if (
    (useReasonCodes === undefined || useReasonCodes) &&
    baselineScore === undefined &&
    (characteristics.length === 0 ||
      characteristics.filter((characteristic) => characteristic.baselineScore === undefined).length > 0)
  ) {
    validationRegistry.set(
      Builder().forModel(modelIndex).forBaselineScore().build(),
      new ValidationEntry(ValidationLevel.WARNING, `Baseline score is required`)
    );
  }
};

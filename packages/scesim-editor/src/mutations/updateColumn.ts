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

import _ from "lodash";
import {
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

export function updateColumn({
  className,
  expressionAlias,
  expressionElementsSteps,
  expressionIdentifierName,
  expressionIdentifierType,
  factMappings,
  factMappingValuesTypes,
  factMappingValueType,
  factClassName,
  factIdentifierClassName,
  factIdentifierName,
  factName,
  genericTypes,
  selectedColumnIndex,
}: {
  className: string;
  expressionAlias: string;
  expressionElementsSteps: string[];
  expressionIdentifierName: string | undefined;
  expressionIdentifierType: string | undefined;
  factMappings: SceSim__FactMappingType[];
  factMappingValuesTypes: SceSim__FactMappingValuesTypes[];
  factMappingValueType: "EXPRESSION" | "NOT_EXPRESSION";
  factClassName: string;
  factIdentifierClassName: string | undefined;
  factIdentifierName: string | undefined;
  factName: string;
  genericTypes: string[];
  selectedColumnIndex: number;
}): { updatedFactMapping: SceSim__FactMappingType } {
  const factMappingToUpdate = factMappings[selectedColumnIndex];
  factMappingToUpdate.className = { __$$text: className };
  factMappingToUpdate.expressionAlias = { __$$text: expressionAlias };
  factMappingToUpdate.expressionElements = {
    ExpressionElement: expressionElementsSteps.map((ee) => {
      return { step: { __$$text: ee } };
    }),
  };
  factMappingToUpdate.factAlias = { __$$text: factName };
  factMappingToUpdate.factIdentifier.className = { __$$text: factClassName };
  factMappingToUpdate.factIdentifier.name = { __$$text: factName };
  factMappingToUpdate.factMappingValueType = { __$$text: factMappingValueType };
  factMappingToUpdate.genericTypes =
    genericTypes.length > 0
      ? {
          string: genericTypes.map((genericType) => {
            return { __$$text: genericType };
          }),
        }
      : undefined;

  factMappingValuesTypes.forEach((fmv) => {
    const factMappingValues = fmv.factMappingValues.FactMappingValue!;
    const factMappingValueToUpdateIndex = factMappingValues.findIndex(
      (factMappingValue) =>
        factMappingValue.factIdentifier.name?.__$$text === factIdentifierName &&
        factMappingValue.factIdentifier.className?.__$$text === factIdentifierClassName &&
        factMappingValue.expressionIdentifier.name?.__$$text === expressionIdentifierName &&
        factMappingValue.expressionIdentifier.type?.__$$text === expressionIdentifierType
    );
    const factMappingValueToUpdate = factMappingValues[factMappingValueToUpdateIndex];
    factMappingValueToUpdate.factIdentifier.className = { __$$text: factClassName };
    factMappingValueToUpdate.factIdentifier.name = { __$$text: factName };
    factMappingValueToUpdate.rawValue = undefined;
  });

  return { updatedFactMapping: JSON.parse(JSON.stringify(factMappingToUpdate)) };
}

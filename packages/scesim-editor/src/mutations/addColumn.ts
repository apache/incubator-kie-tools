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
import { v4 as uuid } from "uuid";

import {
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

/* To be called to create a new Column with a defined Instance and undefined Property */
export function addColumnWithEmptyProperty({
  expressionElementsSteps,
  expressionIdentifierType,
  factAlias,
  factIdentifierClassName,
  factIdentifierName,
  factMappings,
  factMappingValuesTypes,
  targetColumnIndex,
}: {
  expressionElementsSteps: string[];
  expressionIdentifierType: string;
  factAlias: string;
  factIdentifierClassName: string;
  factIdentifierName: string;
  factMappings: SceSim__FactMappingType[];
  factMappingValuesTypes: SceSim__FactMappingValuesTypes[];
  targetColumnIndex: number;
}) {
  addColumn({
    expressionElementsSteps,
    expressionIdentifierType,
    factAlias,
    factIdentifierClassName,
    factIdentifierName,
    factMappings: factMappings,
    factMappingValuesTypes,
    targetColumnIndex,
  });
}

/* To be called to create a new undefined Column, i.e. with a undefined Instance and undefined Property */
export function addColumnWithEmptyInstanceAndProperty({
  expressionIdentifierType,
  factMappings,
  factMappingValuesTypes,
  targetColumnIndex,
}: {
  expressionIdentifierType: string;
  factMappings: SceSim__FactMappingType[];
  factMappingValuesTypes: SceSim__FactMappingValuesTypes[];
  targetColumnIndex: number;
}) {
  addColumn({
    expressionIdentifierType,
    factMappings: factMappings,
    factMappingValuesTypes,
    targetColumnIndex,
  });
}

function addColumn({
  expressionElementsSteps,
  expressionIdentifierType,
  factAlias,
  factIdentifierClassName,
  factIdentifierName,
  factMappings,
  factMappingValuesTypes,
  targetColumnIndex,
}: {
  expressionElementsSteps?: string[];
  expressionIdentifierType: string;
  factAlias?: string;
  factIdentifierClassName?: string;
  factIdentifierName?: string;
  factMappings: SceSim__FactMappingType[];
  factMappingValuesTypes: SceSim__FactMappingValuesTypes[];
  targetColumnIndex: number;
}) {
  const instanceDefaultNames = factMappings
    .filter((factMapping) => factMapping.factAlias!.__$$text.startsWith("INSTANCE-"))
    .map((factMapping) => factMapping.factAlias!.__$$text);

  const propertyDefaultNames = factMappings
    .filter((factMapping) => factMapping.expressionAlias?.__$$text.startsWith("PROPERTY-"))
    .map((factMapping) => factMapping.expressionAlias!.__$$text);

  const newFactMapping = {
    className: { __$$text: "java.lang.Void" },
    columnWidth: { __$$text: 150 },
    expressionAlias: { __$$text: getNextAvailablePrefixedName(propertyDefaultNames, "PROPERTY") },
    expressionElements: expressionElementsSteps
      ? {
          ExpressionElement: expressionElementsSteps.map((ee) => {
            return { step: { __$$text: ee } };
          }),
        }
      : undefined,
    expressionIdentifier: {
      name: { __$$text: `_${uuid()}`.toLocaleUpperCase() },
      type: { __$$text: expressionIdentifierType },
    },
    factAlias: {
      __$$text: factAlias ?? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE"),
    },
    factIdentifier: {
      name: {
        __$$text: factIdentifierName ?? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE"),
      },
      className: {
        __$$text: factIdentifierClassName ?? "java.lang.Void",
      },
    },
    factMappingValueType: { __$$text: "NOT_EXPRESSION" },
  };

  factMappings.splice(targetColumnIndex, 0, newFactMapping);

  factMappingValuesTypes.forEach((fmvt) => {
    fmvt.factMappingValues.FactMappingValue!.splice(targetColumnIndex, 0, {
      expressionIdentifier: {
        name: { __$$text: newFactMapping.expressionIdentifier.name.__$$text },
        type: { __$$text: newFactMapping.expressionIdentifier.type.__$$text },
      },
      factIdentifier: {
        name: { __$$text: newFactMapping.factIdentifier.name.__$$text },
        className: { __$$text: newFactMapping.factIdentifier.className.__$$text },
      },
      rawValue: { __$$text: "", "@_class": "string" },
    });
  });
}

const getNextAvailablePrefixedName = (
  names: string[],
  namePrefix: string,
  lastIndex: number = names.length
): string => {
  const candidate = `${namePrefix}-${lastIndex + 1}`;
  const elemWithCandidateName = names.indexOf(candidate);
  return elemWithCandidateName >= 0 ? getNextAvailablePrefixedName(names, namePrefix, lastIndex + 1) : candidate;
};

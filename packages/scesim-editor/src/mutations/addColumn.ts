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
import { InsertRowColumnsDirection } from "@kie-tools/boxed-expression-component/dist/api/BeeTable";

export function addColumn({
  beforeIndex,
  factMappings,
  factMappingValues,
  insertDirection,
  isInstance,
  selectedColumnFactMappingIndex,
}: {
  beforeIndex: number;
  factMappings: SceSim__FactMappingType[];
  factMappingValues: SceSim__FactMappingValuesTypes[];
  insertDirection: InsertRowColumnsDirection;
  isInstance: boolean;
  selectedColumnFactMappingIndex: number;
}) {
  const selectedColumnFactMapping = factMappings[selectedColumnFactMappingIndex];
  const targetColumnIndex = determineNewColumnTargetIndex(
    factMappings,
    insertDirection,
    isInstance,
    selectedColumnFactMappingIndex,
    selectedColumnFactMapping
  );

  const instanceDefaultNames = factMappings
    .filter((factMapping) => factMapping.factAlias!.__$$text.startsWith("INSTANCE-"))
    .map((factMapping) => factMapping.factAlias!.__$$text);

  const propertyDefaultNames = factMappings
    .filter((factMapping) => factMapping.expressionAlias?.__$$text.startsWith("PROPERTY-"))
    .map((factMapping) => factMapping.expressionAlias!.__$$text);

  const isNewInstance = isInstance || selectedColumnFactMapping.factIdentifier.className?.__$$text === "java.lang.Void";

  const newFactMapping = {
    className: { __$$text: "java.lang.Void" },
    columnWidth: { __$$text: 150 },
    expressionAlias: { __$$text: getNextAvailablePrefixedName(propertyDefaultNames, "PROPERTY") },
    expressionElements: isNewInstance
      ? undefined
      : {
          ExpressionElement: [
            {
              step: {
                __$$text: selectedColumnFactMapping.expressionElements!.ExpressionElement![0].step.__$$text,
              },
            },
          ],
        },
    expressionIdentifier: {
      name: { __$$text: `_${uuid()}`.toLocaleUpperCase() },
      type: { __$$text: selectedColumnFactMapping.expressionIdentifier.type!.__$$text },
    },
    factAlias: {
      __$$text: isNewInstance
        ? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE")
        : selectedColumnFactMapping.factAlias.__$$text,
    },
    factIdentifier: {
      name: {
        __$$text: isNewInstance
          ? getNextAvailablePrefixedName(instanceDefaultNames, "INSTANCE")
          : selectedColumnFactMapping.factIdentifier.name!.__$$text,
      },
      className: {
        __$$text: isNewInstance ? "java.lang.Void" : selectedColumnFactMapping.factIdentifier.className!.__$$text,
      },
    },
    factMappingValueType: { __$$text: "NOT_EXPRESSION" },
  };

  factMappings.splice(targetColumnIndex, 0, newFactMapping);

  factMappingValues.forEach((scenario) => {
    scenario.factMappingValues.FactMappingValue!.splice(beforeIndex + 1, 0, {
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

/* It determines in which index position a column should be added. In case of a field, the new column index is simply 
   in the right or in the left of the selected column. In case of a new instance, it's required to find the first column 
   index outside the selected Instance group. */
const determineNewColumnTargetIndex = (
  factMappings: SceSim__FactMappingType[],
  insertDirection: InsertRowColumnsDirection,
  isInstance: boolean,
  selectedColumnIndex: number,
  selectedFactMapping: SceSim__FactMappingType
) => {
  const groupType = selectedFactMapping.expressionIdentifier.type!.__$$text;
  const instanceName = selectedFactMapping.factIdentifier.name!.__$$text;
  const instanceType = selectedFactMapping.factIdentifier.className!.__$$text;

  if (!isInstance) {
    if (insertDirection === InsertRowColumnsDirection.AboveOrRight) {
      return selectedColumnIndex + 1;
    } else {
      return selectedColumnIndex;
    }
  }

  let newColumnTargetColumn = -1;

  if (insertDirection === InsertRowColumnsDirection.AboveOrRight) {
    for (let i = selectedColumnIndex; i < factMappings.length; i++) {
      const currentFM = factMappings[i];
      if (
        currentFM.expressionIdentifier.type!.__$$text === groupType &&
        currentFM.factIdentifier.name?.__$$text === instanceName &&
        currentFM.factIdentifier.className?.__$$text === instanceType
      ) {
        if (i == factMappings.length - 1) {
          newColumnTargetColumn = i + 1;
        }
      } else {
        newColumnTargetColumn = i;
        break;
      }
    }
  } else {
    for (let i = selectedColumnIndex; i >= 0; i--) {
      const currentFM = factMappings[i];

      if (
        currentFM.expressionIdentifier.type!.__$$text === groupType &&
        currentFM.factIdentifier.name?.__$$text === instanceName &&
        currentFM.factIdentifier.className?.__$$text === instanceType
      ) {
        if (i == 0) {
          newColumnTargetColumn = 0;
        }
      } else {
        newColumnTargetColumn = i + 1;
        break;
      }
    }
  }

  return newColumnTargetColumn;
};

const getNextAvailablePrefixedName = (
  names: string[],
  namePrefix: string,
  lastIndex: number = names.length
): string => {
  const candidate = `${namePrefix}-${lastIndex + 1}`;
  const elemWithCandidateName = names.indexOf(candidate);
  return elemWithCandidateName >= 0 ? getNextAvailablePrefixedName(names, namePrefix, lastIndex + 1) : candidate;
};

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

import _, { isNumber } from "lodash";
import {
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

export function deleteColumn({
  factMappingIndexToRemove,
  factMappings,
  factMappingValues,
  isBackground,
  isInstance,
  selectedColumnIndex,
}: {
  factMappingIndexToRemove: number;
  factMappings: SceSim__FactMappingType[];
  factMappingValues: SceSim__FactMappingValuesTypes[];
  isBackground: boolean;
  isInstance: boolean;
  selectedColumnIndex: number;
}): {
  deletedFactMappingIndexs: number[];
} {
  /* Retriving the FactMapping (Column) to be removed). If the user selected a single column, it finds the exact
       FactMapping to delete. If the user selected an instance (group of columns), it retrives all the FactMappings
       that belongs to the the instance group */
  const factMappingToRemove = factMappings[factMappingIndexToRemove];
  const groupType = factMappingToRemove.expressionIdentifier.type!.__$$text;
  const instanceName = factMappingToRemove.factIdentifier.name!.__$$text;
  const instanceType = factMappingToRemove.factIdentifier.className!.__$$text;

  const allFactMappingWithIndexesToRemove = isInstance
    ? factMappings
        .map((factMapping, index) => {
          if (
            factMapping.expressionIdentifier.type!.__$$text === groupType &&
            factMapping.factIdentifier.name?.__$$text === instanceName &&
            factMapping.factIdentifier.className?.__$$text === instanceType
          ) {
            return { factMappingIndex: index, factMapping: factMapping };
          } else {
            return {};
          }
        })
        .filter((item) => isNumber(item.factMappingIndex))
    : [{ factMappingIndex: selectedColumnIndex + (isBackground ? 0 : 1), factMapping: factMappingToRemove }];

  factMappings.splice(allFactMappingWithIndexesToRemove[0].factMappingIndex!, allFactMappingWithIndexesToRemove.length);

  factMappingValues.forEach((rowData) => {
    allFactMappingWithIndexesToRemove.forEach((itemToRemove) => {
      const factMappingValueColumnIndexToRemove = rowData.factMappingValues.FactMappingValue!.findIndex(
        (factMappingValue) =>
          factMappingValue.factIdentifier.name?.__$$text === itemToRemove.factMapping!.factIdentifier.name?.__$$text &&
          factMappingValue.factIdentifier.className?.__$$text ===
            itemToRemove.factMapping!.factIdentifier.className?.__$$text &&
          factMappingValue.expressionIdentifier.name?.__$$text ===
            itemToRemove.factMapping!.expressionIdentifier.name?.__$$text &&
          factMappingValue.expressionIdentifier.type?.__$$text ===
            itemToRemove.factMapping!.expressionIdentifier.type?.__$$text
      );

      rowData.factMappingValues.FactMappingValue!.splice(factMappingValueColumnIndexToRemove, 1);
    });
  });

  return { deletedFactMappingIndexs: allFactMappingWithIndexesToRemove.flatMap((item) => item.factMappingIndex!) };
}

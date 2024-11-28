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
  SceSim__FactMappingType,
  SceSim__FactMappingValuesTypes,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

export function updateCell({
  columnIndex,
  factMappings,
  factMappingValuesTypes,
  rowIndex,
  value,
}: {
  columnIndex: number;
  factMappings: SceSim__FactMappingType[];
  factMappingValuesTypes: SceSim__FactMappingValuesTypes[];
  rowIndex: number;
  value: any;
}) {
  /* To update the related FactMappingValue, it compares every FactMappingValue associated with the Scenario (Row)
     that contains the cell with the FactMapping (Column) fields factIdentifier and expressionIdentifier */
  const factMapping = factMappings[columnIndex];
  const factMappingValues = factMappingValuesTypes[rowIndex].factMappingValues.FactMappingValue!;

  const factMappingValueToUpdateIndex = factMappingValues.findIndex(
    (factMappingValue) =>
      factMappingValue.factIdentifier.name?.__$$text === factMapping!.factIdentifier.name?.__$$text &&
      factMappingValue.factIdentifier.className?.__$$text === factMapping!.factIdentifier.className?.__$$text &&
      factMappingValue.expressionIdentifier.name?.__$$text === factMapping!.expressionIdentifier.name?.__$$text &&
      factMappingValue.expressionIdentifier.type?.__$$text === factMapping!.expressionIdentifier.type?.__$$text
  );
  const factMappingValueToUpdate = factMappingValues[factMappingValueToUpdateIndex];
  factMappingValueToUpdate.rawValue = { __$$text: value };
}

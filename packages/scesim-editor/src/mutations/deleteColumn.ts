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
import { SceSim__ScenarioSimulationModelType } from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";

export function deleteColumn({
  columnIndex,
  isBackground,
  isInstance,
  scesimModel,
}: {
  columnIndex: number;
  isBackground: boolean;
  isInstance: boolean;
  scesimModel: SceSim__ScenarioSimulationModelType;
}): {
  deletedFactMappingIndexs: number[];
} {
  const factMappings = isBackground
    ? scesimModel.background.scesimModelDescriptor.factMappings.FactMapping!
    : scesimModel.simulation.scesimModelDescriptor.factMappings.FactMapping!;
  const columnIndexToRemove = determineSelectedColumnIndex(factMappings, columnIndex + 1, isInstance);
  const columnIndexStart = isBackground ? 0 : 1;

  /* Retriving the FactMapping (Column) to be removed). If the user selected a single column, it finds the exact
   FactMapping to delete. If the user selected an instance (group of columns), it retrives all the FactMappings
   that belongs to the the instance group */
  const factMappingToRemove = factMappings[columnIndexToRemove];
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
    : [{ factMappingIndex: columnIndex + columnIndexStart, factMapping: factMappingToRemove }];

  if (isBackground) {
    scesimModel.background.scesimModelDescriptor.factMappings.FactMapping!.splice(
      allFactMappingWithIndexesToRemove[0].factMappingIndex!,
      allFactMappingWithIndexesToRemove.length
    );
    //state.scesim.model.ScenarioSimulationModel.background.scesimData.BackgroundData = deepClonedRowsData;
  } else {
    scesimModel.simulation.scesimModelDescriptor.factMappings.FactMapping!.splice(
      allFactMappingWithIndexesToRemove[0].factMappingIndex!,
      allFactMappingWithIndexesToRemove.length
    );
    // state.scesim.model.ScenarioSimulationModel.simulation.scesimData.Scenario = deepClonedRowsData;
  }

  /* Cloning the Scenario List (Rows) and finding the Cell(s) to remove accordingly to the factMapping data of 
  the removed columns */
  //   const deepClonedRowsData: SceSim__FactMappingValuesTypes[] = JSON.parse(
  //     JSON.stringify(retrieveRowsDataFromModel(state.scesim.model.ScenarioSimulationModel, isBackground) ?? [])
  //   );
  //   deepClonedRowsData.forEach((rowData) => {
  //     allFactMappingWithIndexesToRemove.forEach((itemToRemove) => {
  //       const factMappingValueColumnIndexToRemove = retrieveFactMappingValueIndexByIdentifiers(
  //         rowData.factMappingValues.FactMappingValue!,
  //         itemToRemove.factMapping!.factIdentifier,
  //         itemToRemove.factMapping!.expressionIdentifier
  //       )!;

  //       return {
  //         factMappingValues: {
  //           FactMappingValue: rowData.factMappingValues.FactMappingValue!.splice(factMappingValueColumnIndexToRemove, 1),
  //         },
  //       };
  //     });
  //   });
  return { deletedFactMappingIndexs: allFactMappingWithIndexesToRemove.flatMap((item) => item.factMappingIndex!) };
}

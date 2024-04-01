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
  SceSim__FactMappingValueType,
  SceSim__ScenarioSimulationModelType,
  SceSim__expressionIdentifierType,
  SceSim__factIdentifierType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";
import * as React from "react";

/** 
 Given a List of FactMappingValues (Row of Cells), it founds the index of the list's element that matches with the 
 identifiers (factIdentifier and expressionIdentifier) fields.
*/
export const retrieveFactMappingValueIndexByIdentifiers = (
  factMappingValues: SceSim__FactMappingValueType[],
  factIdentifier: SceSim__factIdentifierType,
  expressionIdentifier: SceSim__expressionIdentifierType
) => {
  return factMappingValues.findIndex(
    (factMappingValue) =>
      factMappingValue.factIdentifier.name?.__$$text == factIdentifier.name?.__$$text &&
      factMappingValue.factIdentifier.className?.__$$text == factIdentifier.className?.__$$text &&
      factMappingValue.expressionIdentifier.name?.__$$text == expressionIdentifier.name?.__$$text &&
      factMappingValue.expressionIdentifier.type?.__$$text == expressionIdentifier.type?.__$$text
  );
};

export const retrieveModelDescriptor = (scesimModel: SceSim__ScenarioSimulationModelType, isBackground: boolean) => {
  if (isBackground) {
    return scesimModel.background.scesimModelDescriptor;
  } else {
    return scesimModel.simulation.scesimModelDescriptor;
  }
};

export const retrieveRowsDataFromModel = (scesimModel: SceSim__ScenarioSimulationModelType, isBackground: boolean) => {
  if (isBackground) {
    return scesimModel.background.scesimData.BackgroundData;
  } else {
    return scesimModel.simulation.scesimData.Scenario;
  }
};

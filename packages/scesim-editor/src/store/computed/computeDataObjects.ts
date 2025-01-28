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

import { State, TestScenarioDataObject } from "../TestScenarioEditorStore";

/* It computes the Data Objects consumed in the TestScenarioDrawerDataSelectorPanel. */
export function computeDataObjects(
  testScenarioDataObjects: TestScenarioDataObject[],
  dmnDataObjects: TestScenarioDataObject[],
  scesimType: State["scesim"]["model"]["ScenarioSimulationModel"]["settings"]["type"]
) {
  /* DataObjects retrieved from the DMN file are passed if is a DMN-based Scesim and if are avaialble   */
  /* i.e. correctly computed. In all other cases, DataObjects retrieved from the scesim file are passed */
  if (scesimType?.__$$text === "DMN" && dmnDataObjects && dmnDataObjects.length > 0) {
    return dmnDataObjects;
  }
  return testScenarioDataObjects;
}

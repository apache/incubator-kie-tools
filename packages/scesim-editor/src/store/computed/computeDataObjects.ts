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

/* It computes the Data Objects consumed in the TestScenarioDrawerDataSelectorPanel.  
   The Data Objects can be extracted from the following sources:                      
   - DMN Model (for DMN-based Test Scenarios) 
   - Project's Data Objects (i.e. Java Classes DTOs)  (for RULE-based Test Scenarios) 
   - The Scesim File itself                                                          
   Based on the openend scesim status, this logic will select Data Objects from one 
   of the above sources. A fallback mechansim is in place: if is not possibile to 
   retrieve data from external sources, the Data Objects will be computed from the      
   scesim file itself. For RULE-based Test Scenario this is the current default         
   as we can't still rely on a feature that retrieve data from the project's Java files */
export function computeDataObjects(
  testScenarioDataObjects: TestScenarioDataObject[],
  dmnDataObjects: TestScenarioDataObject[],
  scesimType: State["scesim"]["model"]["ScenarioSimulationModel"]["settings"]["type"]
) {
  if (scesimType?.__$$text === "DMN" && dmnDataObjects && dmnDataObjects.length > 0) {
    return dmnDataObjects;
  }
  return testScenarioDataObjects;
}

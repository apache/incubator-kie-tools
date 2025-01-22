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

import { ExternalDmn, ExternalDmnsIndex } from "../../TestScenarioEditor";
import { Computed, State, TestScenarioDataObject } from "../TestScenarioEditorStore";

export function computeDmnDataTypes(
  externalModelsByNamespace: ExternalDmnsIndex,
  settings: State["scesim"]["model"]["ScenarioSimulationModel"]["settings"]
): TestScenarioDataObject[] {
  const referencedDmns = Object.entries(externalModelsByNamespace ?? {}).reduce((acc, [namespace, externalModel]) => {
    if (!externalModel) {
      console.warn(`Test Scenario EDITOR: Could not find model with namespace '${namespace}'. Ignoring.`);
      return acc;
    } else {
      return acc.set(namespace, externalModel);
    }
  }, new Map<string, ExternalDmn>());

  /* CHECKS external DMN */

  const dmnModel = referencedDmns.get(settings.dmnNamespace!.__$$text);

  if (dmnModel) {
    const itemDefinitions = new Map(
      dmnModel.model.definitions.itemDefinition!.map(
        (itemDefinition) => [itemDefinition["@_name"], itemDefinition] as const
      )
    );

    const inputDataElements = dmnModel.model.definitions.drgElement!.filter(
      (drgElement) => drgElement.__$$element === "inputData"
    );
    const decisionElements = dmnModel.model.definitions.drgElement!.filter(
      (drgElement) => drgElement.__$$element === "decision"
    );

    console.log("**************************************");
    console.log(itemDefinitions);
    console.log(inputDataElements);
    console.log(decisionElements);
  }

  return [];
}

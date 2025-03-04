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
  SceSim__settingsType,
} from "@kie-tools/scesim-marshaller/dist/schemas/scesim-1_8/ts-gen/types";
import { ExternalDmn } from "../TestScenarioEditor";
import { pushFactMappings } from "./pushFactMappings";
import {
  DMN15__tDecision,
  DMN15__tInputData,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

const EMPTY_GIVEN_FACTMAPPING = {
  className: "java.lang.Void",
  columnWidth: 300,
  expressionAlias: "PROPERTY-1",
  expressionElements: [],
  expressionIdentifierType: "GIVEN",
  factAlias: "INSTANCE-1",
  factIdentifierName: "INSTANCE-1",
  factIdentifierClassName: "java.lang.Void",
  genericTypes: undefined,
};

const EMPTY_EXPECT_FACTMAPPING = {
  className: "java.lang.Void",
  columnWidth: 300,
  expressionAlias: "PROPERTY-2",
  expressionElements: [],
  expressionIdentifierType: "EXPECT",
  factAlias: "INSTANCE-2",
  factIdentifierName: "INSTANCE-2",
  factIdentifierClassName: "java.lang.Void",
  genericTypes: undefined,
};

type FactMapping = {
  className: string;
  columnWidth: number;
  expressionAlias: string;
  expressionElements: string[];
  expressionIdentifierType: string;
  factAlias: string;
  factIdentifierName: string;
  factIdentifierClassName: string;
  genericTypes: string[] | undefined;
};

/**
 * It creates a new DMN-type Test Scenario. If isAutoFillTableEnabled is true, the logic automatically fills the
 * table adding a GIVEN column instance for all the DMN Inputs nodes, and an EXPECT column instance for all the DMN Decision nodes.
 * If isAutoFillTableEnabled is false, 2 empty columns are added.
 */
export function createNewDmnTypeTestScenario({
  dmnModel,
  factMappingsModel, // The SceSim FactMappings model (which represent Columns) to be mutated when isAutoFillTableEnabled is true
  factMappingValuesModel, // The SceSim FactMappingValue model (which represent a Data Rows) to be mutated when isAutoFillTableEnabled is true
  isAutoFillTableEnabled, // The user determines if the table should be automatically filled with the extracted data from the DMN Model
  isTestSkipped,
  settingsModel, // The SceSim Setting model to be mutated with the user provided infos.
}: {
  dmnModel: ExternalDmn;
  factMappingsModel: SceSim__FactMappingType[];
  factMappingValuesModel: SceSim__FactMappingValuesTypes[];
  isAutoFillTableEnabled: boolean;
  isTestSkipped: boolean;
  settingsModel: SceSim__settingsType;
}) {
  settingsModel.dmnFilePath = { __$$text: dmnModel.normalizedPosixPathRelativeToTheOpenFile };
  settingsModel.dmnName = { __$$text: dmnModel.model.definitions["@_name"] };
  settingsModel.dmnNamespace = { __$$text: dmnModel.model.definitions["@_namespace"] };
  settingsModel.skipFromBuild = { __$$text: isTestSkipped };
  settingsModel.type = { __$$text: "DMN" };

  let givenFactMappingsToPush = [] as FactMapping[];
  let expectFactMappingsToPush = [] as FactMapping[];

  if (isAutoFillTableEnabled && dmnModel.model.definitions.drgElement) {
    const itemDefinitions = new Map(
      dmnModel.model.definitions.itemDefinition?.map(
        (itemDefinition) => [itemDefinition["@_name"], itemDefinition] as const
      )
    );

    const inputDataElements = dmnModel.model.definitions.drgElement.filter(
      (drgElement) => drgElement.__$$element === "inputData"
    );
    const decisionElements = dmnModel.model.definitions.drgElement.filter(
      (drgElement) => drgElement.__$$element === "decision"
    );

    /* Generating GIVEN and EXPECT FactMappings and their related FactMappingValues
       The call order MATTERS, as the generated elements are pushed in the array,
       we need to generate GIVEN elements BEFORE the EXPECT ones.  */
    givenFactMappingsToPush = generateFactMappingsAndFactMappingValuesFromDmnModel(
      inputDataElements,
      "GIVEN",
      itemDefinitions
    );
    expectFactMappingsToPush = generateFactMappingsAndFactMappingValuesFromDmnModel(
      decisionElements,
      "EXPECT",
      itemDefinitions
    );
  }

  /* If no GIVEN FactMapping is present, we add an empty one. */
  if (givenFactMappingsToPush.length === 0) {
    givenFactMappingsToPush.push(EMPTY_GIVEN_FACTMAPPING);
  }
  /* If no EXPECT FactMapping is present, we add an empty one. */
  if (expectFactMappingsToPush.length === 0) {
    expectFactMappingsToPush.push(EMPTY_EXPECT_FACTMAPPING);
  }

  pushFactMappings({
    factMappingsModel,
    factMappingValuesModel,
    factMappingsToPush: [...givenFactMappingsToPush, ...expectFactMappingsToPush],
  });
}

function generateFactMappingsAndFactMappingValuesFromDmnModel(
  drgElements: DMN15__tInputData[] | DMN15__tDecision[],
  expressionIdentifierType: "EXPECT" | "GIVEN",
  allItemDefinitionsMap: Map<string, DMN15__tItemDefinition>
) {
  const factMappingsToPush = [] as FactMapping[];

  drgElements.forEach((drgElement) => {
    const itemDefinition = allItemDefinitionsMap.get(drgElement.variable!["@_typeRef"]!);
    if (!itemDefinition?.itemComponent || itemDefinition?.itemComponent.length === 0) {
      factMappingsToPush.push({
        className: itemDefinition?.["@_isCollection"] ? "java.util.List" : drgElement.variable!["@_typeRef"]!,
        columnWidth: 100,
        expressionAlias: "value",
        expressionElements: [drgElement.variable!["@_name"]!],
        expressionIdentifierType: expressionIdentifierType,
        factAlias: drgElement.variable!["@_name"]!,
        factIdentifierName: drgElement.variable!["@_name"]!,
        factIdentifierClassName: drgElement.variable!["@_typeRef"]!,
        genericTypes: itemDefinition?.["@_isCollection"] ? [drgElement.variable!["@_typeRef"]!] : undefined,
      });
    } else {
      itemDefinition?.itemComponent!.forEach((itemComponent) => {
        factMappingsToPush.push(
          ...recursevlyNavigateItemComponent(
            allItemDefinitionsMap,
            100,
            [drgElement.variable!["@_name"]!],
            expressionIdentifierType,
            itemComponent,
            drgElement.variable!["@_name"]!,
            drgElement.variable!["@_typeRef"]!
          )
        );
      });
    }
  });

  return factMappingsToPush.sort((a, b) => a.expressionElements.join().localeCompare(b.expressionElements.join()));
}

function recursevlyNavigateItemComponent(
  allItemDefinitionsMap: Map<string, DMN15__tItemDefinition>,
  columnWidth: number,
  expressionElements: string[],
  expressionIdentifierType: "EXPECT" | "GIVEN",
  itemComponent: DMN15__tItemDefinition,
  name: string,
  typeRef: string
) {
  const factMappingsToReturn: FactMapping[] = [];
  const currentItemDefinition = allItemDefinitionsMap.has(itemComponent?.typeRef?.__$$text ?? "")
    ? allItemDefinitionsMap.get(itemComponent?.typeRef?.__$$text ?? "")
    : itemComponent;

  if (!currentItemDefinition?.typeRef && currentItemDefinition?.itemComponent) {
    currentItemDefinition.itemComponent.forEach((nestedItemComponent) => {
      factMappingsToReturn.push(
        ...recursevlyNavigateItemComponent(
          allItemDefinitionsMap,
          columnWidth,
          [...expressionElements, itemComponent["@_name"]],
          expressionIdentifierType,
          nestedItemComponent,
          name,
          typeRef
        )
      );
    });
  } else {
    factMappingsToReturn.push({
      className: itemComponent?.["@_isCollection"] ? "java.util.List" : itemComponent.typeRef!.__$$text,
      columnWidth: columnWidth,
      expressionAlias: [...expressionElements.slice(1), itemComponent["@_name"]].join("."),
      expressionElements: [...expressionElements, itemComponent["@_name"]],
      expressionIdentifierType: expressionIdentifierType,
      factAlias: name,
      factIdentifierName: name,
      factIdentifierClassName: typeRef,
      genericTypes: itemComponent?.["@_isCollection"] ? [itemComponent!.typeRef!.__$$text] : undefined,
    });
  }

  return factMappingsToReturn;
}

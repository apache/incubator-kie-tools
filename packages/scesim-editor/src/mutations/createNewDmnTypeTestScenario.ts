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
      dmnModel.model.definitions.itemDefinition!.map(
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

function isSimpleType(type: string) {
  return [
    "Any",
    "boolean",
    "context",
    "date",
    "date and time",
    "days and time duration",
    "number",
    "string",
    "time",
    "years and months duration",
    "<Undefined>",
  ].includes(type);
}

function generateFactMappingsAndFactMappingValuesFromDmnModel(
  drgElements: DMN15__tInputData[] | DMN15__tDecision[],
  expressionIdentifierType: "EXPECT" | "GIVEN",
  itemDefinitionMap: Map<string, DMN15__tItemDefinition>
) {
  const factMappingsToPush = [] as FactMapping[];

  drgElements.forEach((inputDataElement) => {
    if (isSimpleType(inputDataElement.variable!["@_typeRef"]!)) {
      factMappingsToPush.push(
        generateSimpleTypeFactMapping(
          inputDataElement.variable!["@_typeRef"]!,
          100,
          [inputDataElement.variable!["@_name"]!],
          expressionIdentifierType,
          inputDataElement.variable!["@_name"]!,
          inputDataElement.variable!["@_typeRef"]!
        )
      );
    } else {
      const itemDefinition = itemDefinitionMap.get(inputDataElement.variable!["@_typeRef"]!);
      if (itemDefinition?.typeRef && isSimpleType(itemDefinition?.typeRef?.__$$text)) {
        generateSimpleTypeFactMapping(
          itemDefinition?.typeRef?.__$$text,
          100,
          [inputDataElement.variable!["@_name"]!],
          expressionIdentifierType,
          inputDataElement.variable!["@_name"]!,
          inputDataElement.variable!["@_typeRef"]!
        );
      } else {
        itemDefinition?.itemComponent!.forEach((itemComponent) => {
          recursevlyNavigateItemComponent(
            100,
            factMappingsToPush,
            [inputDataElement.variable!["@_name"]!],
            expressionIdentifierType,
            itemComponent,
            inputDataElement.variable!["@_name"]!,
            inputDataElement.variable!["@_typeRef"]!
          );
        });
      }
    }
  });
  return factMappingsToPush;
}

function generateSimpleTypeFactMapping(
  className: string,
  columnWidth: number,
  expressionElements: string[],
  expressionIdentifierType: "EXPECT" | "GIVEN",
  name: string,
  typeRef: string
) {
  return {
    className,
    columnWidth,
    expressionAlias: "value",
    expressionElements: expressionElements,
    expressionIdentifierType,
    factAlias: name,
    factIdentifierName: name,
    factIdentifierClassName: typeRef,
  };
}

function recursevlyNavigateItemComponent(
  columnWidth: number,
  factMappingsToReturn: FactMapping[],
  expressionElements: string[],
  expressionIdentifierType: "EXPECT" | "GIVEN",
  itemComponent: DMN15__tItemDefinition,
  name: string,
  typeRef: string
) {
  if (!itemComponent.typeRef && itemComponent.itemComponent) {
    itemComponent.itemComponent.forEach((nestedItemComponent) => {
      recursevlyNavigateItemComponent(
        columnWidth,
        factMappingsToReturn,
        [...expressionElements, itemComponent["@_name"]],
        expressionIdentifierType,
        nestedItemComponent,
        name,
        typeRef
      );
    });
  } else {
    factMappingsToReturn.push({
      className: itemComponent.typeRef!.__$$text,
      columnWidth: columnWidth,
      expressionAlias: [...expressionElements.slice(1), itemComponent["@_name"]].join("."),
      expressionElements: [...expressionElements, itemComponent["@_name"]],
      expressionIdentifierType: expressionIdentifierType,
      factAlias: name,
      factIdentifierName: name,
      factIdentifierClassName: typeRef,
    });
  }
}

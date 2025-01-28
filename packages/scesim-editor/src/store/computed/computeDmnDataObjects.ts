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
  DMN15__tDecision,
  DMN15__tInputData,
  DMN15__tItemDefinition,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";

import { ExternalDmn } from "../../TestScenarioEditor";
import { TestScenarioDataObject } from "../TestScenarioEditorStore";

export function computeDmnDataObjects(
  externalDmnModel: ExternalDmn
  //settings: State["scesim"]["model"]["ScenarioSimulationModel"]["settings"]
): TestScenarioDataObject[] {
  const dataObjects: TestScenarioDataObject[] = [];

  if (externalDmnModel?.model) {
    const itemDefinitions = new Map(
      externalDmnModel.model.definitions.itemDefinition!.map(
        (itemDefinition) => [itemDefinition["@_name"], itemDefinition] as const
      )
    );

    const inputDataElements = externalDmnModel.model.definitions.drgElement!.filter(
      (drgElement) => drgElement.__$$element === "inputData"
    );
    const decisionElements = externalDmnModel.model.definitions.drgElement!.filter(
      (drgElement) => drgElement.__$$element === "decision"
    );

    const inpuDataObjects = inputDataElements.map((inputDataElement) =>
      createTestScenarioObjects(inputDataElement, itemDefinitions)
    );
    const decisionDataObjects = decisionElements.map((decisionElement) =>
      createTestScenarioObjects(decisionElement, itemDefinitions)
    );
    dataObjects.push(...inpuDataObjects, ...decisionDataObjects);
  }

  return dataObjects.sort((dataObjectA, dataObjectB) => dataObjectA.name.localeCompare(dataObjectB.name));
}

function createTestScenarioObjects(
  drgElement: DMN15__tInputData | DMN15__tDecision,
  itemDefinitionMap: Map<string, DMN15__tItemDefinition>
): TestScenarioDataObject {
  const children: TestScenarioDataObject[] = [];

  const drgElementName = drgElement["@_name"];
  const drgElementTypeRef = drgElement!.variable?.["@_typeRef"];
  const isDrgElementSimpleType = isSimpleType(drgElementTypeRef!);

  if (isDrgElementSimpleType) {
    children.push({
      id: drgElementName.concat("."), //TO BE REVIEWED IN https://github.com/apache/incubator-kie-issues/issues/1514
      name: "value",
      customBadgeContent: drgElementTypeRef,
      isSimpleTypeFact: isDrgElementSimpleType,
    });
  } else {
    const itemDefinition = itemDefinitionMap.get(drgElementTypeRef!);
    children.push(...createChildrenTestScenarioObjects(itemDefinition!, drgElementName, drgElementTypeRef!));
  }

  return {
    id: drgElementName,
    name: drgElementName,
    customBadgeContent: drgElementTypeRef,
    children: children,
  };
}

function createChildrenTestScenarioObjects(
  itemDefinition: DMN15__tItemDefinition,
  drgElementName: string,
  drgElementTypeRef: string
) {
  const children: TestScenarioDataObject[] = [];

  if (itemDefinition.itemComponent && itemDefinition.itemComponent.length > 0) {
    const childrenTestScenarioObjects = itemDefinition.itemComponent.map((itemComponent) => {
      const nestedChildren: TestScenarioDataObject[] = [];

      if (!itemComponent.typeRef) {
        const ns = createChildrenTestScenarioObjects(
          itemComponent,
          drgElementName.concat(".").concat(itemComponent["@_name"]),
          "?"
        );
        nestedChildren.push(...ns);
      }

      return {
        customBadgeContent: itemComponent.typeRef?.__$$text,
        children: nestedChildren.length > 0 ? nestedChildren : undefined,
        id: drgElementName.concat(".").concat(itemComponent["@_name"]),
        isCollection: itemComponent["@_isCollection"],
        name: itemComponent["@_name"],
      };
    });
    children.push(...childrenTestScenarioObjects);
  } else {
    children.push({
      customBadgeContent: drgElementTypeRef,
      id: drgElementName.concat("."), //TO BE REVIEWED IN https://github.com/apache/incubator-kie-issues/issues/1514
      //isCollection: itemComponent["@_isCollection"],
      name: "value",
      //isSimpleTypeFact: isDrgElementSimpleType, TO DOUBLE CHECK
    });
  }

  return children.sort((childA, childB) => childA.name.localeCompare(childB.name));
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

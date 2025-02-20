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

import { ExternalDmnsIndex } from "../../TestScenarioEditor";
import { State, TestScenarioDataObject } from "../TestScenarioEditorStore";

export function computeDmnDataObjects(
  externalModelsByNamespace: ExternalDmnsIndex | undefined,
  settings: State["scesim"]["model"]["ScenarioSimulationModel"]["settings"]
): TestScenarioDataObject[] {
  const dataObjects: TestScenarioDataObject[] = [];

  if (settings.type?.__$$text !== "DMN") {
    return dataObjects;
  }

  const dmnModel = externalModelsByNamespace?.get(settings.dmnNamespace!.__$$text);

  /* CHECKS external DMN */

  if (dmnModel) {
    const itemDefinitions = new Map(
      dmnModel.model.definitions.itemDefinition?.map(
        (itemDefinition) => [itemDefinition["@_name"], itemDefinition] as const
      )
    );

    const inputDataElements = dmnModel.model.definitions.drgElement?.filter(
      (drgElement) => drgElement.__$$element === "inputData"
    );
    const decisionElements = dmnModel.model.definitions.drgElement?.filter(
      (drgElement) => drgElement.__$$element === "decision"
    );

    const inpuDataObjects = inputDataElements?.map((inputDataElement) =>
      createTestScenarioObjects(inputDataElement, itemDefinitions)
    );
    const decisionDataObjects = decisionElements?.map((decisionElement) =>
      createTestScenarioObjects(decisionElement, itemDefinitions)
    );
    dataObjects.push(...(inpuDataObjects ?? []), ...(decisionDataObjects ?? []));
  }

  return dataObjects.sort((dataObjectA, dataObjectB) => dataObjectA.name.localeCompare(dataObjectB.name));
}

function createTestScenarioObjects(
  drgElement: DMN15__tInputData | DMN15__tDecision,
  itemDefinitionMap: Map<string, DMN15__tItemDefinition>
): TestScenarioDataObject {
  const drgElementName = drgElement["@_name"];
  const drgElementTypeRef = drgElement!.variable?.["@_typeRef"] ?? "<Undefined>";
  const itemDefinition = itemDefinitionMap.get(drgElementTypeRef!);

  return {
    id: drgElementName,
    name: drgElementName,
    children: createChildrenTestScenarioObjects(itemDefinition, [drgElementName], drgElementTypeRef!),
    className: drgElementTypeRef!,
    customBadgeContent: drgElementTypeRef,
    expressionElements: [drgElementName],
  };
}

function createChildrenTestScenarioObjects(
  itemDefinition: DMN15__tItemDefinition | undefined,
  expressionElements: string[],
  rootDrgElementTypeRef: string
) {
  const children: TestScenarioDataObject[] = [];

  if (itemDefinition?.itemComponent && itemDefinition.itemComponent.length > 0) {
    const childrenTestScenarioObjects = itemDefinition.itemComponent.map((itemComponent) => {
      const nestedChildren: TestScenarioDataObject[] = [];

      if (!itemComponent.typeRef) {
        const ns = createChildrenTestScenarioObjects(
          itemComponent,
          [...expressionElements, itemComponent["@_name"]],
          rootDrgElementTypeRef
        );
        nestedChildren.push(...ns);
      }

      const isCollection = itemComponent["@_isCollection"] ?? false;
      const name = itemComponent["@_name"];

      return {
        id: [...expressionElements, name].join("."),
        name: name,
        children: nestedChildren.length > 0 ? nestedChildren : undefined,
        className: isCollection ? "java.util.List" : itemComponent.typeRef?.__$$text,
        collectionGenericType: isCollection ? [itemComponent.typeRef!.__$$text] : undefined,
        customBadgeContent: `${itemComponent.typeRef?.__$$text}${isCollection ? "[]" : ""}`,
        expressionElements: [...expressionElements, name],
      };
    });
    children.push(...childrenTestScenarioObjects);
  } else {
    const isCollection = itemDefinition?.["@_isCollection"] ?? false;
    const name = isCollection ? "values" : "value";

    children.push({
      id: [...expressionElements, name].join("."),
      name: name,
      className: isCollection ? "java.util.List" : rootDrgElementTypeRef,
      collectionGenericType: isCollection ? [rootDrgElementTypeRef] : undefined,
      customBadgeContent: `${rootDrgElementTypeRef}${isCollection ? "[]" : ""}`,
      expressionElements: expressionElements,
    });
  }

  return children.sort((childA, childB) => childA.name.localeCompare(childB.name));
}

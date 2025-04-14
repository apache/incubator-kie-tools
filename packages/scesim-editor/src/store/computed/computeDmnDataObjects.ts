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

  if (dmnModel) {
    const allItemDefinitionsMap = new Map(
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
      createTestScenarioObjects(inputDataElement, allItemDefinitionsMap)
    );
    const decisionDataObjects = decisionElements?.map((decisionElement) =>
      createTestScenarioObjects(decisionElement, allItemDefinitionsMap)
    );
    dataObjects.push(...(inpuDataObjects ?? []), ...(decisionDataObjects ?? []));
  }

  return dataObjects.sort((dataObjectA, dataObjectB) => dataObjectA.name.localeCompare(dataObjectB.name));
}

function createTestScenarioObjects(
  drgElement: DMN15__tInputData | DMN15__tDecision,
  allItemDefinitionsMap: Map<string, DMN15__tItemDefinition>
): TestScenarioDataObject {
  const drgElementName = drgElement["@_name"];
  const drgElementTypeRef = drgElement!.variable?.["@_typeRef"];
  const itemDefinition = drgElementTypeRef ? allItemDefinitionsMap.get(drgElementTypeRef) : undefined;

  return {
    id: drgElementName,
    name: drgElementName,
    children: createChildrenTestScenarioObjects(
      itemDefinition,
      allItemDefinitionsMap,
      [drgElementName],
      drgElementTypeRef
    ),
    className: drgElementTypeRef,
    customBadgeContent: drgElementTypeRef ?? "<Undefined>",
    expressionElements: [drgElementName],
  };
}

function createChildrenTestScenarioObjects(
  itemDefinition: DMN15__tItemDefinition | undefined,
  allItemDefinitionsMap: Map<string, DMN15__tItemDefinition>,
  expressionElements: string[],
  rootDrgElementTypeRef: string | undefined
) {
  const children: TestScenarioDataObject[] = [];

  if (itemDefinition?.itemComponent && itemDefinition.itemComponent.length > 0) {
    const childrenTestScenarioObjects = itemDefinition.itemComponent.map((itemComponent) => {
      const nestedChildren: TestScenarioDataObject[] = [];
      const currentItemDefinition = allItemDefinitionsMap.has(itemComponent?.typeRef?.__$$text ?? "")
        ? allItemDefinitionsMap.get(itemComponent?.typeRef?.__$$text ?? "")
        : itemComponent;
      const isCollection = itemComponent["@_isCollection"] ?? false;

      if (!currentItemDefinition?.typeRef && !isCollection) {
        const ns = createChildrenTestScenarioObjects(
          currentItemDefinition,
          allItemDefinitionsMap,
          [...expressionElements, itemComponent["@_name"]],
          rootDrgElementTypeRef
        );
        nestedChildren.push(...ns);
      }

      const name = itemComponent["@_name"];
      const className = isCollection ? "java.util.List" : itemComponent.typeRef?.__$$text;

      return {
        id: [...expressionElements, name].join("."),
        hasBadge: !(className === undefined && nestedChildren.length > 0),
        name: name,
        children: nestedChildren.length > 0 ? nestedChildren : undefined,
        className: className,
        collectionGenericType: isCollection ? [itemComponent.typeRef!.__$$text] : undefined,
        customBadgeContent: `${itemComponent.typeRef?.__$$text ?? "<Undefined>"}${isCollection ? "[]" : ""}`,
        expressionElements: [...expressionElements, name],
      };
    });
    children.push(...childrenTestScenarioObjects);
  } else {
    const isCollection = itemDefinition?.["@_isCollection"] ?? false;
    const name = "value";

    children.push({
      id: [...expressionElements, name].join("."),
      name: name,
      className: isCollection ? "java.util.List" : rootDrgElementTypeRef,
      collectionGenericType: isCollection && rootDrgElementTypeRef ? [rootDrgElementTypeRef] : undefined,
      customBadgeContent: `${rootDrgElementTypeRef ?? "<Undefined>"}${isCollection && rootDrgElementTypeRef ? "[]" : ""}`,
      expressionElements: expressionElements,
    });
  }

  return children.sort((childA, childB) => childA.name.localeCompare(childB.name));
}

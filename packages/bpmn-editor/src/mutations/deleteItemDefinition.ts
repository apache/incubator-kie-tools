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

import { BPMN20__tDefinitions } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Normalized } from "../normalization/normalize";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";
import { RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES } from "./addOrGetMessages";
import { DEFAULT_DATA_TYPES } from "./addOrGetItemDefinitions";

export function deleteItemDefinition({
  definitions,
  itemDefinition,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  itemDefinition: string;
}) {
  const existingItemDefinitionIndex = definitions.rootElement?.findIndex(
    (s) => s.__$$element === "itemDefinition" && s["@_structureRef"] === itemDefinition
  );
  if (existingItemDefinitionIndex === undefined || existingItemDefinitionIndex < 0) {
    throw new Error(`BPMN MUTATION: Item definition ${itemDefinition} is not in the model`);
  }
  const itemDefinitionId = definitions.rootElement?.[existingItemDefinitionIndex]?.["@_id"];

  // Delete from root element
  definitions.rootElement?.splice(existingItemDefinitionIndex, 1);

  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  // Delete from property
  process.property
    ?.filter((property) => property["@_itemSubjectRef"] === itemDefinitionId)
    .forEach((property) => {
      delete property["@_itemSubjectRef"];
    });

  // Delete from all flow elements
  visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
    if (array != owner.flowElement) {
      throw new Error(
        `BPMN MUTATION: Element with id ${itemDefinitionId} is not a flowElement, but rather a ${element.__$$element}`
      );
    }

    if (
      array[index].__$$element === "businessRuleTask" ||
      array[index].__$$element === "callActivity" ||
      array[index].__$$element === "serviceTask" ||
      array[index].__$$element === "userTask"
    ) {
      for (const dataInput of array[index]?.ioSpecification?.dataInput ?? []) {
        if (dataInput && dataInput?.["@_itemSubjectRef"] === itemDefinitionId) {
          delete dataInput["@_itemSubjectRef"];
          delete dataInput["@_drools:dtype"];
        }
      }
      for (const dataOutput of array[index]?.ioSpecification?.dataOutput ?? []) {
        if (dataOutput && dataOutput?.["@_itemSubjectRef"] === itemDefinitionId) {
          delete dataOutput["@_itemSubjectRef"];
          delete dataOutput["@_drools:dtype"];
        }
      }
    }

    if (array[index].__$$element === "endEvent" || array[index].__$$element === "intermediateThrowEvent") {
      for (const dataInput of array[index]?.dataInput ?? []) {
        if (dataInput && dataInput?.["@_itemSubjectRef"] === itemDefinitionId) {
          delete dataInput["@_itemSubjectRef"];
          delete dataInput["@_drools:dtype"];
        }
      }
    }

    if (array[index].__$$element === "startEvent" || array[index].__$$element === "intermediateCatchEvent") {
      for (const dataOutput of array[index]?.dataOutput ?? []) {
        if (dataOutput && dataOutput?.["@_itemSubjectRef"] === itemDefinitionId) {
          delete dataOutput["@_itemSubjectRef"];
          delete dataOutput["@_drools:dtype"];
        }
      }
    }
  });

  definitions.rootElement
    ?.filter((e) => e.__$$element === "message" && e["@_itemRef"] === itemDefinitionId)
    .forEach((message) => {
      if (message.__$$element === "message") {
        message["@_itemRef"] = RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES;
      }
    });

  definitions.rootElement
    ?.filter((e) => e.__$$element === "correlationProperty" && e["@_type"] === itemDefinitionId)
    .forEach((property) => {
      if (property.__$$element === "correlationProperty") {
        property["@_type"] = undefined;
      }
    });
}

export function deleteUnusedItemDefinitions({ definitions }: { definitions: Normalized<BPMN20__tDefinitions> }) {
  definitions.rootElement ??= [];

  const defaultDataTypes = new Set(Object.values(DEFAULT_DATA_TYPES));

  const defaultItemDefinitions = definitions.rootElement.filter(
    (e) =>
      e.__$$element === "itemDefinition" &&
      e["@_id"] &&
      e["@_id"] !== RESERVED_ITEM_DEFINITION_ID_FOR_MESSAGES &&
      e["@_structureRef"] &&
      defaultDataTypes.has(e["@_structureRef"])
  );

  if (defaultItemDefinitions.length === 0) {
    return;
  }

  const defaultItemDefinitionIds = new Set(
    defaultItemDefinitions.map((def) => def["@_id"]).filter((id): id is string => !!id)
  );
  const usedItemDefinitionIds = new Set<string>();

  const addItemDefinitionIfUsed = (id: string | undefined) => {
    if (id && defaultItemDefinitionIds.has(id)) {
      usedItemDefinitionIds.add(id);
    }
  };

  definitions.rootElement.forEach((element) => {
    if (element.__$$element === "message") {
      addItemDefinitionIfUsed(element["@_itemRef"]);
    } else if (element.__$$element === "correlationProperty") {
      addItemDefinitionIfUsed(element["@_type"]);
    } else if (element.__$$element === "dataStore") {
      addItemDefinitionIfUsed(element["@_itemSubjectRef"]);
    }
  });

  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  process.property?.forEach((property) => {
    addItemDefinitionIfUsed(property["@_itemSubjectRef"]);
  });

  visitFlowElementsAndArtifacts(process, ({ array, index }) => {
    const element = array[index];

    if (element.__$$element === "dataObject") {
      addItemDefinitionIfUsed(element["@_itemSubjectRef"]);
    } else if (element.__$$element === "dataObjectReference") {
      addItemDefinitionIfUsed(element["@_itemSubjectRef"]);
    } else if (element.__$$element === "dataStoreReference") {
      addItemDefinitionIfUsed(element["@_itemSubjectRef"]);
    } else if (
      element.__$$element === "businessRuleTask" ||
      element.__$$element === "callActivity" ||
      element.__$$element === "serviceTask" ||
      element.__$$element === "userTask"
    ) {
      element.ioSpecification?.dataInput?.forEach((dataInput) => {
        addItemDefinitionIfUsed(dataInput["@_itemSubjectRef"]);
      });
      element.ioSpecification?.dataOutput?.forEach((dataOutput) => {
        addItemDefinitionIfUsed(dataOutput["@_itemSubjectRef"]);
      });
    } else if (element.__$$element === "endEvent" || element.__$$element === "intermediateThrowEvent") {
      element.dataInput?.forEach((dataInput) => {
        addItemDefinitionIfUsed(dataInput["@_itemSubjectRef"]);
      });
    } else if (
      element.__$$element === "startEvent" ||
      element.__$$element === "intermediateCatchEvent" ||
      element.__$$element === "boundaryEvent"
    ) {
      element.dataOutput?.forEach((dataOutput) => {
        addItemDefinitionIfUsed(dataOutput["@_itemSubjectRef"]);
      });
    }
  });

  definitions.rootElement = definitions.rootElement.filter((e) => {
    if (e.__$$element === "itemDefinition" && e["@_id"] && defaultItemDefinitionIds.has(e["@_id"])) {
      return usedItemDefinitionIds.has(e["@_id"]);
    }
    return true;
  });
}

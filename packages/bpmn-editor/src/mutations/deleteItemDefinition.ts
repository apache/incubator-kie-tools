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
}

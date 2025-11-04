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
import { ElementFilter } from "@kie-tools/xml-parser-ts/dist/elementFilter";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";
import { Normalized } from "../normalization/normalize";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function renameItemDefinition({
  definitions,
  id,
  newItemDefinitionName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  id: string;
  newItemDefinitionName: string;
}):
  | {
      itemDefinition: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "itemDefinition">;
    }
  | undefined {
  if (definitions.rootElement === undefined) {
    throw new Error(`BPMN MUTATION: Model without root element`);
  }
  const existingItemDefinitionIndex = definitions.rootElement?.findIndex((s) => s["@_id"] === id);

  if (existingItemDefinitionIndex === undefined || existingItemDefinitionIndex < 0) {
    throw new Error(`BPMN MUTATION: Item definition with id ${id} is not in the model`);
  }

  // Rename item definition
  const itemDefinition = definitions.rootElement[existingItemDefinitionIndex] as ElementFilter<
    Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>,
    "itemDefinition"
  >;
  itemDefinition["@_structureRef"] = newItemDefinitionName;

  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  // Rename on all flow elements
  visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
    if (array != owner.flowElement) {
      throw new Error(`BPMN MUTATION: Element with id ${id} is not a flowElement, but rather a ${element.__$$element}`);
    }

    if (
      array[index].__$$element === "businessRuleTask" ||
      array[index].__$$element === "callActivity" ||
      array[index].__$$element === "serviceTask" ||
      array[index].__$$element === "userTask"
    ) {
      for (const dataInput of array[index]?.ioSpecification?.dataInput ?? []) {
        if (dataInput && dataInput?.["@_itemSubjectRef"] === id) {
          dataInput["@_drools:dtype"] = newItemDefinitionName;
        }
      }
      for (const dataOutput of array[index]?.ioSpecification?.dataOutput ?? []) {
        if (dataOutput && dataOutput?.["@_itemSubjectRef"] === id) {
          dataOutput["@_drools:dtype"] = newItemDefinitionName;
        }
      }
    }

    if (array[index].__$$element === "endEvent" || array[index].__$$element === "intermediateThrowEvent") {
      for (const dataInput of array[index]?.dataInput ?? []) {
        if (dataInput && dataInput?.["@_itemSubjectRef"] === id) {
          dataInput["@_drools:dtype"] = newItemDefinitionName;
        }
      }
    }

    if (array[index].__$$element === "startEvent" || array[index].__$$element === "intermediateCatchEvent") {
      for (const dataOutput of array[index]?.dataOutput ?? []) {
        if (dataOutput && dataOutput?.["@_itemSubjectRef"] === id) {
          dataOutput["@_drools:dtype"] = newItemDefinitionName;
        }
      }
    }
  });

  return { itemDefinition };
}

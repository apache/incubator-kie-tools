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

export function renameEscalation({
  definitions,
  id,
  newEscalationName,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  id: string;
  newEscalationName: string;
}):
  | {
      escalation: ElementFilter<Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>, "escalation">;
    }
  | undefined {
  if (definitions.rootElement === undefined) {
    throw new Error(`BPMN MUTATION: Model without root element`);
  }
  const existingEscalationIndex = definitions.rootElement?.findIndex((s) => s["@_id"] === id);
  if (existingEscalationIndex === undefined || existingEscalationIndex < 0) {
    throw new Error(`BPMN MUTATION: Escalation with id ${id} is not in the model`);
  }

  // Rename escalation name
  const escalation = definitions.rootElement[existingEscalationIndex] as ElementFilter<
    Unpacked<Normalized<BPMN20__tDefinitions["rootElement"]>>,
    "escalation"
  >;
  escalation["@_name"] = newEscalationName;
  escalation["@_escalationCode"] = newEscalationName;
  escalation["@_structureRef"] = `${newEscalationName}Type`;

  const { process } = addOrGetProcessAndDiagramElements({ definitions });

  // Rename on all flow elements
  visitFlowElementsAndArtifacts(process, ({ array, index, owner, element }) => {
    if (array != owner.flowElement) {
      throw new Error(`BPMN MUTATION: Element with id ${id} is not a flowElement, but rather a ${element.__$$element}`);
    }

    if (
      array[index].__$$element === "endEvent" ||
      array[index].__$$element === "intermediateThrowEvent" ||
      array[index].__$$element === "intermediateCatchEvent"
    ) {
      for (const eventDefinition of array[index]?.eventDefinition ?? []) {
        if (eventDefinition && eventDefinition.__$$element === "escalationEventDefinition") {
          eventDefinition["@_drools:esccode"] = newEscalationName;
        }
      }
    }
  });

  return { escalation };
}

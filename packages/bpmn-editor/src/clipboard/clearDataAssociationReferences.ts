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

import { Normalized } from "../normalization/normalize";
import {
  BPMN20__tProcess,
  BPMN20__tDataInputAssociation,
  BPMN20__tDataOutputAssociation,
} from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";
import { Unpacked } from "@kie-tools/xyflow-react-kie-diagram/dist/tsExt/tsExt";

type FlowElement = NonNullable<Unpacked<Normalized<BPMN20__tProcess>["flowElement"]>>;

export function clearDataAssociationReferences(flowElements: FlowElement[]): void {
  for (let i = 0; i < flowElements.length; i++) {
    clearDataAssociationReferencesFromElement(flowElements[i]);
  }
}

function clearDataAssociationReferencesFromElement(element: FlowElement): void {
  if (
    element.__$$element === "callActivity" ||
    element.__$$element === "businessRuleTask" ||
    element.__$$element === "userTask" ||
    element.__$$element === "task" ||
    element.__$$element === "serviceTask" ||
    element.__$$element === "scriptTask" ||
    element.__$$element === "subProcess" ||
    element.__$$element === "adHocSubProcess"
  ) {
    clearReferences(element.dataInputAssociation);
    clearReferences(element.dataOutputAssociation);
  }

  if (element.__$$element === "endEvent" || element.__$$element === "intermediateThrowEvent") {
    clearReferences(element.dataInputAssociation);
  }

  if (
    element.__$$element === "startEvent" ||
    element.__$$element === "intermediateCatchEvent" ||
    element.__$$element === "boundaryEvent"
  ) {
    clearReferences(element.dataOutputAssociation);
  }

  if (
    element.__$$element === "subProcess" ||
    element.__$$element === "adHocSubProcess" ||
    element.__$$element === "transaction"
  ) {
    element.flowElement ??= [];

    for (let i = 0; i < element.flowElement.length; i++) {
      clearDataAssociationReferencesFromElement(element.flowElement[i]);
    }
  }
}

function clearReferences(
  associations?: Partial<BPMN20__tDataInputAssociation | BPMN20__tDataOutputAssociation>[]
): void {
  if (!associations) {
    return;
  }

  for (let i = 0; i < associations.length; i++) {
    delete associations[i].sourceRef;
    delete associations[i].targetRef;
  }
}

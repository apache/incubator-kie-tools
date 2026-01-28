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
import { generateUuid } from "@kie-tools/xyflow-react-kie-diagram/dist/uuid/uuid";
import { visitFlowElementsAndArtifacts } from "./_elementVisitor";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function addVariable({
  definitions,
  pId,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  pId: string | undefined;
}): void {
  if (pId === undefined) {
    throw new Error("BPMN MUTATION: Can't add a variable to a Process without an id.");
  }

  const { process } = addOrGetProcessAndDiagramElements({ definitions });
  if (pId === process["@_id"]) {
    process.property ??= [];
    process.property?.push({
      "@_id": generateUuid(),
      "@_name": "",
      "@_itemSubjectRef": "",
    });
  } else {
    visitFlowElementsAndArtifacts(process, ({ element }) => {
      if (
        element["@_id"] === pId &&
        (element.__$$element === "subProcess" ||
          element.__$$element === "adHocSubProcess" ||
          element.__$$element === "transaction")
      ) {
        element.property ??= [];
        element.property?.push({
          "@_id": generateUuid(),
          "@_name": "",
          "@_itemSubjectRef": "",
        });
        return false; // Will stop visiting.
      }
    });
  }
}

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
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function deleteEdgeWaypoint({
  definitions,
  __readonly_edgeIndex,
  __readonly_waypointIndex,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_edgeIndex: number;
  __readonly_waypointIndex: number;
}) {
  const { diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  const diagramElement = diagramElements[__readonly_edgeIndex];
  if (diagramElement.__$$element !== "bpmndi:BPMNEdge") {
    throw new Error("BPMN MUTATION: Can't remove a waypoint from an element that is not a BPMNEdge.");
  }

  if (__readonly_waypointIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `BPMN MUTATION: Can't remove waypoint with index '${__readonly_waypointIndex}' from BPMNEdge '${diagramElement["@_id"]}' because it doesn't exist.`
    );
  }

  diagramElement["di:waypoint"]!.splice(__readonly_waypointIndex, 1);
}

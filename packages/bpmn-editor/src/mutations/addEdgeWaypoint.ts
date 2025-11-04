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
import { DC__Point } from "@kie-tools/xyflow-react-kie-diagram/dist/maths/model";
import { Normalized } from "../normalization/normalize";
import { addOrGetProcessAndDiagramElements } from "./addOrGetProcessAndDiagramElements";

export function addEdgeWaypoint({
  definitions,
  __readonly_edgeIndex,
  __readonly_beforeIndex,
  __readonly_waypoint,
}: {
  definitions: Normalized<BPMN20__tDefinitions>;
  __readonly_edgeIndex: number;
  __readonly_beforeIndex: number;
  __readonly_waypoint: DC__Point;
}) {
  const { diagramElements } = addOrGetProcessAndDiagramElements({ definitions });

  const diagramElement = diagramElements[__readonly_edgeIndex];
  if (diagramElement.__$$element !== "bpmndi:BPMNEdge") {
    throw new Error("DMN MUTATION: Can't remove a waypoint from an element that is not a DMNEdge.");
  }

  if (__readonly_beforeIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `DMN MUTATION: Can't add waypoint before index '${__readonly_beforeIndex}' to DMNEdge '${diagramElement["@_id"]}' because the waypoint array is smaller than 'beforeIndex' requires.`
    );
  }

  diagramElement["di:waypoint"]!.splice(__readonly_beforeIndex, 0, __readonly_waypoint);
}

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

import { DMN15__tDefinitions } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { addOrGetDrd } from "./addOrGetDrd";

export function deleteEdgeWaypoint({
  definitions,
  drdIndex,
  edgeIndex,
  waypointIndex,
}: {
  definitions: DMN15__tDefinitions;
  drdIndex: number;
  edgeIndex: number;
  waypointIndex: number;
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const diagramElement = diagramElements[edgeIndex];
  if (diagramElement.__$$element !== "dmndi:DMNEdge") {
    throw new Error("DMN MUTATION: Can't remove a waypoint from an element that is not a DMNEdge.");
  }

  if (waypointIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `DMN MUTATION: Can't remove waypoint with index '${waypointIndex}' from DMNEdge '${diagramElement["@_id"]}' because it doesn't exist.`
    );
  }

  diagramElement["di:waypoint"]!.splice(waypointIndex, 1);
}

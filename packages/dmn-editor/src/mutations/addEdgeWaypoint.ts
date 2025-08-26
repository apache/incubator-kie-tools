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

import { DC__Point } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_6/ts-gen/types";
import { DMN_LATEST__tDefinitions } from "@kie-tools/dmn-marshaller";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { addOrGetDrd } from "./addOrGetDrd";

export function addEdgeWaypoint({
  definitions,
  drdIndex,
  dmnEdgeIndex,
  beforeIndex,
  waypoint,
}: {
  definitions: Normalized<DMN_LATEST__tDefinitions>;
  drdIndex: number;
  dmnEdgeIndex: number;
  beforeIndex: number;
  waypoint: DC__Point;
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const diagramElement = diagramElements[dmnEdgeIndex];
  if (diagramElement.__$$element !== "dmndi:DMNEdge") {
    throw new Error("DMN MUTATION: Can't add a waypoint for an element that is not a DMNEdge.");
  }

  if (beforeIndex > (diagramElement["di:waypoint"]?.length ?? 0) - 1) {
    throw new Error(
      `DMN MUTATION: Can't add waypoint before index '${beforeIndex}' to DMNEdge '${diagramElement["@_id"]}' because the waypoint array is smaller than 'beforeIndex' requires.`
    );
  }

  diagramElement["di:waypoint"]!.splice(beforeIndex, 0, waypoint);
}

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
import { BPMNDI__BPMNShape, BPMNDI__BPMNEdge } from "@kie-tools/bpmn-marshaller/dist/schemas/bpmn-2_0/ts-gen/types";

const DEFAULT_PASTE_OFFSET_X = 30;
const DEFAULT_PASTE_OFFSET_Y = 30;

export function offsetPastedNodes(
  shapes: Normalized<BPMNDI__BPMNShape>[],
  edges: Normalized<BPMNDI__BPMNEdge>[],
  offsetX: number = DEFAULT_PASTE_OFFSET_X,
  offsetY: number = DEFAULT_PASTE_OFFSET_Y
): void {
  for (let i = 0; i < shapes.length; i++) {
    const shape = shapes[i];
    if (shape["dc:Bounds"]) {
      const bounds = shape["dc:Bounds"];
      bounds["@_x"] += offsetX;
      bounds["@_y"] += offsetY;
    }
  }

  for (let i = 0; i < edges.length; i++) {
    const edge = edges[i];
    for (let j = 0; j < edge["di:waypoint"].length; j++) {
      const waypoint = edge["di:waypoint"][j];
      waypoint["@_x"] += offsetX;
      waypoint["@_y"] += offsetY;
    }
  }
}

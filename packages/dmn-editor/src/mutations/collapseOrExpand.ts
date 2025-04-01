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

import { DMN15__tDefinitions, DMNDI15__DMNShape } from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { addOrGetDrd, addOrGetDrd as getDefaultDiagram } from "./addOrGetDrd";
import { DECISION_SERVICE_COLLAPSED_DIMENSIONS } from "../diagram/nodes/DefaultSizes";
import { CurrentBound } from "../store/Store";

export function collapseOrExpand({
  definitions,
  drdIndex,
  collapse,
  shapeIndex,
  currentBound,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  drdIndex: number;
  collapse: boolean;
  shapeIndex: number;
  currentBound: CurrentBound[];
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });
  const shape = diagramElements?.[shapeIndex] as Normalized<DMNDI15__DMNShape> | undefined;

  if (shape !== undefined && shape !== null) {
    shape["@_isCollapsed"] = collapse;
    if (shape["dc:Bounds"]) {
      if (collapse) {
        (shape["dc:Bounds"]["@_width"] = DECISION_SERVICE_COLLAPSED_DIMENSIONS.width),
          (shape["dc:Bounds"]["@_height"] = DECISION_SERVICE_COLLAPSED_DIMENSIONS.height);
      } else {
        for (const bound of currentBound) {
          if (shapeIndex === bound.id) {
            (shape["dc:Bounds"]["@_width"] = bound.width), (shape["dc:Bounds"]["@_height"] = bound.height);
          }
        }
      }
    }
  }
}

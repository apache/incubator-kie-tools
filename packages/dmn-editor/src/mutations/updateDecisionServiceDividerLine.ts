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

import {
  DC__Bounds,
  DMN15__tDecisionService,
  DMN15__tDefinitions,
  DMNDI15__DMNDecisionServiceDividerLine,
  DMNDI15__DMNShape,
} from "@kie-tools/dmn-marshaller/dist/schemas/dmn-1_5/ts-gen/types";
import { Normalized } from "@kie-tools/dmn-marshaller/dist/normalization/normalize";
import { addNamespaceToHref } from "@kie-tools/dmn-marshaller/dist/xml/xmlHrefs";
import { addOrGetDrd } from "./addOrGetDrd";
import { snapShapeDimensions, snapShapePosition } from "../diagram/SnapGrid";
import { MIN_NODE_SIZES } from "../diagram/nodes/DefaultSizes";
import { SnapGrid } from "../store/Store";
import { NODE_TYPES } from "../diagram/nodes/NodeTypes";
import { generateUuid } from "@kie-tools/boxed-expression-component/dist/api";
import { ExternalDmnsIndex } from "../DmnEditor";
import { getDecisionServiceDividerLineLocalY } from "../diagram/maths/DmnMaths";
import { computeIndexedDrd } from "../store/computed/computeIndexes";

export const DECISION_SERVICE_DIVIDER_LINE_PADDING = 100;

export function updateDecisionServiceDividerLine({
  definitions,
  drdIndex,
  __readonly_dmnShapesByHref,
  __readonly_dmnObjectNamespace,
  __readonly_externalDmnsIndex,
  shapeIndex,
  localYPosition,
  drgElementIndex,
  snapGrid,
  __readonly_decisionServiceHref,
}: {
  definitions: Normalized<DMN15__tDefinitions>;
  drdIndex: number;
  __readonly_dmnShapesByHref: Map<string, Normalized<DMNDI15__DMNShape> & { index: number }>;
  __readonly_dmnObjectNamespace: string | undefined;
  __readonly_externalDmnsIndex: ExternalDmnsIndex;
  shapeIndex: number;
  localYPosition: number;
  drgElementIndex: number;
  snapGrid: SnapGrid;
  __readonly_decisionServiceHref: string;
}) {
  const { diagramElements } = addOrGetDrd({ definitions, drdIndex });

  const shape = diagramElements?.[shapeIndex] as Normalized<DMNDI15__DMNShape> | undefined;
  const shapeBounds = shape?.["dc:Bounds"];
  if (!shapeBounds) {
    throw new Error("DMN MUTATION: Cannot reposition divider line of non-existent shape bounds");
  }

  const externalDmn = __readonly_externalDmnsIndex.get(__readonly_dmnObjectNamespace ?? "");

  const ds =
    externalDmn === undefined
      ? (definitions.drgElement![drgElementIndex] as Normalized<DMN15__tDecisionService>)
      : (externalDmn.model.definitions.drgElement![drgElementIndex] as Normalized<DMN15__tDecisionService>);
  if (!ds) {
    throw new Error("DMN MUTATION: Cannot reposition divider line of non-existent Decision Service");
  }

  const decisionMinSizes = MIN_NODE_SIZES[NODE_TYPES.decision]({ snapGrid });
  const decisionServiceMinSizes = MIN_NODE_SIZES[NODE_TYPES.decisionService]({ snapGrid });

  const snappedPosition = snapShapePosition(snapGrid, shape);
  const snappedDimensions = snapShapeDimensions(snapGrid, shape, decisionServiceMinSizes);

  const upperLimit = (ds.outputDecision ?? []).reduce((acc, od) => {
    // For external Decision Services, the Output Decision will have the relative namespace. e.g. without namespace.
    const href =
      __readonly_dmnObjectNamespace !== undefined
        ? addNamespaceToHref({
            href: od["@_href"],
            namespace:
              definitions["@_namespace"] === __readonly_dmnObjectNamespace ? undefined : __readonly_dmnObjectNamespace,
          })
        : od["@_href"];
    const v =
      snapShapePosition(snapGrid, __readonly_dmnShapesByHref.get(href)!).y +
      snapShapeDimensions(snapGrid, __readonly_dmnShapesByHref.get(href)!, decisionMinSizes).height;
    return v > acc ? v : acc;
  }, snappedPosition.y + DECISION_SERVICE_DIVIDER_LINE_PADDING);

  const lowerLimit = (ds.encapsulatedDecision ?? []).reduce(
    (acc, ed) => {
      // For external Decision Services, the Encapsulated Decision will have the relative namespace. e.g. without namespace.
      const href =
        __readonly_dmnObjectNamespace !== undefined
          ? addNamespaceToHref({
              href: ed["@_href"],
              namespace:
                definitions["@_namespace"] === __readonly_dmnObjectNamespace
                  ? undefined
                  : __readonly_dmnObjectNamespace,
            })
          : ed["@_href"];
      const v = snapShapePosition(snapGrid, __readonly_dmnShapesByHref.get(href)!).y;
      return v < acc ? v : acc;
    },
    snappedPosition.y + snappedDimensions.height - DECISION_SERVICE_DIVIDER_LINE_PADDING
  );

  const newDividerLineYPosition = Math.max(upperLimit, Math.min(snappedPosition.y + localYPosition, lowerLimit));

  shape["dmndi:DMNDecisionServiceDividerLine"] ??= getCentralizedDecisionServiceDividerLine(shapeBounds);
  shape["dmndi:DMNDecisionServiceDividerLine"]["di:waypoint"]![0]["@_y"] = newDividerLineYPosition;
  shape["dmndi:DMNDecisionServiceDividerLine"]["di:waypoint"]![1]["@_y"] = newDividerLineYPosition;

  //Updating dividerline position in all DRDs that contain the decisions service
  const dividerLineLocalY = getDecisionServiceDividerLineLocalY(shape);
  const drds = definitions["dmndi:DMNDI"]?.["dmndi:DMNDiagram"] ?? [];
  for (let i = 0; i < drds.length; i++) {
    if (i === drdIndex) {
      continue;
    }
    const _indexedDrd = computeIndexedDrd(definitions["@_namespace"], definitions, i);
    const dsShape = _indexedDrd.dmnShapesByHref.get(__readonly_decisionServiceHref);
    const dsShapeYPosition = dsShape?.["dc:Bounds"]?.["@_y"];
    if (dsShape && dsShape["dmndi:DMNDecisionServiceDividerLine"]) {
      dsShape["dmndi:DMNDecisionServiceDividerLine"]!["di:waypoint"]![0]["@_y"] = dsShapeYPosition! + dividerLineLocalY;
      dsShape["dmndi:DMNDecisionServiceDividerLine"]!["di:waypoint"]![1]["@_y"] = dsShapeYPosition! + dividerLineLocalY;
    }
  }
}

export function getCentralizedDecisionServiceDividerLine(
  bounds: DC__Bounds
): Normalized<DMNDI15__DMNDecisionServiceDividerLine> {
  return {
    "@_id": generateUuid(),
    "di:waypoint": [
      { "@_x": bounds["@_x"], "@_y": bounds["@_y"] + bounds["@_height"] / 2 },
      {
        "@_x": bounds["@_x"] + bounds["@_height"],
        "@_y": bounds["@_y"] + bounds["@_height"] / 2,
      },
    ],
  };
}
